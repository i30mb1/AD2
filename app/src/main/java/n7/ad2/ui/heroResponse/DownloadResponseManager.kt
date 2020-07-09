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

class DownloadResponseManager(
        private val contentResolver: ContentResolver,
        private val handler: Handler,
        private val application: Application,
        private val lifecycle: Lifecycle
) : LifecycleObserver {

    private val downloadManager: DownloadManager = application.getSystemService()!!
    private val receiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            checkIf(getDMStatus(downloadId))
        }
    }
    private val observer = object : ContentObserver(handler) {
        override fun onChange(selfChange: Boolean, uri: Uri?) {
            super.onChange(selfChange, uri)

        }
    }

    init {
        lifecycle.addObserver(this)
    }

    fun download(item: VOResponseBody): Long {
        val uri = item.audioUrl!!.toUri()
        val downloadRequest = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(item.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                .setDestinationInExternalFilesDir(application, Environment.DIRECTORY_RINGTONES, "hero/")
//                .setVisibleInDownloadsUi(false)
//                .addRequestHeader()

        return downloadManager.enqueue(downloadRequest)
    }

    fun getUri(downloadId: Long): Uri {
        val request = DownloadManager.Query()
                .setFilterById(downloadId)
        downloadManager.query(request).use {
            val index = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
            return it.getString(index).toUri()
        }
    }

    fun observer(downloadId: Long) {
        contentResolver.registerContentObserver(getUri(downloadId), false, observer)
    }

    fun getDMStatus(downloadId: Long): Int? {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            return if (it.count > 0) it.getInt(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
            else null
        }
    }

    fun checkIf(status: Int?) {
        when (status) {
            null, DownloadManager.STATUS_FAILED -> {
            }
            DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
            }
            DownloadManager.STATUS_SUCCESSFUL -> {
                contentResolver.unregisterContentObserver(observer)
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    private fun onDestroy() {
        lifecycle.removeObserver(this)
    }

}