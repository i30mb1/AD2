package n7.ad2.ui.heroResponse

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.database.ContentObserver
import android.net.Uri
import android.os.Environment
import android.os.Handler
import androidx.core.content.getSystemService
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import java.io.File

sealed class DownloadResult
data class DownloadSuccess(val downloadId: Long) : DownloadResult()
object DownloadFailed : DownloadResult()

class DownloadResponseManager(
        private val contentResolver: ContentResolver,
        private val handler: Handler,
        private val application: Application,
        private val lifecycle: Lifecycle
) : LifecycleObserver {

    companion object {
        val DIRECTORY = Environment.DIRECTORY_RINGTONES
    }

    private val downloadManager: DownloadManager = application.getSystemService()!!
    private var downloadListener: ((result: DownloadResult) -> Unit)? = null
    private val downloadEndReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            actionOnStatus(downloadId)
        }
    }
    private val observer = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            // опрашиваем downloadManager и прокидываем полученнные значения в UI
        }
    }

    init {
        lifecycle.addObserver(this)
    }

    fun setDownloadListener(listener: (result: DownloadResult) -> Unit) {
        downloadListener = listener
    }

    fun download(item: VOResponseBody): Long {
        val uri = item.audioUrl!!.toUri()
        val downloadRequest = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(item.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(application, DIRECTORY, item.heroName + File.separator + item.titleForFile)
//                .setVisibleInDownloadsUi(false)
//                .addRequestHeader()

        return downloadManager.enqueue(downloadRequest)
    }

    private fun getUri(downloadId: Long): Uri {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            val index = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            return it.getString(index).toUri()
        }
    }

    fun registerObserverFor(downloadId: Long) {
        contentResolver.registerContentObserver(getUri(downloadId), false, observer)
    }

    private fun getDMStatus(downloadId: Long): Int? {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            return if (it.count > 0) {
                val downloadedBytes = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val totalBytes = it.getInt(it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                it.getInt(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
            } else null
        }
    }

    fun getFileDescription(downloadId: Long) {
        val pdf = downloadManager.openDownloadedFile(downloadId)
        val fd = pdf.fileDescriptor
    }

    fun actionOnStatus(downloadId: Long) {
        when (getDMStatus(downloadId)) {
            null, DownloadManager.STATUS_FAILED -> {
                downloadListener?.invoke(DownloadFailed)
            }
            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {

            }
            DownloadManager.STATUS_SUCCESSFUL -> {
                downloadListener?.invoke(DownloadSuccess(downloadId))
                contentResolver.unregisterContentObserver(observer)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        downloadListener = null
        lifecycle.removeObserver(this)
    }

}