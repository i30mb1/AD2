package n7.ad2.heroes.api

import androidx.fragment.app.Fragment
import n7.ad2.heroes.internal.HeroesFragment
import n7.ad2.provider.api.HeroesApi

class HeroesProvider : HeroesApi {

    override fun getFragment(): Fragment = HeroesFragment.getInstance()

}