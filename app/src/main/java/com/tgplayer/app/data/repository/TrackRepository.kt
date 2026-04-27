package com.tgplayer.app.data.repository

import com.tgplayer.app.data.local.dao.PlaylistDao
import com.tgplayer.app.data.local.dao.TrackDao
import com.tgplayer.app.data.local.entity.PlaylistEntity
import com.tgplayer.app.data.local.entity.PlaylistTrackEntity
import com.tgplayer.app.data.remote.TelegramClient
import com.tgplayer.app.domain.model.Playlist
import com.tgplayer.app.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TrackRepository @Inject constructor(
    private val trackDao: TrackDao,
    private val playlistDao: PlaylistDao,
    private val telegram: TelegramClient
) {

    fun observeChannelTracks(channelId: Long): Flow<List<Track>> =
        trackDao.observeByChannel(channelId).map { it.map { e -> e.toDomain() } }

    suspend fun fetchAndStoreTracks(channelId: Long, fromMessageId: Long, limit: Int): List<Track> {
        val remote = telegram.fetchAudioPage(channelId, fromMessageId, limit)
        val merged = remote.map { t ->
            val existing = trackDao.findByMessage(channelId, t.messageId)
            t.copy(
                id = existing?.id ?: 0,
                isLiked = existing?.isLiked ?: false,
                isSavedOffline = existing?.isSavedOffline ?: false,
                localPath = existing?.localPath ?: t.localPath
            )
        }
        trackDao.upsertAll(merged.map { it.toEntity() })
        return merged
    }

    suspend fun setLiked(track: Track, liked: Boolean) {
        trackDao.setLiked(track.id, liked)
        val liked0 = ensureLikedPlaylist()
        if (liked) {
            val pos = playlistDao.nextPosition(liked0)
            playlistDao.insertCrossRef(
                PlaylistTrackEntity(liked0, track.id, pos, System.currentTimeMillis())
            )
        } else {
            playlistDao.removeTrack(liked0, track.id)
        }
    }

    suspend fun setSavedOffline(track: Track, saved: Boolean, localPath: String?) {
        trackDao.setSavedOffline(track.id, saved, localPath)
    }

    suspend fun ensureLikedPlaylist(): Long {
        val existing = playlistDao.getSystemPlaylist()
        if (existing != null) return existing.id
        return playlistDao.insert(
            PlaylistEntity(
                id = Playlist.LIKED_PLAYLIST_ID,
                name = Playlist.LIKED_PLAYLIST_NAME,
                isSystem = true,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getById(id: Long): Track? = trackDao.getById(id)?.toDomain()
}
