package com.tgplayer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.tgplayer.app.data.local.entity.PlaylistEntity
import com.tgplayer.app.data.local.entity.PlaylistTrackEntity
import com.tgplayer.app.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

data class PlaylistWithStats(
    val id: Long,
    val name: String,
    val isSystem: Boolean,
    val createdAt: Long,
    val trackCount: Int,
    val totalDurationMs: Long,
    val thumbnailPath: String?
)

@Dao
interface PlaylistDao {

    @Query(
        """
        SELECT p.id, p.name, p.isSystem, p.createdAt,
               IFNULL(s.trackCount, 0) AS trackCount,
               IFNULL(s.totalDurationMs, 0) AS totalDurationMs,
               s.thumbnailPath AS thumbnailPath
        FROM playlists p
        LEFT JOIN (
            SELECT pt.playlistId AS playlistId,
                   COUNT(*) AS trackCount,
                   SUM(t.durationMs) AS totalDurationMs,
                   MIN(t.localPath) AS thumbnailPath
            FROM playlist_tracks pt
            INNER JOIN tracks t ON t.id = pt.trackId
            GROUP BY pt.playlistId
        ) s ON s.playlistId = p.id
        ORDER BY p.isSystem DESC, p.createdAt DESC
        """
    )
    fun observePlaylistsWithStats(): Flow<List<PlaylistWithStats>>

    @Query("SELECT * FROM playlists WHERE id = :id")
    suspend fun get(id: Long): PlaylistEntity?

    @Query("SELECT * FROM playlists WHERE isSystem = 1 LIMIT 1")
    suspend fun getSystemPlaylist(): PlaylistEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(playlist: PlaylistEntity): Long

    @Query("UPDATE playlists SET name = :name WHERE id = :id")
    suspend fun rename(id: Long, name: String)

    @Query("DELETE FROM playlists WHERE id = :id AND isSystem = 0")
    suspend fun delete(id: Long)

    @Query(
        """
        SELECT t.* FROM tracks t
        INNER JOIN playlist_tracks pt ON pt.trackId = t.id
        WHERE pt.playlistId = :playlistId
        ORDER BY pt.position ASC
        """
    )
    fun observeTracks(playlistId: Long): Flow<List<TrackEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrossRef(ref: PlaylistTrackEntity)

    @Query("DELETE FROM playlist_tracks WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun removeTrack(playlistId: Long, trackId: Long)

    @Query("SELECT IFNULL(MAX(position), -1) + 1 FROM playlist_tracks WHERE playlistId = :playlistId")
    suspend fun nextPosition(playlistId: Long): Int

    @Query("UPDATE playlist_tracks SET position = :position WHERE playlistId = :playlistId AND trackId = :trackId")
    suspend fun setPosition(playlistId: Long, trackId: Long, position: Int)

    @Transaction
    suspend fun reorder(playlistId: Long, orderedTrackIds: List<Long>) {
        orderedTrackIds.forEachIndexed { idx, trackId ->
            setPosition(playlistId, trackId, idx)
        }
    }
}
