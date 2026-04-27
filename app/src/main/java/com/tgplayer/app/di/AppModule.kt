package com.tgplayer.app.di

import android.content.Context
import androidx.room.Room
import com.tgplayer.app.data.local.AppDatabase
import com.tgplayer.app.data.local.dao.ChannelDao
import com.tgplayer.app.data.local.dao.PlaylistDao
import com.tgplayer.app.data.local.dao.TrackDao
import com.tgplayer.app.data.remote.TdLibTelegramClient
import com.tgplayer.app.data.remote.TelegramAppContext
import com.tgplayer.app.data.remote.TelegramClient
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): AppDatabase =
        Room.databaseBuilder(context, AppDatabase::class.java, "tgplayer.db")
            .fallbackToDestructiveMigration()
            .build()

    @Provides fun provideChannelDao(db: AppDatabase): ChannelDao = db.channelDao()
    @Provides fun provideTrackDao(db: AppDatabase): TrackDao = db.trackDao()
    @Provides fun providePlaylistDao(db: AppDatabase): PlaylistDao = db.playlistDao()

    @Provides
    @TelegramAppContext
    fun provideTelegramAppContext(@ApplicationContext context: Context): Context = context
}

@Module
@InstallIn(SingletonComponent::class)
abstract class TelegramBindingModule {

    @Binds
    @Singleton
    abstract fun bindTelegramClient(impl: TdLibTelegramClient): TelegramClient
}
