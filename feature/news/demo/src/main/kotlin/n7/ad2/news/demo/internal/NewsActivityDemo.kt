package n7.ad2.news.demo.internal

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import n7.ad2.news.ui.api.NewsProvider
import n7.ad2.news.ui.internal.screen.news.NewsFragmentFactory

internal class NewsActivityDemo(
    private val fragmentFactory: NewsFragmentFactory,
) : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commit {
            val fragment: Class<out Fragment> = NewsProvider().getFragment()
            add(container.id, fragment, null)
        }
    }
}
