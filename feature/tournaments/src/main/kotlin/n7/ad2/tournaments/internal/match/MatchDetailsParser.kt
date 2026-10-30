package n7.ad2.tournaments.internal.match

import n7.ad2.tournaments.internal.data.MatchStatus
import org.json.JSONArray
import org.json.JSONObject

/**
 * The match page of dota2.ru embeds all its data as `window.matchData = {...}`; reading it is much more
 * stable than reading the markup.
 */
internal object MatchDetailsParser {

    private const val BASE_URL = "https://dota2.ru"
    private const val MARKER = "window.matchData = "

    fun parse(url: String, html: String, nowSeconds: Long = System.currentTimeMillis() / 1000): MatchDetails =
        parseJson(url, JSONObject(extractJson(html)).getJSONObject("match"), nowSeconds)

    /** Cuts the object literal out of the page: braces are matched with strings and escapes taken into account. */
    fun extractJson(html: String): String {
        val start = html.indexOf(MARKER).takeIf { it >= 0 }?.plus(MARKER.length) ?: error("no match data on the page")
        var depth = 0
        var inString = false
        var escaped = false
        for (i in start until html.length) {
            val c = html[i]
            if (inString) {
                when {
                    escaped -> escaped = false
                    c == '\\' -> escaped = true
                    c == '"' -> inString = false
                }
            } else {
                when (c) {
                    '"' -> inString = true
                    '{' -> depth++
                    '}' -> {
                        depth--
                        if (depth == 0) return html.substring(start, i + 1)
                    }
                }
            }
        }
        error("match data is cut off")
    }

    private fun parseJson(url: String, match: JSONObject, nowSeconds: Long): MatchDetails {
        val radiant = match.getJSONObject("radiant")
        val dire = match.getJSONObject("dire")
        val leftId = radiant.optInt("id")
        val tournament = match.optJSONObject("tournament")
        val streams = match.optJSONArray("streams").objects()
        val twitch = streams.firstOrNull { it.str("service") == "twitch" }
        val maps = match.optJSONArray("maps").objects().map { parseMap(it, leftId) }.sortedBy { it.number }
        val start = match.optLong("dateStart")
        val status = when {
            match.optLong("dateEnd") > 0 || match.str("stage") == "finished" -> MatchStatus.FINISHED
            maps.isNotEmpty() || start <= nowSeconds -> MatchStatus.LIVE
            else -> MatchStatus.UPCOMING
        }
        return MatchDetails(
            url = url,
            tournament = tournament?.str("tournament_title").orEmpty(),
            tournamentLogoUrl = tournament?.let { "$BASE_URL/img/esport/tournament_mini/${it.optInt("tournament_id")}.webp?${it.optLong("tournament_logo_mini")}" }.orEmpty(),
            prize = tournament?.str("tournament_prize"),
            stage = match.str("grid"),
            startSeconds = start,
            bestOf = match.optInt("matchBO"),
            status = status,
            left = parseTeam(radiant, match.optJSONObject("players")),
            right = parseTeam(dire, match.optJSONObject("players")),
            scoreLeft = match.optInt("radiantTeamScore"),
            scoreRight = match.optInt("direTeamScore"),
            maps = maps,
            headToHead = match.optJSONObject("lastGames")?.optJSONObject("togetherGames")?.optJSONArray("matches").objects().map(::parsePast),
            forecast = parseForecast(match.optJSONObject("forecast")),
            twitchChannel = twitch?.str("serviceId"),
            streamUrl = (twitch ?: streams.firstOrNull())?.str("link"),
        )
    }

    private fun parseTeam(team: JSONObject, playerStats: JSONObject?): DetailsTeam = DetailsTeam(
        name = team.str("title").orEmpty(),
        logoUrl = teamLogo(team),
        rating = team.intOrNull("rating"),
        position = team.intOrNull("position"),
        players = team.optJSONArray("players").objects().map { player ->
            DetailsPlayer(
                nick = player.str("nickname").orEmpty(),
                flagUrl = player.optJSONObject("flag")?.str("abbr")?.let { "$BASE_URL/img/flags/$it.png" },
                leaderboard = player.intOrNull("leaderboard"),
                isCaptain = player.optInt("status") == 1,
                kda = playerStats?.optJSONObject(player.optInt("id").toString())?.optJSONObject("stats")?.optDouble("kda")?.takeIf { !it.isNaN() },
            )
        },
    )

