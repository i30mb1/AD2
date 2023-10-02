package n7.ad2.news.ui.internal.screen.news

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import javax.inject.Inject
import n7.ad2.news.ui.api.NewsDependencies

class NewsFragmentFactory @Inject constructor(
    private val deps: NewsDependencies,
) : FragmentFactory() {

    override fun instantiate(
        classLoader: ClassLoader,
        className: String,
    ): Fragment = when (className) {
        NewsFragment::class.java.name -> NewsFragment(mapOf(NewsDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
