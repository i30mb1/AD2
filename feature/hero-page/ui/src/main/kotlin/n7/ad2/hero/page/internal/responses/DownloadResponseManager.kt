package n7.ad2.hero.page.internal.responses

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
import androidx.core.content.ContextCompat
import androidx.core.content.getSystemService
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.net.toUri
import androidx.core.util.forEach
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.repositories.ResponseRepository
import java.io.File
import java.net.URL

sealed class DownloadResult {
    data class InProgress(val downloadedBytes: Int, val totalBytes: Int, val downloadID: Long) : DownloadResult()
    data class Success(val downloadID: Long) : DownloadResult()
    data class Failed(val error: Throwable) : DownloadResult()
}

private typealias Result<T> = (T) -> Unit

//  https://youtu.be/-4JqEROeI7U
class DownloadResponseManager(private val contentResolver: ContentResolver, private val application: Application, private val lifecycle: Lifecycle) : DefaultLifecycleObserver {

    private val downloadManager: DownloadManager = application.getSystemService()!!
    private var downloadListener: ((result: DownloadResult) -> Unit)? = null
    private val downloadEndReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1L)
            updateDMStatus(downloadId)
        }
    }
    private val hashMap = LongSparseArray<Pair<Long, ContentObserver>>()

    init {
        ContextCompat.registerReceiver(application, downloadEndReceiver, IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE), ContextCompat.RECEIVER_NOT_EXPORTED)
        lifecycle.addObserver(this)
    }

    fun setDownloadListener(listener: Result<DownloadResult>) {
        downloadListener = listener
    }

    fun download(item: VOResponse.Body): Long? {
        try {
            val uri = item.audioUrl.toUri()
            val downloadRequest = DownloadManager.Request(uri)
                .setAllowedOverMetered(true)
                .setTitle(item.title)
                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE)
                .setDestinationInExternalFilesDir(application, ResponseRepository.DIRECTORY_RESPONSES, item.heroName + File.separator + item.titleForFile)
            downloadRequest.allowScanningByMediaScanner()
            val downloadId = downloadManager.enqueue(downloadRequest)
            registerObserverFor(downloadId)
            return downloadId
        } catch (e: Exception) {
            downloadListener?.invoke(DownloadResult.Failed(e))
        }
        return null
    }

    private fun reSaveResponseIn() {
        val resolver = application.contentResolver
        val date = System.currentTimeMillis()
        val audioCollection = if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
            MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        }
        val responseDetails = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, "response.mp3")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.DATE_MODIFIED, date)
            put(MediaStore.Audio.Media.DATE_ADDED, date)
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val outputUri = resolver.insert(audioCollection, responseDetails) ?: error("something wrong with contentProvider")
        URL("").openStream().use { input ->
            resolver.openOutputStream(outputUri).use { output ->
                input.copyTo(output!!)
            }
        }
        resolver.openFileDescriptor(outputUri, "w", null).use {
            // write data
        }
        responseDetails.clear()
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) responseDetails.put(MediaStore.Audio.Media.IS_PENDING, 0)
        resolver.update(outputUri, responseDetails, null, null)
    }

    private fun getResponses() {
        // https://youtu.be/l4bHzLKxyj4?list=PLNSmyatBJig6FwzDx_Uj79Hn-O5xY9oOL
        val resolver = application.contentResolver

        val projection = arrayOf(
            MediaStore.Audio.Media._ID, // получая id сможем построить путь до изображения
            MediaStore.Audio.Media.DISPLAY_NAME,
        )
        val selection = MediaStore.Audio.Media.SIZE + "<=?"
        val selectionArgs = arrayOf("500")
        val sortOrder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
//            MediaStore.Audio.Media.DISPLAY_NAME
            "${MediaStore.MediaColumns.GENERATION_ADDED} DESC"
        } else {
            "${MediaStore.MediaColumns.DATE_ADDED} DESC"
        }
        val cursor = resolver.query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, projection, selection, selectionArgs, sortOrder)
        cursor?.use {
            val idIndex = it.getColumnIndex(MediaStore.Audio.Media._ID)
            val nameIndex = it.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME)
            while (it.moveToNext()) {
                val id = cursor.getString(idIndex)
                val name = cursor.getString(nameIndex)
                val uri = Uri.withAppendedPath(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, id)
                // save somewhere
            }
        }
    }

    private fun registerObserverFor(downloadId: Long, handler: Handler = Handler(Looper.getMainLooper())) {
        val observer = object : ContentObserver(handler) {
            override fun onChange(selfChange: Boolean, uri: Uri?) {
                super.onChange(selfChange, uri)
                updateDMStatus(downloadId)
            }
        }
        hashMap.put(downloadId, Pair(downloadId, observer))
        contentResolver.registerContentObserver(getUri(downloadId), false, observer)
    }

    private fun getUri(downloadId: Long): Uri {
        val request = DownloadManager.Query().setFilterById(downloadId)
        downloadManager.query(request).use {
            if (it.count > 0) {
                it.moveToFirst()
                val index = it.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI)
                return it.getStringOrNull(index)?.toUri() ?: "content://downloads/all_downloads/$downloadId".toUri()
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
                        downloadListener?.invoke(DownloadResult.Failed(Throwable("Download Error Code = $errorCode")))
                    }

                    DownloadManager.STATUS_PAUSED, DownloadManager.STATUS_RUNNING, DownloadManager.STATUS_PENDING -> {
                        if (totalBytes != null && downloadedBytes != null) {
                            downloadListener?.invoke(DownloadResult.InProgress(downloadedBytes, totalBytes, downloadId))
                        }
                    }

                    DownloadManager.STATUS_SUCCESSFUL -> {
                        downloadListener?.invoke(DownloadResult.Success(downloadId))
                        contentResolver.unregisterContentObserver(hashMap.get(downloadId).second)
                    }
                }
            }
        }
    }

    fun getFileDescription(downloadId: Long) {
        val pdf = downloadManager.openDownloadedFile(downloadId)
        val fd = pdf.fileDescriptor
    }

    override fun onDestroy(owner: LifecycleOwner) {
        downloadListener = null
        hashMap.forEach { _, item ->
            contentResolver.unregisterContentObserver(item.second)
        }
        application.unregisterReceiver(downloadEndReceiver)
        lifecycle.removeObserver(this)
    }
}
