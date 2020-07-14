package n7.ad2.ui.heroResponse

import android.app.Application
import android.app.DownloadManager
import android.content.*
import android.database.ContentObserver
import android.net.Uri
import android.os.Environment
import android.os.Handler
import androidx.core.content.getSystemService
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import java.io.File

sealed class DownloadResult
data class DownloadSuccess(val downloadId: Long) : DownloadResult()
data class DownloadInProgress(val downloadedBytes: Int, val totalBytes: Int) : DownloadResult()
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

    private var downloadId: Long = 0
    private var currentItem: VOResponseBody? = null
    private val downloadManager: DownloadManager = application.getSystemService()!!
    private var downloadListener: ((result: DownloadResult) -> Unit)? = null
    private val downloadEndReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            getDMStatus(downloadId)
        }
    }
    private val observer = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)
            updateProgress()
        }
    }

    init {
        application.registerReceiver(downloadEndReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        lifecycle.addObserver(this)
    }

    fun setDownloadListener(listener: (result: DownloadResult) -> Unit) {
        downloadListener = listener
    }

    fun download(item: VOResponseBody): Long {
        stopProgress()
        currentItem = item

        val uri = item.audioUrl!!.toUri()
        val downloadRequest = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(item.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(application, DIRECTORY, item.heroName + File.separator + item.titleForFile)
//                .setVisibleInDownloadsUi(false)
//                .addRequestHeader()

        downloadId = downloadManager.enqueue(downloadRequest)
        registerObserverFor(downloadId)

        return downloadId
    }

    private fun registerObserverFor(downloadId: Long) {
        contentResolver.registerContentObserver(getUri(downloadId), false, observer)
    }

    private fun getUri(downloadId: Long): Uri {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            if (it.count > 0) {
                it.moveToFirst()
                val index = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                return it.getStringOrNull(index)?.toUri()
                        ?: "content://downloads/all_downloads/$downloadId".toUri()
            }
            return "content://downloads/all_downloads/$downloadId".toUri()
        }
    }

    private fun getDMStatus(downloadId: Long) {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            if (it.count > 0) {
                it.moveToFirst()
                val downloadedBytes = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val totalBytes = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val status = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        downloadListener?.invoke(DownloadFailed)
                        stopProgress()
                    }
                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
//                        downloadListener?.invoke(DownloadInProgress(downloadedBytes, totalBytes))
                        currentItem?.let { item ->
                            if (totalBytes != null) item.maxProgress.set(totalBytes)
                            if (downloadedBytes != null) item.currentProgress.set(downloadedBytes)
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        stopProgress()
                        downloadListener?.invoke(DownloadSuccess(downloadId))
                        contentResolver.unregisterContentObserver(observer)
                    }
                }
            }
        }
    }

    private fun stopProgress() {
        currentItem?.let {
            it.maxProgress.set(0)
        }
    }

    private fun updateProgress() {
        getDMStatus(downloadId)
    }

    fun getFileDescription(downloadId: Long) {
        val pdf = downloadManager.openDownloadedFile(downloadId)
        val fd = pdf.fileDescriptor
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        downloadListener = null
        contentResolver.unregisterContentObserver(observer)
        application.unregisterReceiver(downloadEndReceiver)
        lifecycle.removeObserver(this)
    }

}