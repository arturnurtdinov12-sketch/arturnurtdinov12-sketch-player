package com.tgplayer.app.presentation.player

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.repository.TrackRepository
import com.tgplayer.app.domain.model.Track
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    private val controller: PlaybackController,
    private val tracks: TrackRepository
) : ViewModel() {

    val state: StateFlow<PlaybackState> = controller.state

    init { controller.connect() }

    fun playPause() = controller.playPause()
    fun next() = controller.next()
    fun previous() = controller.previous()
    fun seekTo(ms: Long) = controller.seekTo(ms)
    fun toggleShuffle() = controller.setShuffle(!state.value.shuffle)
    fun cycleRepeat() = controller.cycleRepeat()
    fun tick() = controller.pollPosition()

    fun toggleLike(track: Track) = viewModelScope.launch {
        val newLiked = !track.isLiked
        tracks.setLiked(track, newLiked)
        controller.updateTrackInQueue(track.id) { it.copy(isLiked = newLiked) }
    }
}
