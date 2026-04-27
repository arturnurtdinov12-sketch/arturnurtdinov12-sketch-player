package com.tgplayer.app.data.repository

import com.tgplayer.app.data.local.dao.PlaylistWithStats
import com.tgplayer.app.data.local.entity.ChannelEntity
import com.tgplayer.app.data.local.entity.TrackEntity
import com.tgplayer.app.domain.model.Channel
import com.tgplayer.app.domain.model.Playlist
import com.tgplayer.app.domain.model.Track

fun ChannelEntity.toDomain() = Channel(
    id = id,
    title = title,
    username = username,
    avatarPath = avatarPath,
    trackCount = trackCount,
    isAdded = isAdded
)

fun Channel.toEntity(lastSyncedAt: Long = System.currentTimeMillis()) = ChannelEntity(
    id = id,
    title = title,
    username = username,
    avatarPath = avatarPath,
    trackCount = trackCount,
    isAdded = isAdded,
    lastSyncedAt = lastSyncedAt
)

fun TrackEntity.toDomain() = Track(
    id = id,
    channelId = channelId,
    messageId = messageId,
    tdlibFileId = tdlibFileId,
    title = title,
    artist = artist,
    durationMs = durationMs,
    sizeBytes = sizeBytes,
    mimeType = mimeType,
    date = date,
    localPath = localPath,
    isLiked = isLiked,
    isSavedOffline = isSavedOffline
)

fun Track.toEntity() = TrackEntity(
    id = id,
    channelId = channelId,
    messageId = messageId,
    tdlibFileId = tdlibFileId,
    title = title,
    artist = artist,
    durationMs = durationMs,
    sizeBytes = sizeBytes,
    mimeType = mimeType,
    date = date,
    localPath = localPath,
    isLiked = isLiked,
    isSavedOffline = isSavedOffline
)

fun PlaylistWithStats.toDomain() = Playlist(
    id = id,
    name = name,
    isSystem = isSystem,
    createdAt = createdAt,
    trackCount = trackCount,
    totalDurationMs = totalDurationMs,
    thumbnailPath = thumbnailPath
)
