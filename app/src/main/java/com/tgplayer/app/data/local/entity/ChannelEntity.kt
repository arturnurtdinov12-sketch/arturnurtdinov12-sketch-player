package com.tgplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channels")
data class ChannelEntity(
    @PrimaryKey val id: Long,
    val title: String,
    val username: String?,
    val avatarPath: String?,
    val trackCount: Int,
    val isAdded: Boolean,
    val lastSyncedAt: Long
)
