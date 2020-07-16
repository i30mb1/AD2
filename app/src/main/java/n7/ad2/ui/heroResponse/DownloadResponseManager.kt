package n7.ad2.ui.heroResponse

import android.app.Application
import android.app.DownloadManager
import android.content.*
import android.database.ContentObserver
import android.net.Uri
import android.os.Environment
import android.os.Handler
import android.util.LongSparseArray
import androidx.core.content.getSystemService
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.core.util.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import java.io.File

sealed class DownloadResult
data class DownloadSuccess(val downloadId: Long) : DownloadResult()
object DownloadFailed : DownloadResult()

private typealias Result<T> = (T) -> Unit

class DownloadResponseManager(
        private val contentResolver: ContentResolver,
        private val handler: Handler,
        private val application: Application,
        private val lifecycle: Lifecycle
) : LifecycleObserver {

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
    private val hashMap = LongSparseArray<Pair<VOResponseBody, ContentObserver>>()

    init {
        application.registerReceiver(downloadEndReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE))
        lifecycle.addObserver(this)
    }

    fun setDownloadListener(listener: Result<DownloadResult>) {
        downloadListener = listener
    }

    fun download(item: VOResponseBody): Long {
        currentItem = item

        val uri = item.audioUrl!!.toUri()
        val downloadRequest = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(item.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(application, Repository.DIRECTORY_RESPONSES, item.heroName + File.separator + item.titleForFile)
//                .setVisibleInDownloadsUi(false)
//                .addRequestHeader()

        downloadId = downloadManager.enqueue(downloadRequest)
        registerObserverFor(downloadId, item)

        return downloadId
    }

    private fun registerObserverFor(downloadId: Long, item: VOResponseBody) {
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                updateProgress(downloadId)
            }
        }
        hashMap.put(downloadId, Pair(item, observer))
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
                        stopProgress(downloadId)
                    }
                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                        hashMap.get(downloadId)?.let { item ->
                            if (totalBytes != null) item.first.maxProgress.set(totalBytes)
                            if (downloadedBytes != null) item.first.currentProgress.set(downloadedBytes)
                        }
                    }
                    DownloadManager.STATUS_SUCCESSFUL -> {
                        stopProgress(downloadId)
                        downloadListener?.invoke(DownloadSuccess(downloadId))
                        contentResolver.unregisterContentObserver(hashMap.get(downloadId).second)
                    }
                }
            }
        }
    }

    private fun stopProgress(downloadId: Long) {
        hashMap.get(downloadId)?.first?.maxProgress?.set(0)
    }

    private fun updateProgress(downloadId: Long) {
        getDMStatus(downloadId)
    }

    fun getFileDescription(downloadId: Long) {
        val pdf = downloadManager.openDownloadedFile(downloadId)
        val fd = pdf.fileDescriptor
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        downloadListener = null
        hashMap.forEach { _, item ->
            contentResolver.unregisterContentObserver(item.second)
        }
        application.unregisterReceiver(downloadEndReceiver)
        lifecycle.removeObserver(this)
    }

}