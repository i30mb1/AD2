package n7.ad2.repositories

import n7.ad2.AppLocale
import n7.ad2.Resources
import java.io.File
import javax.inject.Inject

class ResponseRepository @Inject constructor(private val res: Resources) {

    companion object {
        val DIRECTORY_RESPONSES: String = android.os.Environment.DIRECTORY_RINGTONES
    }

    fun getHeroResponses(heroName: String, appLocale: AppLocale): String = res.getAssets("heroes/$heroName/${appLocale.value}/responses.json").bufferedReader().use {
        it.readText()
    }

    fun getSavedHeroResponses(heroName: String): List<File> = res.getExternalFilesDir(DIRECTORY_RESPONSES + File.separator + heroName)?.listFiles()?.toList() ?: emptyList()
}
