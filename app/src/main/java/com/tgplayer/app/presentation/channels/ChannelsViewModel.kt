package com.tgplayer.app.presentation.channels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.repository.ChannelRepository
import com.tgplayer.app.domain.model.Channel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChannelsViewModel @Inject constructor(
    private val repo: ChannelRepository
) : ViewModel() {

    val addedChannels: StateFlow<List<Channel>> = repo.observeAdded().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    val allChannels: StateFlow<List<Channel>> = repo.observeAll().stateIn(
        viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList()
    )

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    fun refresh() = viewModelScope.launch {
        _isRefreshing.value = true
        runCatching { repo.refreshAvailableCommunities() }
        _isRefreshing.value = false
    }

    fun setAdded(channelId: Long, added: Boolean) = viewModelScope.launch {
        repo.setChannelAdded(channelId, added)
    }
}
