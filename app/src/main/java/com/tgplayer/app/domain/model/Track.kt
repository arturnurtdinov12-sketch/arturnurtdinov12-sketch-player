package com.tgplayer.app.domain.model

data class Track(
    val id: Long,
    val channelId: Long,
    val messageId: Long,
    val tdlibFileId: Int,
    val title: String,
    val artist: String?,
    val durationMs: Long,
    val sizeBytes: Long,
    val mimeType: String?,
    val date: Long,
    val localPath: String?,
    val isLiked: Boolean,
    val isSavedOffline: Boolean
) {
    val displayTitle: String get() = title.ifBlank { "Unknown title" }
    val displayArtist: String get() = artist?.takeIf { it.isNotBlank() } ?: "Unknown artist"
}
