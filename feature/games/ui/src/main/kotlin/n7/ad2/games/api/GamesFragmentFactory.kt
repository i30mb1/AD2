package n7.ad2.games.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject
import n7.ad2.games.internal.GamesFragment
import n7.ad2.games.internal.games.xo.XOGameFragment

class GamesFragmentFactory @Inject constructor(
    private val deps: GamesDependencies,
) : FragmentFactory() {

    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment = when (className) {
        GamesFragment::class.java.name -> GamesFragment(mapOf(GamesDependencies::class.java to deps))
        XOGameFragment::class.java.name -> XOGameFragment(mapOf(GamesDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
