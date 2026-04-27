package com.tgplayer.app.domain.model

sealed interface AuthState {
    data object Initializing : AuthState
    data object WaitingPhone : AuthState
    data object WaitingCode : AuthState
    data object WaitingPassword : AuthState
    data object Ready : AuthState
    data object LoggedOut : AuthState
    data class Error(val message: String) : AuthState
}
