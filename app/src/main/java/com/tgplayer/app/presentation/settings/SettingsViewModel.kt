package com.tgplayer.app.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.remote.TelegramClient
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val telegram: TelegramClient
) : ViewModel() {

    fun logOut(onDone: () -> Unit) = viewModelScope.launch {
        telegram.logOut()
        onDone()
    }
}
