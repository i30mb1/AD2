package n7.ad2.hero.page.internal.responses

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import androidx.core.net.toUri
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import java.io.File
import java.io.InputStream
import java.net.URL

/**
 * Sets a response as the phone's call / notification / alarm sound.
 *
 * The system can't read files from the app's own folder, so the audio is first put into the shared
 * media store (Ringtones / Notifications / Alarms folder), and only that copy is set as the default.
 * Setting the default needs the "modify system settings" permission the user grants on a separate screen.
 */
internal class ResponseRingtoneSetter(private val context: Context) {

    enum class Target(val ringtoneType: Int, val directory: String, val flagColumn: String) {
        CALL(RingtoneManager.TYPE_RINGTONE, Environment.DIRECTORY_RINGTONES, MediaStore.Audio.Media.IS_RINGTONE),
        NOTIFICATION(RingtoneManager.TYPE_NOTIFICATION, Environment.DIRECTORY_NOTIFICATIONS, MediaStore.Audio.Media.IS_NOTIFICATION),
        ALARM(RingtoneManager.TYPE_ALARM, Environment.DIRECTORY_ALARMS, MediaStore.Audio.Media.IS_ALARM),
    }

    fun canWriteSettings(): Boolean = Settings.System.canWrite(context)

    fun writeSettingsIntent(): Intent = Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS, "package:${context.packageName}".toUri())
        .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    suspend fun set(response: VOResponse.Body, target: Target) = withContext(Dispatchers.IO) {
        val uri = findInMediaStore(response.titleForFile, target) ?: insertIntoMediaStore(response, target)
        RingtoneManager.setActualDefaultRingtoneUri(context, target.ringtoneType, uri)
    }

    private fun relativePath(target: Target) = target.directory + File.separator + "AD2" + File.separator

    /** The same response set twice should not leave two copies in the user's ringtone list. */
    private fun findInMediaStore(fileName: String, target: Target): Uri? {
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME}=? AND ${MediaStore.Audio.Media.RELATIVE_PATH}=?"
        context.contentResolver.query(collection, arrayOf(MediaStore.Audio.Media._ID), selection, arrayOf(fileName, relativePath(target)), null)?.use { cursor ->
            if (cursor.moveToFirst()) return ContentUris.withAppendedId(collection, cursor.getLong(0))
        }
        return null
    }

    private fun insertIntoMediaStore(response: VOResponse.Body, target: Target): Uri {
        val resolver = context.contentResolver
        val collection = MediaStore.Audio.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        val details = ContentValues().apply {
            put(MediaStore.Audio.Media.DISPLAY_NAME, response.titleForFile)
            put(MediaStore.Audio.Media.TITLE, "${response.heroName}: ${response.title}")
            put(MediaStore.Audio.Media.MIME_TYPE, "audio/mpeg")
            put(MediaStore.Audio.Media.RELATIVE_PATH, relativePath(target))
            put(target.flagColumn, 1)
            put(MediaStore.Audio.Media.IS_PENDING, 1)
        }
        val uri = resolver.insert(collection, details) ?: error("media store refused to create ${response.titleForFile}")
        try {
            openSource(response).use { input ->
                resolver.openOutputStream(uri).use { output -> input.copyTo(requireNotNull(output)) }
            }
        } catch (error: Exception) {
            resolver.delete(uri, null, null)
            throw error
        }
        resolver.update(uri, ContentValues().apply { put(MediaStore.Audio.Media.IS_PENDING, 0) }, null, null)
        return uri
    }

    // a downloaded response points at the local file, otherwise it is streamed straight from the wiki
    private fun openSource(response: VOResponse.Body): InputStream =
        if (response.isSavedInMemory) File(response.audioUrl).inputStream() else URL(response.audioUrl).openStream()
}
