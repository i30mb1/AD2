package n7.ad2.hero_page.api

import androidx.fragment.app.Fragment
import n7.ad2.hero_page.internal.pager.HeroPageFragment
import n7.ad2.provider.api.HeroPageApi

class HeroPageProvider : HeroPageApi {
    override fun getPagerFragment(heroName: String): Fragment = HeroPageFragment.getInstance(heroName)
}