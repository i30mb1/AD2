package n7.ad2.tournaments.api

import androidx.fragment.app.Fragment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import n7.ad2.navigator.api.TournamentsApi
import n7.ad2.tournaments.internal.TournamentsFragment
import n7.ad2.tournaments.internal.data.Dota2Ru
import n7.ad2.tournaments.internal.data.MatchStatus
import n7.ad2.tournaments.internal.data.MatchesParser

class TournamentsProvider : TournamentsApi {

    override fun getFragment(): Class<out Fragment> = TournamentsFragment::class.java

    override suspend fun getLiveMatchesCount(): Int = withContext(Dispatchers.IO) {
        MatchesParser.parse(Dota2Ru.connect(Dota2Ru.MATCHES_URL).get()).count { match -> match.status == MatchStatus.LIVE }
    }
}
