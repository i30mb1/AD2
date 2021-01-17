package n7.ad2.ui.heroResponse

import android.app.Application
import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.database.ContentObserver
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.util.LongSparseArray
import androidx.core.content.getSystemService
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.core.util.forEach
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import n7.ad2.data.source.local.ResponseRepository
import n7.ad2.ui.heroResponse.domain.vo.VOResponseBody
import java.io.File

sealed class DownloadResult
data class DownloadSuccess(val downloadId: Long) : DownloadResult()
data class DownloadFailed(val error: Throwable) : DownloadResult()

private typealias Result<T> = (T) -> Unit

//  https://youtu.be/-4JqEROeI7U
class DownloadResponseManager(
    private val contentResolver: ContentResolver,
    private val application: Application,
    private val lifecycle: Lifecycle,
) : LifecycleObserver {

    private var downloadId: Long = 0
    private val downloadManager: DownloadManager = application.getSystemService()!!
    private var downloadListener: ((result: DownloadResult) -> Unit)? = null
    private val downloadEndReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            updateDMStatus(downloadId)
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

    fun download(item: VOResponseBody): Long? {
        try {
            val uri = item.audioUrl!!.toUri()
            val downloadRequest = DownloadManager.Request(uri)
                .setAllowedNetworkTypes(DownloadManager.Request.NETWORK_MOBILE)
                .setTitle(item.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_HIDDEN)
                .setDestinationInExternalFilesDir(application, ResponseRepository.DIRECTORY_RESPONSES, item.heroName + File.separator + item.titleForFile)
//                .setVisibleInDownloadsUi(false)

            downloadId = downloadManager.enqueue(downloadRequest)
            registerObserverFor(downloadId, item)

            return downloadId
        } catch (e: Exception) {
            downloadListener?.invoke(DownloadFailed(e))
        }
        return null
    }

    private fun reSaveResponseIn() {
        val resolver = application.contentResolver
        val audioCollection = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY) else MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val responseDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "response.mp3")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val contentUri = resolver.insert(audioCollection, responseDetails)!!
        resolver.openFileDescriptor(contentUri, "w", null).use {
            // write data
        }
        responseDetails.clear()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) responseDetails.put(MediaStore.Audio.Media.IS_PENDING, 0)
        resolver.update(contentUri, responseDetails, null, null)
    }

    private fun registerObserverFor(downloadId: Long, item: VOResponseBody, handler: Handler = Handler(Looper.getMainLooper())) {
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                updateDMStatus(downloadId)
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

    private fun updateDMStatus(downloadId: Long) {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            if (it.count > 0) {
                it.moveToFirst()
                val downloadedBytes = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR))
                val totalBytes = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES))
                val status = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_STATUS))
                val errorCode = it.getIntOrNull(it.getColumnIndex(DownloadManager.COLUMN_REASON))
                when (status) {
                    DownloadManager.STATUS_FAILED -> {
                        downloadListener?.invoke(DownloadFailed(Throwable("Donwload Error Code = $errorCode")))
                        stopProgress(downloadId)
                    }
                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                        hashMap.get(downloadId)?.let { item ->
                            if (totalBytes != null) item.first.maxProgress.value = totalBytes
                            if (downloadedBytes != null) item.first.currentProgress.value = downloadedBytes
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
        hashMap.get(downloadId)?.first?.maxProgress?.value = 0
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