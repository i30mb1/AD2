package n7.ad2.heroes.ui.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import n7.ad2.heroes.ui.internal.HeroesFragment
import javax.inject.Inject

class HeroesFragmentFactory @Inject constructor(private val deps: HeroesDependencies) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when (className) {
        HeroesFragment::class.java.name -> HeroesFragment(mapOf(HeroesDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
