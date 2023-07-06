package n7.ad2.heroes.ui.api

import androidx.fragment.app.Fragment
import n7.ad2.heroes.ui.internal.HeroesFragment
import n7.ad2.provider.api.HeroesApi

class HeroesProvider : HeroesApi {

    override fun getFragment(): Fragment = HeroesFragment.getInstance()

}