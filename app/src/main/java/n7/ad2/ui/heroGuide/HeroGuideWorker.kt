@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.ui.heroGuide

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import kotlinx.coroutines.coroutineScope
import n7.ad2.data.source.local.model.LocalGuide
import n7.ad2.ui.MyApplication
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJsonModel
import n7.ad2.ui.heroGuide.domain.usecase.SaveLocalGuideUseCase
import org.jsoup.Jsoup
import java.util.*
import javax.inject.Inject


class HeroGuideWorker(
    val context: Context,
    workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val HERO_NAME = "HERO_NAME"
    }

    @Inject
    lateinit var saveLocalGuideUseCase: SaveLocalGuideUseCase

    @Inject
    lateinit var moshi: Moshi

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@HeroGuideWorker)

        try {
            val heroName = inputData.getString(HERO_NAME)!!
            val heroNameForWeb = heroName.replace("_", "-").replace("'", "").toLowerCase(Locale.ENGLISH)

            val documentSimple = Jsoup.connect("https://ru.dotabuff.com/heroes/$heroNameForWeb").get()

            // BEST VERSUS
            val list = mutableListOf<String>()
            if (documentSimple.getElementsByTag("tbody").size >= 3 && documentSimple.getElementsByTag("tbody")[3].children().size > 0) {
                val best = documentSimple.getElementsByTag("tbody")[3].children()
                for (element in best) {
                    val heroBestName = element.child(1).text()
                    val heroBestWinrate = element.child(2).text()
                    list.add(heroBestName)
                }
            }

            val localGuideJsonModel = LocalGuideJsonModel(
                heroName,
                "50",
                "50",
                list,
                emptyList(),
                emptyList(),
                emptyList()
            )

            val json = moshi.adapter(LocalGuideJsonModel::class.java).toJson(localGuideJsonModel)
            val localGuide = LocalGuide(name = heroName, json = json)

            saveLocalGuideUseCase(localGuide)
            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}