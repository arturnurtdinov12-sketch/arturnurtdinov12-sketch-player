package com.tgplayer.app.data.remote

import android.net.Uri
import androidx.media3.common.C
import androidx.media3.datasource.BaseDataSource
import androidx.media3.datasource.DataSource
import androidx.media3.datasource.DataSpec
import androidx.media3.datasource.TransferListener
import kotlinx.coroutines.runBlocking
import java.io.RandomAccessFile

/**
 * DataSource that reads bytes from a TDLib-managed local file. Triggers a
 * download for the corresponding TDLib file id and waits until enough bytes are
 * available to satisfy the read.
 *
 * URI format: tdfile://<fileId>
 */
class TdLibAudioDataSource(
    private val telegram: TelegramClient
) : BaseDataSource(/* isNetwork = */ true) {

    private var uri: Uri? = null
    private var raf: RandomAccessFile? = null
    private var bytesRemaining: Long = 0L

    override fun open(dataSpec: DataSpec): Long {
        transferInitializing(dataSpec)
        uri = dataSpec.uri
        val fileId = dataSpec.uri.host?.toIntOrNull()
            ?: dataSpec.uri.lastPathSegment?.toIntOrNull()
            ?: throw IllegalArgumentException("Bad TDLib URI: $uri")

        val path = runBlocking { telegram.downloadFile(fileId, priority = 32, synchronous = true) }
            ?: throw IllegalStateException("TDLib download failed for fileId=$fileId")
        val file = RandomAccessFile(path, "r")
        if (dataSpec.position > 0) file.seek(dataSpec.position)
        raf = file
        val total = file.length() - dataSpec.position
        bytesRemaining = if (dataSpec.length == C.LENGTH_UNSET.toLong()) total else minOf(dataSpec.length, total)
        transferStarted(dataSpec)
        return bytesRemaining
    }

    override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
        if (bytesRemaining == 0L) return C.RESULT_END_OF_INPUT
        val toRead = minOf(length.toLong(), bytesRemaining).toInt()
        val n = raf?.read(buffer, offset, toRead) ?: return C.RESULT_END_OF_INPUT
        if (n > 0) {
            bytesRemaining -= n
            bytesTransferred(n)
        }
        return n
    }

    override fun getUri(): Uri? = uri

    override fun close() {
        if (raf != null) {
            raf?.close()
            raf = null
            transferEnded()
        }
    }
}

class TdLibAudioDataSourceFactory(
    private val telegram: TelegramClient
) : DataSource.Factory {

    private var listener: TransferListener? = null

    override fun createDataSource(): DataSource {
        val ds = TdLibAudioDataSource(telegram)
        listener?.let { ds.addTransferListener(it) }
        return ds
    }

    fun setTransferListener(transferListener: TransferListener?) {
        listener = transferListener
    }
}
