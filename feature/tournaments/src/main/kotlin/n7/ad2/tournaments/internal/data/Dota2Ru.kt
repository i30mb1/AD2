package n7.ad2.tournaments.internal.data

import org.jsoup.Jsoup

internal object Dota2Ru {

    /** Desktop on purpose: the site serves a different markup to mobile browsers and the parsers are written for the desktop one. */
    const val USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/124.0 Safari/537.36"
    const val MATCHES_URL = "https://dota2.ru/esport/matches/"
    private const val TIMEOUT_MS = 15_000

    fun connect(url: String) = Jsoup.connect(url).userAgent(USER_AGENT).timeout(TIMEOUT_MS)

    fun html(url: String): String = connect(url).execute().body()
}
