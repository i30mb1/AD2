@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.ui.heroGuide

import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.squareup.moshi.Moshi
import kotlinx.coroutines.coroutineScope
import n7.ad2.R
import n7.ad2.createNotificationChannel
import n7.ad2.data.source.local.model.LocalGuide
import n7.ad2.isChannelNotCreated
import n7.ad2.ui.MyApplication
import n7.ad2.ui.heroGuide.domain.model.DetailedGuide
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.usecase.ConvertLocalGuideJsonToLocalGuide
import n7.ad2.ui.heroGuide.domain.usecase.SaveLocalGuideUseCase
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*
import javax.inject.Inject


class HeroGuideWorker(
    val context: Context,
    workerParameters: WorkerParameters,
) : CoroutineWorker(context, workerParameters) {

    companion object {
        const val HERO_NAME = "HERO_NAME"
        const val RESULT = "RESULT"
        private fun getUrlForHeroPage(heroName: String) = "https://ru.dotabuff.com/heroes/$heroName"
    }

    private val notificationId = 1
    private val channelId = "guide_worker"
    private val channelName = "Guide"
    private val notificationTitle = applicationContext.getString(R.string.notification_title_guide)

    @Inject
    lateinit var saveLocalGuideUseCase: SaveLocalGuideUseCase

    @Inject
    lateinit var convertLocalGuideJsonToLocalGuide: ConvertLocalGuideJsonToLocalGuide

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@HeroGuideWorker)

        createNotification()
        val heroName = inputData.getString(HERO_NAME)!!

        try {
            val heroNameForUrl = heroName.replace("_", "-").replace("'", "").toLowerCase(Locale.ENGLISH)

            val documentSimple = Jsoup.connect(getUrlForHeroPage(heroNameForUrl)).get()

            // BEST VERSUS
            val heroesBestVersus = mutableListOf<String>()
            if (documentSimple.getElementsByTag("tbody").size >= 3 && documentSimple.getElementsByTag("tbody")[3].children().size > 0) {
                val best = documentSimple.getElementsByTag("tbody")[3].children()
                for (element in best) {
                    val name = element.child(1).text()
                    val heroWinrate = element.child(2).text()
                    heroesBestVersus.add(name)
                }
            }
            // WORST VERSUS
            val heroesWorstVersus = mutableListOf<String>()
            if (documentSimple.getElementsByTag("tbody").size >= 4 && documentSimple.getElementsByTag("tbody")[4].children().size > 0) {
                val worst = documentSimple.getElementsByTag("tbody")[4].children()
                for (element in worst) {
                    val name = element.child(1).text()
                    val heroWinrate = element.child(2).text()
                    heroesWorstVersus.add(name)
                }
            }

            val document = Jsoup.connect("https://www.dotabuff.com/heroes/$heroNameForUrl/guides").get()
            // WIN RATE
            val heroWinrate = document.getElementsByClass("won")[0].text()
            val popularity = document.getElementsByAttributeValue("class", "header-content-secondary")[0].child(0).child(0).text()

            // 5 DIFFERENT PACKS
            var guideTime = ""
            var lane = ""
            val itemTime = mutableListOf<String>()
            val elements = document.getElementsByClass("r-stats-grid")
            if (elements.size > 0) {
                for (element in elements) {
                    if (itemTime.size > 0) break
                    guideTime = element.getElementsByTag("time").text()
                    // LANE
                    lane = detectLine(element)
//                    // ITEMS
//                    if (element.children().size >= 2) {
//                        val elementsItemRows = element.child(2).getElementsByClass("kv r-none-mobile")
                    // STARTING ITEMS
//                Element startingItems = elementsItemRows.get(0);
//                if (stringStartingItems.length() != 0) {
//                    stringStartingItems.append("+");
//                }
//                for (Element item : startingItems.children()) {
//                    if (item.tag().toString().endsWith("div")) {
//                        stringStartingItems.append(item.child(0).child(0).child(0).attr("title").toLowerCase().trim().replace(" ", "_"));
//                        stringStartingItems.append("/");
//                    }
//                }
                    // FURTHER ITEMS
//                        if (stringFurtherItems.length != 0) {
//                            stringFurtherItems.append("+")
//                        }
//                        for (i in elementsItemRows.indices) {
//                            var findTime = false
//                            if (i == 0 && elementsItemRows[i].children().size > 2) continue
//                            for (item in elementsItemRows[i].children()) {
//                                if (item.tag().toString() == "div") {
//                                    val itemName = item.child(0).child(0).child(0).attr("title").toLowerCase().trim { it <= ' ' }.replace(" ", "_")
//                                    if (itemName == "gem_of_true_sight") continue
//                                    stringFurtherItems.append(itemName)
//                                    if (i != 0) if (!findTime && elementsItemRows[i].getElementsByTag("small").size > 0) {
//                                        findTime = true
//                                        stringFurtherItems.append("^").append(elementsItemRows[i].getElementsByTag("small")[0].text())
//                                    }
//                                }
//                                if (!stringFurtherItems.toString().endsWith("/")) stringFurtherItems.append("/")
//                            }
//                        }
//                    }
                    // SKILL BUILD
//                    if (stringSkillBuilds.length != 0) {
//                        stringSkillBuilds.append("+")
//                    }
//                    if (element.children().size >= 3) {
//                        val skills = element.child(3).getElementsByClass("kv kv-small-margin")
//                        for (skill in skills) {
//                            var skillName = skill.child(0).child(0).child(0).attr("alt")
//                            if (skillName.startsWith("Talent:")) skillName = "talent"
//                            stringSkillBuilds.append(skillName).append("/")
//                        }
//                    }
                }
            }

            val localGuideJson = LocalGuideJson(
                heroName,
                heroWinrate,
                popularity,
                heroesBestVersus,
                heroesWorstVersus,
                DetailedGuide(
                    guideTime,
                    emptyList(),
                    emptyList()
                )
            )

            val localGuide = convertLocalGuideJsonToLocalGuide(localGuideJson, heroName)
            saveLocalGuideUseCase(localGuide)

            Result.success()
        } catch (e: Exception) {
            val error = workDataOf(RESULT to e.toString())
            Result.failure(error)
        }
    }

    private suspend fun createNotification() {
        if (NotificationManagerCompat.from(applicationContext).areNotificationsEnabled()) {
            if (applicationContext.isChannelNotCreated(channelId)) applicationContext.createNotificationChannel(channelId, channelName)

            val notification = NotificationCompat.Builder(applicationContext, channelId)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle(notificationTitle)
                .build()
            setForeground(ForegroundInfo(notificationId, notification))
        }
    }

    private fun detectLine(element: Element): String {
        val elementsMid = element.getElementsByClass("fa fa-lane-midlane fa-fw lane-icon midlane-icon")
        if (elementsMid.size == 1) return "MID LANE"
        val elementsOff = element.getElementsByClass("fa fa-lane-offlane fa-fw lane-icon offlane-icon")
        if (elementsOff.size == 1) return "OFF LANE"
        val elementsSafe = element.getElementsByClass("fa fa-lane-safelane fa-fw lane-icon safelane-icon")
        if (elementsSafe.size == 1) return "SAFE LANE"
        val elementsRoaming = element.getElementsByClass("fa fa-lane-roaming fa-fw lane-icon roaming-icon")
        return if (elementsRoaming.size == 1) "ROAMING" else "-"
    }

}