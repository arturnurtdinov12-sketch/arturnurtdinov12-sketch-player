package com.tgplayer.app.data.remote

import com.tgplayer.app.domain.model.AuthState
import com.tgplayer.app.domain.model.Channel
import com.tgplayer.app.domain.model.Track
import kotlinx.coroutines.flow.Flow

/**
 * Pure abstraction over TDLib that hides all TDLib types from the rest of the app.
 *
 * Exposes only the operations the player needs: phone-based authorization,
 * listing user's channels/groups, fetching audio messages and streaming files.
 */
interface TelegramClient {

    val authState: Flow<AuthState>

    /** Stream of file-download progress updates, key = TDLib file id. */
    val fileDownloadUpdates: Flow<FileDownloadUpdate>

    suspend fun start()
    suspend fun submitPhoneNumber(phone: String)
    suspend fun submitCode(code: String)
    suspend fun submitPassword(password: String)
    suspend fun logOut()

    /** Returns chats (channels + groups) that contain at least one audio file. */
    suspend fun fetchAudioCommunities(): List<Channel>

    /**
     * Loads tracks (audio messages) from a chat in pages, oldest-first traversal
     * by anchoring on `fromMessageId` (use 0 for newest).
     */
    suspend fun fetchAudioPage(chatId: Long, fromMessageId: Long, limit: Int): List<Track>

    /** Triggers a download for a TDLib file id; result observed via [fileDownloadUpdates]. */
    suspend fun downloadFile(fileId: Int, priority: Int = 16, synchronous: Boolean = false): String?

    /** Returns the local file path if the file is fully downloaded, else null. */
    suspend fun localPathFor(fileId: Int): String?
}

data class FileDownloadUpdate(
    val fileId: Int,
    val downloadedSize: Long,
    val totalSize: Long,
    val isCompleted: Boolean,
    val localPath: String?
)
