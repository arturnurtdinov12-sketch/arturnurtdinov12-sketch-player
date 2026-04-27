package com.tgplayer.app.presentation.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LibraryMusic
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Tag
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.tgplayer.app.R
import com.tgplayer.app.presentation.channels.ChannelListScreen
import com.tgplayer.app.presentation.player.NowPlayingScreen
import com.tgplayer.app.presentation.player.MiniPlayerBar
import com.tgplayer.app.presentation.playlists.PlaylistsScreen
import com.tgplayer.app.presentation.theme.TgAccent
import com.tgplayer.app.presentation.theme.TgBackground
import com.tgplayer.app.presentation.theme.TgTextSecondary

@Composable
fun MainTabsScaffold(rootController: NavController) {
    val tabsController = rememberNavController()
    val backStackEntry by tabsController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route ?: Routes.TAB_CHANNELS

    Scaffold(
        containerColor = TgBackground,
        bottomBar = {
            Column {
                MiniPlayerBar(onTap = { rootController.navigate(Routes.PLAYER) })
                NavigationBar(containerColor = MaterialTheme.colorScheme.background) {
                    NavigationBarItem(
                        selected = currentRoute == Routes.TAB_CHANNELS,
                        onClick = { tabsController.navigate(Routes.TAB_CHANNELS) { launchSingleTop = true; popUpTo(Routes.TAB_CHANNELS) } },
                        icon = { Icon(Icons.Filled.Tag, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_channels)) },
                        colors = barColors()
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.TAB_PLAYLISTS,
                        onClick = { tabsController.navigate(Routes.TAB_PLAYLISTS) { launchSingleTop = true; popUpTo(Routes.TAB_CHANNELS) } },
                        icon = { Icon(Icons.Filled.LibraryMusic, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_playlists)) },
                        colors = barColors()
                    )
                    NavigationBarItem(
                        selected = currentRoute == Routes.TAB_NOW_PLAYING,
                        onClick = { tabsController.navigate(Routes.TAB_NOW_PLAYING) { launchSingleTop = true; popUpTo(Routes.TAB_CHANNELS) } },
                        icon = { Icon(Icons.Filled.PlayCircle, contentDescription = null) },
                        label = { Text(stringResource(R.string.tab_now_playing)) },
                        colors = barColors()
                    )
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            NavHost(navController = tabsController, startDestination = Routes.TAB_CHANNELS) {
                composable(Routes.TAB_CHANNELS) {
                    ChannelListScreen(
                        onChannelClick = { id -> rootController.navigate(Routes.channelTracks(id)) },
                        onSettingsClick = { rootController.navigate(Routes.SETTINGS) },
                        onAddChannels = { rootController.navigate(Routes.CHANNEL_SELECT) }
                    )
                }
                composable(Routes.TAB_PLAYLISTS) {
                    PlaylistsScreen(
                        onOpenPlaylist = { id -> rootController.navigate(Routes.playlistDetail(id)) }
                    )
                }
                composable(Routes.TAB_NOW_PLAYING) {
                    NowPlayingScreen(onOpenFull = { rootController.navigate(Routes.PLAYER) })
                }
            }
        }
    }
}

@Composable
private fun barColors() = NavigationBarItemDefaults.colors(
    selectedIconColor = TgAccent,
    selectedTextColor = TgAccent,
    unselectedIconColor = TgTextSecondary,
    unselectedTextColor = TgTextSecondary,
    indicatorColor = MaterialTheme.colorScheme.background
)

@Composable
private fun stringResource(id: Int): String = androidx.compose.ui.res.stringResource(id)
