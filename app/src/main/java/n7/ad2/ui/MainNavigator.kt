package n7.ad2.ui

import androidx.fragment.app.FragmentManager
import androidx.fragment.app.commit
import n7.ad2.ui.heroPage.HeroPageFragment

class MainNavigator(
    private val fragmentManager: FragmentManager,
    private val containerViewID: Int,
) {

    fun openHeroPageFragment(heroName: String) {
        fragmentManager.commit {
            val heroPageFragment = HeroPageFragment.getInstance(heroName)
            add(containerViewID, heroPageFragment)
            addToBackStack(null)
        }
    }

}