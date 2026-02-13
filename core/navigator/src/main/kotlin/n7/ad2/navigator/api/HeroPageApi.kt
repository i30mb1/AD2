package n7.ad2.navigator.api

import androidx.fragment.app.Fragment

interface HeroPageApi {
    fun getPagerFragment(heroName: String): Fragment
}
