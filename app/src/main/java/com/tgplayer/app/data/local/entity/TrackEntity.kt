package com.tgplayer.app.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "tracks",
    foreignKeys = [
        ForeignKey(
            entity = ChannelEntity::class,
            parentColumns = ["id"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("channelId"), Index(value = ["channelId", "messageId"], unique = true)]
)
data class TrackEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
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
    val isLiked: Boolean = false,
    val isSavedOffline: Boolean = false
)
