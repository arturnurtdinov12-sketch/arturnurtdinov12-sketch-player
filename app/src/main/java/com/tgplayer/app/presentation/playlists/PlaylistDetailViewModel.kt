package com.tgplayer.app.presentation.playlists

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.repository.PlaylistRepository
import com.tgplayer.app.domain.model.Track
import com.tgplayer.app.presentation.player.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistDetailViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val repo: PlaylistRepository,
    private val playback: PlaybackController
) : ViewModel() {

    val playlistId: Long = savedState.get<Long>("playlistId") ?: 0L

    val tracks: StateFlow<List<Track>> = repo.observeTracks(playlistId).stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    fun playFrom(index: Int) = playback.setQueue(tracks.value, index)

    fun shuffleAll() {
        val list = tracks.value.shuffled()
        if (list.isNotEmpty()) playback.setQueue(list, 0)
        playback.setShuffle(true)
    }

    fun reorder(fromIndex: Int, toIndex: Int) {
        val current = tracks.value.toMutableList()
        if (fromIndex !in current.indices || toIndex !in current.indices) return
        val moved = current.removeAt(fromIndex)
        current.add(toIndex, moved)
        viewModelScope.launch { repo.reorder(playlistId, current.map { it.id }) }
    }

    fun remove(track: Track) = viewModelScope.launch { repo.removeTrack(playlistId, track.id) }
}
