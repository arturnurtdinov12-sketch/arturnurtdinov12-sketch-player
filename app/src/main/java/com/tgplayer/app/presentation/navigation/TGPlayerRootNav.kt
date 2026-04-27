package com.tgplayer.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.tgplayer.app.domain.model.AuthState
import com.tgplayer.app.presentation.auth.AuthViewModel
import com.tgplayer.app.presentation.auth.CodeScreen
import com.tgplayer.app.presentation.auth.PasswordScreen
import com.tgplayer.app.presentation.auth.PhoneScreen
import com.tgplayer.app.presentation.auth.WelcomeScreen
import com.tgplayer.app.presentation.channels.ChannelSelectScreen
import com.tgplayer.app.presentation.channels.TrackListScreen
import com.tgplayer.app.presentation.common.SplashScreen
import com.tgplayer.app.presentation.player.PlayerScreen
import com.tgplayer.app.presentation.playlists.PlaylistDetailScreen
import com.tgplayer.app.presentation.settings.SettingsScreen

@Composable
fun TGPlayerRootNav() {
    val navController = rememberNavController()
    val authViewModel: AuthViewModel = hiltViewModel()
    val authState by authViewModel.state.collectAsState()

    // Observe auth state from the root so navigation keeps working after the
    // SPLASH destination is destroyed. Re-keying on each authState transition
    // (and the current route) ensures retries when needed.
    val currentEntry by navController.currentBackStackEntryAsState()
    LaunchedEffect(authState, currentEntry?.destination?.route) {
        routeForAuth(authState, currentEntry?.destination?.route, navController)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        NavHost(
            navController = navController,
            startDestination = Routes.SPLASH
        ) {
            composable(Routes.SPLASH) {
                SplashScreen()
            }
            composable(Routes.WELCOME) {
                WelcomeScreen(onLoginClick = { navController.navigate(Routes.PHONE) })
            }
            composable(Routes.PHONE) {
                PhoneScreen(onSubmit = authViewModel::submitPhone, viewModel = authViewModel)
            }
            composable(Routes.CODE) {
                CodeScreen(onSubmit = authViewModel::submitCode, viewModel = authViewModel)
            }
            composable(Routes.PASSWORD) {
                PasswordScreen(onSubmit = authViewModel::submitPassword, viewModel = authViewModel)
            }
            composable(Routes.CHANNEL_SELECT) {
                ChannelSelectScreen(
                    onDone = {
                        navController.navigate(Routes.ROOT_TABS) {
                            popUpTo(Routes.SPLASH) { inclusive = true }
                        }
                    }
                )
            }
            composable(Routes.ROOT_TABS) {
                MainTabsScaffold(rootController = navController)
            }
            composable(
                Routes.CHANNEL_TRACKS,
                arguments = listOf(navArgument("channelId") { type = NavType.LongType })
            ) { backStack ->
                val channelId = backStack.arguments?.getLong("channelId") ?: 0L
                TrackListScreen(
                    channelId = channelId,
                    onBack = { navController.popBackStack() },
                    onOpenPlayer = { navController.navigate(Routes.PLAYER) }
                )
            }
            composable(
                Routes.PLAYLIST_DETAIL,
                arguments = listOf(navArgument("playlistId") { type = NavType.LongType })
            ) { backStack ->
                val playlistId = backStack.arguments?.getLong("playlistId") ?: 0L
                PlaylistDetailScreen(
                    playlistId = playlistId,
                    onBack = { navController.popBackStack() },
                    onOpenPlayer = { navController.navigate(Routes.PLAYER) }
                )
            }
            composable(Routes.PLAYER) {
                PlayerScreen(onMinimize = { navController.popBackStack() })
            }
            composable(Routes.SETTINGS) {
                SettingsScreen(
                    onBack = { navController.popBackStack() },
                    onLoggedOut = {
                        navController.navigate(Routes.WELCOME) {
                            popUpTo(0) { inclusive = true }
                        }
                    },
                    onManageChannels = { navController.navigate(Routes.CHANNEL_SELECT) }
                )
            }
        }
    }
}

private fun routeForAuth(
    state: AuthState,
    currentRoute: String?,
    navController: NavController
) {
    val target = when (state) {
        AuthState.WaitingPhone -> Routes.WELCOME
        AuthState.WaitingCode -> Routes.CODE
        AuthState.WaitingPassword -> Routes.PASSWORD
        AuthState.Ready -> Routes.ROOT_TABS
        AuthState.LoggedOut -> Routes.WELCOME
        AuthState.Initializing, is AuthState.Error -> return
    }
    // Don't trigger redundant navigation — e.g. user typing on PhoneScreen
    // shouldn't yank them back if WaitingPhone is re-emitted, and pushing
    // WELCOME on top of PHONE would feel like a regression.
    if (currentRoute == target) return
    if (state == AuthState.WaitingPhone && currentRoute == Routes.PHONE) return
    navController.navigate(target) {
        popUpTo(0) { inclusive = true }
        launchSingleTop = true
    }
}
