package com.tgplayer.app.data.repository

import com.tgplayer.app.data.local.dao.PlaylistDao
import com.tgplayer.app.data.local.entity.PlaylistEntity
import com.tgplayer.app.data.local.entity.PlaylistTrackEntity
import com.tgplayer.app.domain.model.Playlist
import com.tgplayer.app.domain.model.Track
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PlaylistRepository @Inject constructor(
    private val playlistDao: PlaylistDao
) {

    fun observePlaylists(): Flow<List<Playlist>> =
        playlistDao.observePlaylistsWithStats().map { it.map { p -> p.toDomain() } }

    fun observeTracks(playlistId: Long): Flow<List<Track>> =
        playlistDao.observeTracks(playlistId).map { it.map { t -> t.toDomain() } }

    suspend fun create(name: String): Long = playlistDao.insert(
        PlaylistEntity(name = name.ifBlank { "Untitled" }, createdAt = System.currentTimeMillis())
    )

    suspend fun rename(id: Long, name: String) = playlistDao.rename(id, name)

    suspend fun delete(id: Long) = playlistDao.delete(id)

    suspend fun addTrack(playlistId: Long, trackId: Long) {
        val pos = playlistDao.nextPosition(playlistId)
        playlistDao.insertCrossRef(
            PlaylistTrackEntity(playlistId, trackId, pos, System.currentTimeMillis())
        )
    }

    suspend fun removeTrack(playlistId: Long, trackId: Long) =
        playlistDao.removeTrack(playlistId, trackId)

    suspend fun reorder(playlistId: Long, orderedTrackIds: List<Long>) =
        playlistDao.reorder(playlistId, orderedTrackIds)
}
