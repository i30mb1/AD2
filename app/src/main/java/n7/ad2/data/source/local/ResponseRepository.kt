package n7.ad2.data.source.local

import android.app.Application
import java.io.File
import javax.inject.Inject

class ResponseRepository @Inject constructor(
    private val application: Application,
) {

    companion object {
        val DIRECTORY_RESPONSES: String = android.os.Environment.DIRECTORY_RINGTONES
    }

    fun getHeroResponses(assetsPath: String, locale: Locale): String {
        return application.assets.open("heroes/$assetsPath/${locale.folderName}/responses.json").bufferedReader().use {
            it.readText()
        }
    }

    fun getSavedHeroResponses(heroName: String): Array<File> {
        return application.getExternalFilesDir(DIRECTORY_RESPONSES + File.separator + heroName)?.listFiles() ?: emptyArray()
    }

}