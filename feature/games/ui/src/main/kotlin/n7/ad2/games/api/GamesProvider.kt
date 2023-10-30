package n7.ad2.games.api

import androidx.fragment.app.Fragment
import n7.ad2.games.internal.GamesFragment
import n7.ad2.games.internal.games.xo.XOGameFragment
import n7.ad2.navigator.api.GamesApi

class GamesProvider : GamesApi {

    override fun getFragment(): Class<out Fragment> = XOGameFragment::class.java

}
