package com.tgplayer.app.presentation.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tgplayer.app.data.remote.TelegramClient
import com.tgplayer.app.domain.model.AuthState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val telegram: TelegramClient
) : ViewModel() {

    val state: StateFlow<AuthState> = telegram.authState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = AuthState.Initializing
    )

    init {
        viewModelScope.launch { telegram.start() }
    }

    fun submitPhone(phone: String) = viewModelScope.launch {
        telegram.submitPhoneNumber(phone)
    }

    fun submitCode(code: String) = viewModelScope.launch {
        telegram.submitCode(code)
    }

    fun submitPassword(password: String) = viewModelScope.launch {
        telegram.submitPassword(password)
    }

    fun logOut() = viewModelScope.launch { telegram.logOut() }
}
