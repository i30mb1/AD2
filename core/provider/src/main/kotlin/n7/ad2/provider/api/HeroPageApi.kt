package n7.ad2.provider.api

import androidx.fragment.app.Fragment

interface HeroPageApi {
    fun getPagerFragment(heroName: String): Fragment
}