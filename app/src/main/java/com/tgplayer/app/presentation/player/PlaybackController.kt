package com.tgplayer.app.presentation.player

import android.content.ComponentName
import android.content.Context
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.PlaybackParameters
import androidx.media3.common.Player
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import com.tgplayer.app.domain.model.RepeatMode
import com.tgplayer.app.domain.model.Track
import com.tgplayer.app.service.PlayerService
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject
import javax.inject.Singleton

data class PlaybackState(
    val current: Track? = null,
    val queue: List<Track> = emptyList(),
    val currentIndex: Int = -1,
    val isPlaying: Boolean = false,
    val positionMs: Long = 0L,
    val durationMs: Long = 0L,
    val repeatMode: RepeatMode = RepeatMode.OFF,
    val shuffle: Boolean = false
)

@Singleton
class PlaybackController @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val scope = CoroutineScope(SupervisorJob())
    private val _state = MutableStateFlow(PlaybackState())
    val state: StateFlow<PlaybackState> = _state.asStateFlow()

    private var controllerFuture: ListenableFuture<MediaController>? = null
    private var controller: MediaController? = null
    private var pendingQueue: Pair<List<MediaItem>, Int>? = null

    fun connect() {
        if (controller != null || controllerFuture != null) return
        val token = SessionToken(context, ComponentName(context, PlayerService::class.java))
        controllerFuture = MediaController.Builder(context, token).buildAsync().also { future ->
            future.addListener({
                runCatching {
                    val c = future.get()
                    controller = c
                    c.addListener(listener)
                    pendingQueue?.let { (items, start) ->
                        c.setMediaItems(items, start.coerceIn(0, items.size - 1), 0L)
                        c.prepare()
                        c.play()
                    }
                    pendingQueue = null
                    syncFromPlayer()
                }
            }, MoreExecutors.directExecutor())
        }
    }

    fun release() {
        controller?.removeListener(listener)
        controller?.release()
        controller = null
        controllerFuture?.cancel(true)
        controllerFuture = null
    }

    fun setQueue(tracks: List<Track>, startIndex: Int) {
        if (tracks.isEmpty()) return
        connect()
        val items = tracks.map { it.toMediaItem() }
        val safeStart = startIndex.coerceIn(0, items.size - 1)
        val c = controller
        if (c != null) {
            c.setMediaItems(items, safeStart, 0L)
            c.prepare()
            c.play()
        } else {
            // Controller hasn't connected yet — replay once it's ready.
            pendingQueue = items to safeStart
        }
        _state.value = _state.value.copy(queue = tracks, currentIndex = safeStart, current = tracks.getOrNull(safeStart))
    }

    /** Apply [transform] to any matching track in the queue and the currently playing track. */
    fun updateTrackInQueue(trackId: Long, transform: (Track) -> Track) {
        val s = _state.value
        val newQueue = s.queue.map { if (it.id == trackId) transform(it) else it }
        val newCurrent = s.current?.let { if (it.id == trackId) transform(it) else it }
        _state.value = s.copy(queue = newQueue, current = newCurrent)
    }

    /** Polls the current playback position from the underlying controller. */
    fun pollPosition() {
        val c = controller ?: return
        _state.value = _state.value.copy(positionMs = c.currentPosition, durationMs = c.duration.coerceAtLeast(0L))
    }

    fun playPause() {
        val c = controller ?: return
        if (c.isPlaying) c.pause() else c.play()
    }

    fun next() { controller?.seekToNextMediaItem() }
    fun previous() { controller?.seekToPreviousMediaItem() }
    fun seekTo(positionMs: Long) { controller?.seekTo(positionMs) }
    fun setPlaybackSpeed(speed: Float) { controller?.playbackParameters = PlaybackParameters(speed) }

    fun setShuffle(enabled: Boolean) {
        controller?.shuffleModeEnabled = enabled
        _state.value = _state.value.copy(shuffle = enabled)
    }

    fun cycleRepeat() {
        val next = when (_state.value.repeatMode) {
            RepeatMode.OFF -> RepeatMode.ALL
            RepeatMode.ALL -> RepeatMode.ONE
            RepeatMode.ONE -> RepeatMode.OFF
        }
        controller?.repeatMode = when (next) {
            RepeatMode.OFF -> Player.REPEAT_MODE_OFF
            RepeatMode.ALL -> Player.REPEAT_MODE_ALL
            RepeatMode.ONE -> Player.REPEAT_MODE_ONE
        }
        _state.value = _state.value.copy(repeatMode = next)
    }

    private val listener = object : Player.Listener {
        override fun onIsPlayingChanged(isPlaying: Boolean) {
            _state.value = _state.value.copy(isPlaying = isPlaying)
        }
        override fun onMediaItemTransition(mediaItem: MediaItem?, reason: Int) {
            syncFromPlayer()
        }
        override fun onPositionDiscontinuity(
            oldPosition: Player.PositionInfo,
            newPosition: Player.PositionInfo,
            reason: Int
        ) { syncFromPlayer() }
    }

    private fun syncFromPlayer() {
        val c = controller ?: return
        val idx = c.currentMediaItemIndex
        val current = _state.value.queue.getOrNull(idx)
        _state.value = _state.value.copy(
            currentIndex = idx,
            current = current,
            isPlaying = c.isPlaying,
            positionMs = c.currentPosition,
            durationMs = c.duration.coerceAtLeast(0L)
        )
    }

    private fun Track.toMediaItem(): MediaItem {
        val source = localPath ?: "tdfile://${tdlibFileId}"
        return MediaItem.Builder()
            .setMediaId(id.toString())
            .setUri(source)
            .setMediaMetadata(
                MediaMetadata.Builder()
                    .setTitle(displayTitle)
                    .setArtist(displayArtist)
                    .build()
            )
            .build()
    }
}