    private fun parseMap(map: JSONObject, leftId: Int): GameMap {
        val radiantId = map.optJSONObject("radiant")?.optInt("id")
        val leftIsRadiant = radiantId == leftId
        val winner = map.optInt("winner").takeIf { it != 0 }
        val isFinished = map.str("stage") == "finished"
        fun isLeftTeam(teamId: Int) = teamId == leftId
        val scoreRadiant = map.optInt("scoreRadiant")
        val scoreDire = map.optInt("scoreDire")
        return GameMap(
            number = map.optInt("gameNumber"),
            isFinished = isFinished,
            winnerIsLeft = if (isFinished && winner != null) isLeftTeam(winner) else null,
            durationSeconds = map.optInt("duration"),
            killsLeft = if (leftIsRadiant) scoreRadiant else scoreDire,
            killsRight = if (leftIsRadiant) scoreDire else scoreRadiant,
            draft = map.optJSONArray("draft").objects().mapNotNull { pick ->
                val hero = pick.optJSONObject("hero") ?: return@mapNotNull null
                DraftHero(hero.str("name").orEmpty(), heroIcon(hero), pick.optInt("isPick") == 1, isLeftTeam(pick.optInt("teamId")))
            },
            players = map.optJSONArray("players").objects().mapNotNull { player ->
                val hero = player.optJSONObject("hero") ?: return@mapNotNull null
                val isRadiant = player.optInt("isRadiant") == 1
                MapPlayer(
                    nick = player.optJSONObject("player")?.str("player_nick").orEmpty(),
                    heroName = hero.str("name").orEmpty(),
                    heroIconUrl = heroIcon(hero),
                    level = player.optInt("level"),
                    kills = player.optInt("kills"),
                    deaths = player.optInt("deaths"),
                    assists = player.optInt("assists"),
                    gpm = player.optInt("gpm"),
                    xpm = player.optInt("xpm"),
                    netWorth = player.optInt("netWorth"),
                    itemIconUrls = player.optJSONObject("items")?.optJSONArray("items").objects().mapNotNull { item ->
                        item.str("item_image_source")?.let { BASE_URL + it }
                    },
                    isLeft = isRadiant == leftIsRadiant,
                )
            },
        )
    }

    private fun parsePast(game: JSONObject): PastMatch {
        val radiant = game.optJSONObject("radiant")
        val dire = game.optJSONObject("dire")
        return PastMatch(
            startSeconds = game.optLong("dateStart"),
            tournament = game.optJSONObject("tournament")?.str("tournament_title").orEmpty(),
            leftName = radiant?.str("title").orEmpty(),
            leftLogoUrl = radiant?.let(::teamLogo).orEmpty(),
            rightName = dire?.str("title").orEmpty(),
            rightLogoUrl = dire?.let(::teamLogo).orEmpty(),
            scoreLeft = game.optInt("scoreRadiant"),
            scoreRight = game.optInt("scoreDire"),
        )
    }

    private fun parseForecast(forecast: JSONObject?): Forecast? {
        if (forecast == null || !forecast.optBoolean("showForecast")) return null
        val left = forecast.optJSONObject("teamLeft")?.optInt("proc") ?: return null
        val right = forecast.optJSONObject("teamRight")?.optInt("proc") ?: return null
        val draw = forecast.optJSONObject("draw")?.optInt("proc") ?: 0
        return Forecast(left, draw, right).takeIf { left + right + draw > 0 }
    }

    private fun teamLogo(team: JSONObject) = "$BASE_URL/img/esport/team/${team.optInt("id")}.webp?${team.optLong("logo")}"

    /** Same folder naming as the app's own hero assets: `heroes/<name lowercase, spaces as _>/full.webp`. */
    private fun heroIcon(hero: JSONObject) = "file:///android_asset/heroes/${hero.str("name").orEmpty().lowercase().replace(" ", "_")}/full.webp"

    private fun JSONObject.str(key: String): String? = if (isNull(key)) null else optString(key).takeIf { it.isNotBlank() }

    private fun JSONObject.intOrNull(key: String): Int? = if (isNull(key)) null else optInt(key)

    private fun JSONArray?.objects(): List<JSONObject> = if (this == null) emptyList() else (0 until length()).mapNotNull { optJSONObject(it) }
}
