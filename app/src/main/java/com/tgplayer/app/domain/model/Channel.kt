package com.tgplayer.app.domain.model

data class Channel(
    val id: Long,
    val title: String,
    val username: String?,
    val avatarPath: String?,
    val trackCount: Int,
    val isAdded: Boolean
)
