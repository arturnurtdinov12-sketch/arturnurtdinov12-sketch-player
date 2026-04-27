package com.tgplayer.app.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.tgplayer.app.data.local.entity.ChannelEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Query("SELECT * FROM channels WHERE isAdded = 1 ORDER BY title COLLATE NOCASE ASC")
    fun observeAdded(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels ORDER BY title COLLATE NOCASE ASC")
    fun observeAll(): Flow<List<ChannelEntity>>

    @Query("SELECT * FROM channels WHERE id = :id")
    suspend fun get(id: Long): ChannelEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(channels: List<ChannelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(channel: ChannelEntity)

    @Query("UPDATE channels SET isAdded = :added WHERE id = :id")
    suspend fun setAdded(id: Long, added: Boolean)

    @Query("UPDATE channels SET trackCount = :count WHERE id = :id")
    suspend fun setTrackCount(id: Long, count: Int)

    @Query("DELETE FROM channels WHERE id = :id")
    suspend fun delete(id: Long)
}
