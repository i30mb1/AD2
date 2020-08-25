@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.ui.heroGuide

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import n7.ad2.data.source.local.model.LocalGuide
import n7.ad2.ui.MyApplication
import n7.ad2.ui.heroGuide.domain.usecase.SaveLocalGuideUseCase
import org.jsoup.Jsoup
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

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@HeroGuideWorker)

        try {
            val heroName = inputData.getString(HERO_NAME)!!.replace("_", "-").replace("'", "")

            val documentSimple = Jsoup.connect("https://ru.dotabuff.com/heroes/$heroName").get()

            val localGuide = LocalGuide(name = heroName, json = documentSimple.toString())

            Result.success()
        } catch (e: Exception) {
            Result.failure()
        }
    }

}