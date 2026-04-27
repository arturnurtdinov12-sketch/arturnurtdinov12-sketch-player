package com.tgplayer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tgplayer.app.data.local.entity.TrackEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TrackDao {

    @Query("SELECT * FROM tracks WHERE channelId = :channelId ORDER BY date DESC")
    fun observeByChannel(channelId: Long): Flow<List<TrackEntity>>

    @Query("SELECT * FROM tracks WHERE id = :id")
    suspend fun getById(id: Long): TrackEntity?

    @Query("SELECT * FROM tracks WHERE channelId = :channelId AND messageId = :messageId LIMIT 1")
    suspend fun findByMessage(channelId: Long, messageId: Long): TrackEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertAll(tracks: List<TrackEntity>): List<Long>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(track: TrackEntity): Long

    @Query("UPDATE tracks SET isLiked = :liked WHERE id = :id")
    suspend fun setLiked(id: Long, liked: Boolean)

    @Query("UPDATE tracks SET isSavedOffline = :saved, localPath = :path WHERE id = :id")
    suspend fun setSavedOffline(id: Long, saved: Boolean, path: String?)

    @Query("UPDATE tracks SET localPath = :path WHERE id = :id")
    suspend fun setLocalPath(id: Long, path: String?)

    @Query("SELECT * FROM tracks WHERE isLiked = 1 ORDER BY date DESC")
    fun observeLiked(): Flow<List<TrackEntity>>

    @Query("SELECT COUNT(*) FROM tracks WHERE channelId = :channelId")
    suspend fun countByChannel(channelId: Long): Int

    @Query("DELETE FROM tracks WHERE channelId = :channelId")
    suspend fun deleteByChannel(channelId: Long)
}
