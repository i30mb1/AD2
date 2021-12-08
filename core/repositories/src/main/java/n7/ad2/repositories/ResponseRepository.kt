package n7.ad2.repositories

import android.app.Application
import n7.ad2.android.Locale
import java.io.File
import javax.inject.Inject

class ResponseRepository @Inject constructor(
    private val application: Application,
) {

    companion object {
        val DIRECTORY_RESPONSES: String = android.os.Environment.DIRECTORY_RINGTONES
    }

    fun getHeroResponses(heroName: String, locale: Locale): String {
        return application.assets.open("heroes/$heroName/${locale.folderName}/responses.json").bufferedReader().use {
            it.readText()
        }
    }

    fun getSavedHeroResponses(heroName: String): Array<File> {
        return application.getExternalFilesDir(DIRECTORY_RESPONSES + File.separator + heroName)?.listFiles() ?: emptyArray()
    }

}