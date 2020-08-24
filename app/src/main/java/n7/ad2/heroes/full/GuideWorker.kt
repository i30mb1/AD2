package n7.ad2.heroes.full

import android.content.Context
import android.preference.PreferenceManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import n7.ad2.R
import n7.ad2.heroes.db.HeroesRoomDatabase
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.concurrent.Executors

class GuideWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {
    private var heroCodeName: String? = ""
    override fun doWork(): Result {
        heroCodeName = inputData.getString(HERO_CODE_NAME)
        val heroesDao = HeroesRoomDatabase.getDatabase(applicationContext, Executors.newSingleThreadExecutor()).heroesDao()
        val heroes = heroesDao.getHeroByCodeNameObject(heroCodeName)

        //todo check this statement | do not work with nature's prophet
        heroCodeName = heroCodeName!!.replace("_", "-").replace("'", "")
        val stringWinRate = StringBuilder()
        val stringPickRate = StringBuilder()
        val stringLane = StringBuilder()
        val stringTime = StringBuilder()
        val stringStartingItems = StringBuilder()
        val stringFurtherItems = StringBuilder()
        val stringSkillBuilds = StringBuilder()
        val stringBestVersus = StringBuilder()
        val stringWorstVersus = StringBuilder()
        try {
            val documentSimple = Jsoup.connect("https://ru.dotabuff.com/heroes/$heroCodeName").get()
            // BEST VERSUS
            if (documentSimple.getElementsByTag("tbody").size >= 3 && documentSimple.getElementsByTag("tbody")[3].children().size > 0) {
                val best = documentSimple.getElementsByTag("tbody")[3].children()
                for (element in best) {
                    stringBestVersus.append(element.child(1).text().toLowerCase().trim { it <= ' ' }.replace(" ", "_"))
                        .append("^").append(element.child(2).text()).append("/")
                }
            }
            // WORST VERSUS
            if (documentSimple.getElementsByTag("tbody").size >= 4 && documentSimple.getElementsByTag("tbody")[4].children().size > 0) {
                val worst = documentSimple.getElementsByTag("tbody")[4].children()
                for (element in worst) {
                    stringWorstVersus.append(element.child(1).text().toLowerCase().trim { it <= ' ' }.replace(" ", "_"))
                        .append("^").append(element.child(2).text()).append("/")
                }
            }
            val document = Jsoup.connect("https://www.dotabuff.com/heroes/$heroCodeName/guides").get()
            // WIN RATE
            val winrate = document.getElementsByClass("won")
            val loserate = document.getElementsByClass("lost")
            if (winrate.size > 0) {
                stringWinRate.append(winrate[0].text())
            }
            if (loserate.size > 0) {
                stringWinRate.append(loserate[0].text())
            }
            // PICK RATE
            val pickrate = document.getElementsByTag("dd")
            if (pickrate.size > 0) {
                stringPickRate.append(pickrate[0].text())
            }
            // 5 DIFFERENT PACKS
            val elements = document.getElementsByClass("r-stats-grid")
            if (elements.size > 0) {
                for (element in elements) {
                    // TIME LAST PLAY
                    val time = element.getElementsByTag("time")
                    if (time.size > 0) {
                        if (stringTime.length != 0) {
                            stringTime.append("+")
                        }
                        stringTime.append(time.text())
                    }
                    // LANE
                    if (stringLane.length != 0) {
                        stringLane.append("+")
                    }
                    stringLane.append(detectLine(element))
                    // ITEMS
                    if (element.children().size >= 2) {
                        val elementsItemRows = element.child(2).getElementsByClass("kv r-none-mobile")
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
                        if (stringFurtherItems.length != 0) {
                            stringFurtherItems.append("+")
                        }
                        for (i in elementsItemRows.indices) {
                            var findTime = false
                            if (i == 0 && elementsItemRows[i].children().size > 2) continue
                            for (item in elementsItemRows[i].children()) {
                                if (item.tag().toString() == "div") {
                                    val itemName = item.child(0).child(0).child(0).attr("title").toLowerCase().trim { it <= ' ' }.replace(" ", "_")
                                    if (itemName == "gem_of_true_sight") continue
                                    stringFurtherItems.append(itemName)
                                    if (i != 0) if (!findTime && elementsItemRows[i].getElementsByTag("small").size > 0) {
                                        findTime = true
                                        stringFurtherItems.append("^").append(elementsItemRows[i].getElementsByTag("small")[0].text())
                                    }
                                }
                                if (!stringFurtherItems.toString().endsWith("/")) stringFurtherItems.append("/")
                            }
                        }
                    }
                    // SKILL BUILD
                    if (stringSkillBuilds.length != 0) {
                        stringSkillBuilds.append("+")
                    }
                    if (element.children().size >= 3) {
                        val skills = element.child(3).getElementsByClass("kv kv-small-margin")
                        for (skill in skills) {
                            var skillName = skill.child(0).child(0).child(0).attr("alt")
                            if (skillName.startsWith("Talent:")) skillName = "talent"
                            stringSkillBuilds.append(skillName).append("/")
                        }
                    }
                }
            }
            // SAVE DATA
            heroes.furtherItems = stringFurtherItems.toString().replace("'", "%27")
            heroes.startingItems = stringStartingItems.toString().replace("'", "%27")
            heroes.pickrate = stringPickRate.toString()
            heroes.lane = stringLane.toString()
            heroes.winrate = stringWinRate.toString()
            heroes.time = stringTime.toString()
            heroes.skillBuilds = stringSkillBuilds.toString()
            heroes.bestVersus = stringBestVersus.toString()
            heroes.worstVersus = stringWorstVersus.toString()
            heroes.guideLastDay = PreferenceManager.getDefaultSharedPreferences(applicationContext).getInt(applicationContext.getString(R.string.setting_current_day), 0)
            heroesDao.update(heroes)
        } catch (e: Throwable) {
            return Result.failure()
        }
        return Result.success()
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

    companion object {
        const val UNIQUE_WORK = "UNIQUE_GUIDE_WORK"
        const val HERO_CODE_NAME = "HERO_CODE_NAME"
    }
}