package com.tgplayer.app.domain.model

data class Playlist(
    val id: Long,
    val name: String,
    val isSystem: Boolean,
    val createdAt: Long,
    val trackCount: Int,
    val totalDurationMs: Long,
    val thumbnailPath: String?
) {
    companion object {
        const val LIKED_PLAYLIST_ID = 1L
        const val LIKED_PLAYLIST_NAME = "Liked Tracks"
    }
}
