package n7.ad2.xo.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import n7.ad2.xo.internal.XoFragment

class XoFragmentFactory(private val deps: XoDependencies) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when (className) {
        XoFragment::class.java.name -> XoFragment(mapOf(XoDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
