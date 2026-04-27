package com.tgplayer.app.presentation.channels

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.repository.ChannelRepository
import com.tgplayer.app.data.repository.TrackRepository
import com.tgplayer.app.domain.model.Track
import com.tgplayer.app.domain.model.TrackSortOrder
import com.tgplayer.app.presentation.player.PlaybackController
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class TrackListViewModel @Inject constructor(
    savedState: SavedStateHandle,
    private val tracks: TrackRepository,
    private val channels: ChannelRepository,
    private val playback: PlaybackController
) : ViewModel() {

    val channelId: Long = savedState.get<Long>("channelId") ?: 0L

    private val _sort = MutableStateFlow(TrackSortOrder.DATE_DESC)
    val sort: StateFlow<TrackSortOrder> = _sort.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    val trackList: StateFlow<List<Track>> = combine(
        tracks.observeChannelTracks(channelId),
        _sort
    ) { list, order ->
        when (order) {
            TrackSortOrder.DATE_DESC -> list.sortedByDescending { it.date }
            TrackSortOrder.DATE_ASC -> list.sortedBy { it.date }
            TrackSortOrder.TITLE -> list.sortedBy { it.displayTitle.lowercase() }
            TrackSortOrder.ARTIST -> list.sortedBy { it.displayArtist.lowercase() }
            TrackSortOrder.DURATION -> list.sortedBy { it.durationMs }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    init { loadOlder(initial = true) }

    fun setSort(order: TrackSortOrder) { _sort.value = order }

    fun loadOlder(initial: Boolean = false) = viewModelScope.launch {
        _isLoading.value = true
        val anchor = if (initial) 0L else trackList.value.minOfOrNull { it.messageId } ?: 0L
        runCatching { tracks.fetchAndStoreTracks(channelId, anchor, 30) }
        runCatching { channels.updateTrackCount(channelId) }
        _isLoading.value = false
    }

    fun playFrom(index: Int) {
        playback.setQueue(trackList.value, index)
    }

    fun toggleLiked(track: Track) = viewModelScope.launch {
        tracks.setLiked(track, !track.isLiked)
    }
}
