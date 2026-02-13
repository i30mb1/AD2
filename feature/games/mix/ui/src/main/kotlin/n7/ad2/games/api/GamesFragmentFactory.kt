package n7.ad2.games.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import n7.ad2.games.internal.GamesFragment

class GamesFragmentFactory(private val deps: GamesDependencies) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when (className) {
        GamesFragment::class.java.name -> GamesFragment(mapOf(GamesDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
