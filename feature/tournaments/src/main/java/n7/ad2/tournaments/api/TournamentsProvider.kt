package n7.ad2.tournaments.api

import androidx.fragment.app.Fragment
import n7.ad2.provider.api.TournamentsApi
import n7.ad2.tournaments.internal.TournamentsFragment

class TournamentsProvider : TournamentsApi {

    override fun getFragment(): Fragment = TournamentsFragment.getInstance()

}