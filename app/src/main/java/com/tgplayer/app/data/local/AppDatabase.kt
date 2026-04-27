package com.tgplayer.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.tgplayer.app.data.local.dao.ChannelDao
import com.tgplayer.app.data.local.dao.PlaylistDao
import com.tgplayer.app.data.local.dao.TrackDao
import com.tgplayer.app.data.local.entity.ChannelEntity
import com.tgplayer.app.data.local.entity.PlaylistEntity
import com.tgplayer.app.data.local.entity.PlaylistTrackEntity
import com.tgplayer.app.data.local.entity.TrackEntity

@Database(
    entities = [
        ChannelEntity::class,
        TrackEntity::class,
        PlaylistEntity::class,
        PlaylistTrackEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun channelDao(): ChannelDao
    abstract fun trackDao(): TrackDao
    abstract fun playlistDao(): PlaylistDao
}
