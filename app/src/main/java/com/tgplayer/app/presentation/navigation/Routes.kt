package com.tgplayer.app.presentation.navigation

object Routes {
    const val SPLASH = "splash"
    const val WELCOME = "auth/welcome"
    const val PHONE = "auth/phone"
    const val CODE = "auth/code"
    const val PASSWORD = "auth/password"
    const val CHANNEL_SELECT = "channels/select"
    const val ROOT_TABS = "tabs"

    const val TAB_CHANNELS = "tab/channels"
    const val TAB_PLAYLISTS = "tab/playlists"
    const val TAB_NOW_PLAYING = "tab/now-playing"

    const val CHANNEL_TRACKS = "channels/{channelId}"
    fun channelTracks(channelId: Long) = "channels/$channelId"

    const val PLAYLIST_DETAIL = "playlists/{playlistId}"
    fun playlistDetail(id: Long) = "playlists/$id"

    const val PLAYER = "player"
    const val SETTINGS = "settings"
}
