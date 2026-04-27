package com.tgplayer.app.service

import android.content.Intent
import androidx.media3.common.AudioAttributes
import androidx.media3.common.C
import androidx.media3.common.MediaItem
import androidx.media3.datasource.DefaultDataSource
import androidx.media3.datasource.cache.CacheDataSource
import androidx.media3.datasource.cache.LeastRecentlyUsedCacheEvictor
import androidx.media3.datasource.cache.SimpleCache
import androidx.media3.exoplayer.ExoPlayer
import androidx.media3.exoplayer.source.DefaultMediaSourceFactory
import androidx.media3.session.MediaSession
import androidx.media3.session.MediaSessionService
import com.tgplayer.app.data.remote.TdLibAudioDataSourceFactory
import com.tgplayer.app.data.remote.TelegramClient
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import javax.inject.Inject

@AndroidEntryPoint
class PlayerService : MediaSessionService() {

    @Inject lateinit var telegram: TelegramClient

    private var mediaSession: MediaSession? = null
    private var cache: SimpleCache? = null
    private var databaseProvider: androidx.media3.database.StandaloneDatabaseProvider? = null

    override fun onCreate() {
        super.onCreate()
        val cacheDir = File(cacheDir, "media").apply { mkdirs() }
        val dbProvider = androidx.media3.database.StandaloneDatabaseProvider(this).also { databaseProvider = it }
        val cache = SimpleCache(
            cacheDir,
            LeastRecentlyUsedCacheEvictor(MAX_CACHE_BYTES),
            dbProvider
        ).also { this.cache = it }

        val tdLibFactory = TdLibAudioDataSourceFactory(telegram)
        val httpFactory = DefaultDataSource.Factory(this)
        val cacheFactory = CacheDataSource.Factory()
            .setCache(cache)
            .setUpstreamDataSourceFactory(httpFactory)
            .setFlags(CacheDataSource.FLAG_IGNORE_CACHE_ON_ERROR)

        val mediaSourceFactory = DefaultMediaSourceFactory(this).setDataSourceFactory(
            DispatchingDataSourceFactory(cacheFactory, tdLibFactory)
        )

        val player = ExoPlayer.Builder(this)
            .setAudioAttributes(
                AudioAttributes.Builder()
                    .setUsage(C.USAGE_MEDIA)
                    .setContentType(C.AUDIO_CONTENT_TYPE_MUSIC)
                    .build(),
                /* handleAudioFocus = */ true
            )
            .setHandleAudioBecomingNoisy(true)
            .setMediaSourceFactory(mediaSourceFactory)
            .build()

        mediaSession = MediaSession.Builder(this, player).build()
    }

    override fun onGetSession(controllerInfo: MediaSession.ControllerInfo): MediaSession? = mediaSession

    override fun onTaskRemoved(rootIntent: Intent?) {
        val player = mediaSession?.player ?: return
        if (!player.playWhenReady || player.mediaItemCount == 0) {
            stopSelf()
        }
    }

    override fun onDestroy() {
        mediaSession?.run {
            player.release()
            release()
        }
        mediaSession = null
        cache?.release()
        cache = null
        databaseProvider?.close()
        databaseProvider = null
        super.onDestroy()
    }

    companion object {
        private const val MAX_CACHE_BYTES = 256L * 1024 * 1024 // 256 MB
    }
}

/** Routes `tdfile://` URIs through TDLib and everything else through HTTP/local. */
private class DispatchingDataSourceFactory(
    private val httpFactory: androidx.media3.datasource.DataSource.Factory,
    private val tdFactory: androidx.media3.datasource.DataSource.Factory
) : androidx.media3.datasource.DataSource.Factory {
    override fun createDataSource(): androidx.media3.datasource.DataSource {
        return DispatchingDataSource(httpFactory.createDataSource(), tdFactory.createDataSource())
    }
}

private class DispatchingDataSource(
    private val httpDelegate: androidx.media3.datasource.DataSource,
    private val tdDelegate: androidx.media3.datasource.DataSource
) : androidx.media3.datasource.DataSource {
    private var active: androidx.media3.datasource.DataSource = httpDelegate

    override fun open(dataSpec: androidx.media3.datasource.DataSpec): Long {
        active = if (dataSpec.uri.scheme == "tdfile") tdDelegate else httpDelegate
        return active.open(dataSpec)
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int =
        active.read(buffer, offset, length)

    override fun addTransferListener(transferListener: androidx.media3.datasource.TransferListener) {
        httpDelegate.addTransferListener(transferListener)
        tdDelegate.addTransferListener(transferListener)
    }

    override fun getUri(): android.net.Uri? = active.uri
    override fun close() { active.close() }
    override fun getResponseHeaders(): Map<String, List<String>> = active.responseHeaders
}
