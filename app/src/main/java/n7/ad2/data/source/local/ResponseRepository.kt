package n7.ad2.data.source.local

import android.app.Application
import java.io.File
import javax.inject.Inject

enum class ResponseLocale(val value: String) {
    RU("ru"), ENG("eng")
}

class ResponseRepository @Inject constructor(
    private val application: Application
) {

    companion object {
        private const val ASSETS_PATH_HERO_RESPONSES = "responses.json"
        val DIRECTORY_RESPONSES: String = android.os.Environment.DIRECTORY_RINGTONES
    }

    fun getHeroResponses(assetsPath: String, locale: ResponseLocale): String {
        return application.assets.open("$assetsPath/${locale.value}/${ASSETS_PATH_HERO_RESPONSES}").bufferedReader().use {
            it.readText()
        }
    }

    fun getSavedHeroResponses(heroName: String): Array<File> {
        return application.getExternalFilesDir(DIRECTORY_RESPONSES + File.separator + heroName)?.listFiles() ?: emptyArray()
    }

}