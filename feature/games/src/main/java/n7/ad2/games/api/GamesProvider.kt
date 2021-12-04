package n7.ad2.games.api

import androidx.fragment.app.Fragment
import n7.ad2.games.internal.GamesFragment
import n7.ad2.provider.api.GamesApi

class GamesProvider : GamesApi {

    override fun getFragment(): Fragment = GamesFragment.getInstance()

}