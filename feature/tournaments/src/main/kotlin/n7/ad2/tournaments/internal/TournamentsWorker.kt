package n7.ad2.tournaments.internal

import android.content.Context
import android.content.Intent
import androidx.work.Worker
import androidx.work.WorkerParameters
import n7.ad2.tournaments.internal.db.GamesRoomDatabase
import n7.ad2.tournaments.internal.db.TournamentGame
import org.jsoup.Jsoup
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TournamentsWorker(context: Context, workerParams: WorkerParameters) : Worker(context, workerParams) {

    companion object {
        const val DELETE_TABLE = "delete_table"
        const val PAGE = "page"
        const val TAG = "tournaments_worker_tag"
        private const val BASE_URL = "https://dota2.ru/esport/matches/?page="
        private const val TOURNAMENT_LOG_ACTION = "n7.ad2.tournaments.LOG_ON_RECEIVE"
    }

    override fun doWork(): Result {
        val deleteTable = inputData.getBoolean(DELETE_TABLE, false)
        val page = inputData.getInt(PAGE, 0)

        val gamesList = mutableListOf<TournamentGame>()

        return try {
            val url = BASE_URL + page
            val doc = Jsoup.connect(url).get()
            val matchElements = doc.getElementsByClass("esport-match-single")

            for (j in matchElements.indices) {
                val game = TournamentGame()
                game.url = "https://dota2.ru" + matchElements[j].child(0).attr("href")
                val matchElementsChildren = matchElements[j].child(1).children()

                for (i in matchElementsChildren.indices) {
                    val element = matchElementsChildren[i]
                    when (element.attr("class")) {
                        "team team-left" -> {
                            for (childElement in element.children()) {
                                when (childElement.attr("class")) {
                                    "name" -> game.team1Name = childElement.text()
                                }
                                if (childElement.tagName() == "img") {
                                    game.team1Logo = "https://dota2.ru" + childElement.attr("src")
                                }
                            }
                        }

                        "status" -> {
                            for (childElement in element.children()) {
                                when (childElement.attr("class")) {
                                    "score match-shop-result" -> game.teamScore = childElement.attr("data-value")
                                    "time" -> {
                                        val simpleDateFormat = SimpleDateFormat("HH:mm dd.MM.yyyy", Locale("ru", "RU"))
                                        val matchStart = simpleDateFormat.parse(
                                            childElement.text().trim() +
                                                SimpleDateFormat(".yyyy", Locale("ru", "RU")).format(Date()),
                                        )?.time ?: 0L
                                        val currentTime = simpleDateFormat.parse(simpleDateFormat.format(Date()))?.time ?: 0L
                                        val time = (matchStart - currentTime) / 1000
                                        game.teamTimeRemains = time
                                        game.teamTime = matchStart
                                    }

                                    "live" -> game.teamScore = "LIVE"
                                }
                            }
                        }

                        "team team-right" -> {
                            for (childElement in element.children()) {
                                when (childElement.attr("class")) {
                                    "name" -> game.team2Name = childElement.text()
                                }
                                if (childElement.tagName() == "img") {
                                    game.team2Logo = "https://dota2.ru" + childElement.attr("src")
                                }
                            }
                        }
                    }
                }
                gamesList.add(game)
            }

            if (gamesList.isEmpty()) throw Exception("No games found")

            val gamesDao = GamesRoomDatabase.getDatabase(applicationContext).gamesDao()
            if (deleteTable && matchElements.isNotEmpty()) {
                gamesDao.deleteAll()
            }
            gamesDao.setGames(gamesList)

            applicationContext.sendBroadcast(
                Intent(TOURNAMENT_LOG_ACTION).putExtra(TOURNAMENT_LOG_ACTION, "page_${page}_loaded"),
            )

            Result.success()
        } catch (e: Throwable) {
            val gamesDao = GamesRoomDatabase.getDatabase(applicationContext).gamesDao()
            gamesDao.deleteAllUnfinished()
            applicationContext.sendBroadcast(
                Intent(TOURNAMENT_LOG_ACTION).putExtra(TOURNAMENT_LOG_ACTION, "page_${page}_failed"),
            )
            Result.failure()
        }
    }
}
