package n7.ad2.tournaments.internal.data

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

internal enum class MatchStatus { LIVE, UPCOMING, FINISHED }

internal data class MatchTeam(val name: String, val logoUrl: String)

internal data class TournamentMatch(
    val url: String,
    val tournament: String,
    val tournamentLogoUrl: String,
    val team1: MatchTeam,
    val team2: MatchTeam,
    val status: MatchStatus,
    /** "2:0" for finished matches, null otherwise. */
    val score: String?,
    /** Start time of an upcoming match ("13:50 30.10"), "2 час. назад" for a finished one, game number and duration for a live one. */
    val time: String?,
)

/** Parses https://dota2.ru/esport/matches/ (live, upcoming and finished matches in one list). */
internal object MatchesParser {

    const val BASE_URL = "https://dota2.ru"

    fun parse(document: Document): List<TournamentMatch> = document.select("div.list-match-item").mapNotNull(::parseMatch)

    private fun parseMatch(item: Element): TournamentMatch? {
        val link = item.selectFirst("a.cybersport-matches__matches-match-wrap") ?: return null
        val left = link.selectFirst(".cybersport-matches__matches-block-left") ?: return null
        val right = link.selectFirst(".cybersport-matches__matches-block-right") ?: return null
        val tournamentLogo = item.selectFirst("a.cybersport-matches__matches-left img")
        val timeBlock = link.selectFirst(".cybersport-matches__matches-time")

        val status: MatchStatus
        var score: String? = null
        var time: String? = null
        when {
            timeBlock?.selectFirst(".live") != null -> {
                status = MatchStatus.LIVE
                time = timeBlock.selectFirst(".live")?.select("span")?.joinToString(" · ") { it.text().trim() }?.takeIf { it.isNotBlank() }
            }

            timeBlock?.selectFirst(".score") != null -> {
                status = MatchStatus.FINISHED
                score = timeBlock.selectFirst(".score")?.text()?.trim()
                time = item.selectFirst(".time_block_mobile span")?.text()?.trim()?.takeIf { it.isNotBlank() }
            }

            else -> {
                status = MatchStatus.UPCOMING
                time = timeBlock?.selectFirst(".time")?.text()?.trim()?.takeIf { it.isNotBlank() }
            }
        }

        return TournamentMatch(
            url = absolute(link.attr("href")),
            tournament = tournamentLogo?.attr("data-title-tooltipe").orEmpty(),
            tournamentLogoUrl = absolute(tournamentLogo?.attr("src").orEmpty()),
            team1 = parseTeam(left),
            team2 = parseTeam(right),
            status = status,
            score = score,
            time = time,
        )
    }

    private fun parseTeam(block: Element) = MatchTeam(
        name = block.selectFirst(".cybersport-matches__matches-name")?.text().orEmpty().trim(),
        logoUrl = absolute(block.selectFirst("img.cybersport-matches__matches-team")?.attr("src").orEmpty()),
    )

    private fun absolute(path: String): String = when {
        path.isBlank() -> ""
        path.startsWith("http") -> path
        else -> BASE_URL + path
    }
}
