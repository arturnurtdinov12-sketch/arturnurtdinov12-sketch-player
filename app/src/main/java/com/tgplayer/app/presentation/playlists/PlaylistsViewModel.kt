package com.tgplayer.app.presentation.playlists

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.repository.PlaylistRepository
import com.tgplayer.app.data.repository.TrackRepository
import com.tgplayer.app.domain.model.Playlist
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlaylistsViewModel @Inject constructor(
    private val repo: PlaylistRepository,
    private val tracks: TrackRepository
) : ViewModel() {

    val playlists: StateFlow<List<Playlist>> = repo.observePlaylists().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    init {
        viewModelScope.launch { tracks.ensureLikedPlaylist() }
    }

    fun create(name: String) = viewModelScope.launch { repo.create(name) }
    fun rename(id: Long, name: String) = viewModelScope.launch { repo.rename(id, name) }
    fun delete(id: Long) = viewModelScope.launch { repo.delete(id) }
}
