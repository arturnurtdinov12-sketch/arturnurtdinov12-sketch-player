package com.tgplayer.app.data.remote

import android.content.Context
import com.tgplayer.app.BuildConfig
import com.tgplayer.app.domain.model.AuthState
import com.tgplayer.app.domain.model.Channel
import com.tgplayer.app.domain.model.Track
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import org.drinkless.tdlib.Client
import org.drinkless.tdlib.TdApi
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Production TelegramClient backed by org.drinkless.tdlib.Client.
 *
 * The companion object only loads the native library when it is actually
 * present on the device — when running with the bundled stub, no .so is
 * required and all TDLib calls return Error gracefully.
 */
@Singleton
class TdLibTelegramClient @Inject constructor(
    @TelegramAppContext private val context: Context
) : TelegramClient {

    private val _authState = MutableStateFlow<AuthState>(AuthState.Initializing)
    override val authState: Flow<AuthState> = _authState.asStateFlow()

    private val _fileUpdates = MutableSharedFlow<FileDownloadUpdate>(extraBufferCapacity = 64)
    override val fileDownloadUpdates: Flow<FileDownloadUpdate> = _fileUpdates.asSharedFlow()

    @Volatile private var client: Client? = null

    override suspend fun start() {
        if (client != null) return
        tryLoadNative()
        val updateHandler = Client.ResultHandler { obj -> handleUpdate(obj) }
        val excHandler = Client.ExceptionHandler { /* swallow — surfaced via authState */ }
        client = Client.create(updateHandler, excHandler, excHandler)
        sendTdlibParameters()
    }

    private fun tryLoadNative() {
        runCatching { System.loadLibrary("tdjni") }
    }

    private fun sendTdlibParameters() {
        val params = TdApi.SetTdlibParameters().apply {
            databaseDirectory = File(context.filesDir, "td").absolutePath
            filesDirectory = File(context.filesDir, "td-files").absolutePath
            useFileDatabase = true
            useChatInfoDatabase = true
            useMessageDatabase = true
            useSecretChats = false
            apiId = BuildConfig.TD_API_ID
            apiHash = BuildConfig.TD_API_HASH
            systemLanguageCode = "en"
            deviceModel = "Android"
            applicationVersion = "1.0"
        }
        File(params.databaseDirectory).mkdirs()
        File(params.filesDirectory).mkdirs()
        client?.send(params) {}
    }

    private fun handleUpdate(obj: TdApi.Object) {
        when (obj) {
            is TdApi.UpdateAuthorizationState -> handleAuth(obj.authorizationState)
            is TdApi.UpdateFile -> emitFileUpdate(obj.file)
            else -> Unit
        }
    }

    private fun handleAuth(state: TdApi.AuthorizationState?) {
        _authState.value = when (state) {
            is TdApi.AuthorizationStateWaitTdlibParameters -> AuthState.Initializing
            is TdApi.AuthorizationStateWaitPhoneNumber -> AuthState.WaitingPhone
            is TdApi.AuthorizationStateWaitCode -> AuthState.WaitingCode
            is TdApi.AuthorizationStateWaitPassword -> AuthState.WaitingPassword
            is TdApi.AuthorizationStateReady -> AuthState.Ready
            is TdApi.AuthorizationStateLoggingOut, is TdApi.AuthorizationStateClosed -> AuthState.LoggedOut
            else -> _authState.value
        }
    }

    private fun emitFileUpdate(file: TdApi.File?) {
        if (file == null) return
        _fileUpdates.tryEmit(
            FileDownloadUpdate(
                fileId = file.id,
                downloadedSize = file.local?.downloadedSize?.toLong() ?: 0L,
                totalSize = file.size,
                isCompleted = file.local?.isDownloadingCompleted == true,
                localPath = file.local?.path?.takeIf { it.isNotBlank() }
            )
        )
    }

    override suspend fun submitPhoneNumber(phone: String) {
        val settings = TdApi.PhoneNumberAuthenticationSettings()
        client?.send(TdApi.SetAuthenticationPhoneNumber(phone, settings)) { handleResultError(it) }
    }

    override suspend fun submitCode(code: String) {
        client?.send(TdApi.CheckAuthenticationCode(code)) { handleResultError(it) }
    }

    override suspend fun submitPassword(password: String) {
        client?.send(TdApi.CheckAuthenticationPassword(password)) { handleResultError(it) }
    }

    override suspend fun logOut() {
        client?.send(TdApi.LogOut()) {}
    }

    private fun handleResultError(result: TdApi.Object) {
        if (result is TdApi.Error) {
            _authState.value = AuthState.Error(result.message)
        }
    }

    override suspend fun fetchAudioCommunities(): List<Channel> {
        val ids = loadAllChatIds()
        val chats = ids.mapNotNull { getChat(it) }
        // We can't cheaply check audio presence without scanning — return all
        // channels and groups; the caller will filter further by fetching
        // a small audio page per chat.
        return chats
            .filter { it.type is TdApi.ChatTypeSupergroup || it.type is TdApi.ChatTypeBasicGroup }
            .map { it.toDomainChannel() }
    }

    private suspend fun loadAllChatIds(): List<Long> {
        val c = client ?: return emptyList()
        val deferred = CompletableDeferred<TdApi.Chats>()
        c.send(TdApi.GetChats(TdApi.ChatListMain(), 200)) { result ->
            if (result is TdApi.Chats) deferred.complete(result)
            else deferred.complete(TdApi.Chats())
        }
        return deferred.await().chatIds?.toList().orEmpty()
    }

    private suspend fun getChat(chatId: Long): TdApi.Chat? {
        val c = client ?: return null
        val deferred = CompletableDeferred<TdApi.Chat?>()
        c.send(TdApi.GetChat(chatId)) { result ->
            deferred.complete(result as? TdApi.Chat)
        }
        return deferred.await()
    }

    override suspend fun fetchAudioPage(chatId: Long, fromMessageId: Long, limit: Int): List<Track> {
        val c = client ?: return emptyList()
        val deferred = CompletableDeferred<TdApi.Messages>()
        c.send(TdApi.GetChatHistory(chatId, fromMessageId, 0, limit, false)) { result ->
            if (result is TdApi.Messages) deferred.complete(result) else deferred.complete(TdApi.Messages())
        }
        val msgs = deferred.await()
        return msgs.messages?.mapNotNull { it.toTrackOrNull() }.orEmpty()
    }

    override suspend fun downloadFile(fileId: Int, priority: Int, synchronous: Boolean): String? {
        val c = client ?: return null
        val deferred = CompletableDeferred<TdApi.File?>()
        c.send(TdApi.DownloadFile(fileId, priority, 0, 0, synchronous)) { result ->
            deferred.complete(result as? TdApi.File)
        }
        return deferred.await()?.local?.path?.takeIf { it.isNotBlank() }
    }

    override suspend fun localPathFor(fileId: Int): String? {
        // Best-effort: TDLib doesn't have a sync getter for arbitrary fileId;
        // the caller relies on UpdateFile streaming.
        return null
    }

    private fun TdApi.Chat.toDomainChannel(): Channel = Channel(
        id = id,
        title = title,
        username = null,
        avatarPath = photo?.small?.local?.path?.takeIf { it.isNotBlank() },
        trackCount = 0,
        isAdded = false
    )

    private fun TdApi.Message.toTrackOrNull(): Track? {
        val content = content ?: return null
        return when (content) {
            is TdApi.MessageAudio -> {
                val a = content.audio ?: return null
                val f = a.audio ?: return null
                Track(
                    id = 0,
                    channelId = chatId,
                    messageId = id,
                    tdlibFileId = f.id,
                    title = a.title.ifBlank { a.fileName.ifBlank { "Untitled" } },
                    artist = a.performer.ifBlank { null },
                    durationMs = a.duration * 1000L,
                    sizeBytes = f.size,
                    mimeType = a.mimeType,
                    date = date.toLong() * 1000L,
                    localPath = f.local?.path?.takeIf { it.isNotBlank() },
                    isLiked = false,
                    isSavedOffline = false
                )
            }
            is TdApi.MessageDocument -> {
                val d = content.document ?: return null
                val f = d.document ?: return null
                if (d.mimeType?.startsWith("audio/") != true) return null
                Track(
                    id = 0,
                    channelId = chatId,
                    messageId = id,
                    tdlibFileId = f.id,
                    title = d.fileName.ifBlank { "Untitled" },
                    artist = null,
                    durationMs = 0L,
                    sizeBytes = f.size,
                    mimeType = d.mimeType,
                    date = date.toLong() * 1000L,
                    localPath = f.local?.path?.takeIf { it.isNotBlank() },
                    isLiked = false,
                    isSavedOffline = false
                )
            }
            else -> null
        }
    }
}
