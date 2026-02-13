package n7.ad2.items.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import n7.ad2.items.internal.ItemsFragment
import javax.inject.Inject

class ItemsFragmentFactory @Inject constructor(private val deps: ItemsDependencies) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when (className) {
        ItemsFragment::class.java.name -> ItemsFragment(mapOf(ItemsDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
