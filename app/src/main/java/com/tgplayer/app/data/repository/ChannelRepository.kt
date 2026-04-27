package com.tgplayer.app.data.repository

import com.tgplayer.app.data.local.dao.ChannelDao
import com.tgplayer.app.data.local.dao.TrackDao
import com.tgplayer.app.data.remote.TelegramClient
import com.tgplayer.app.domain.model.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChannelRepository @Inject constructor(
    private val channelDao: ChannelDao,
    private val trackDao: TrackDao,
    private val telegram: TelegramClient
) {
    fun observeAdded(): Flow<List<Channel>> =
        channelDao.observeAdded().map { it.map { e -> e.toDomain() } }

    fun observeAll(): Flow<List<Channel>> =
        channelDao.observeAll().map { it.map { e -> e.toDomain() } }

    suspend fun refreshAvailableCommunities(): List<Channel> {
        val remote = telegram.fetchAudioCommunities()
        // Preserve local "isAdded" + trackCount when upserting.
        val merged = remote.map { c ->
            val existing = channelDao.get(c.id)
            c.copy(
                isAdded = existing?.isAdded ?: false,
                trackCount = existing?.trackCount ?: 0
            )
        }
        channelDao.upsert(merged.map { it.toEntity() })
        return merged
    }

    suspend fun setChannelAdded(channelId: Long, added: Boolean) {
        channelDao.setAdded(channelId, added)
        if (!added) {
            trackDao.deleteByChannel(channelId)
        }
    }

    suspend fun updateTrackCount(channelId: Long) {
        channelDao.setTrackCount(channelId, trackDao.countByChannel(channelId))
    }
}
