package n7.ad2.heroes.demo

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import n7.ad2.heroes.ui.api.HeroesFragmentFactory
import n7.ad2.heroes.ui.api.HeroesProvider

internal class HeroesActivityDemo(
    private val fragmentFactory: HeroesFragmentFactory,
) : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commitNow {
            val fragment: Class<out Fragment> = HeroesProvider().getFragment()
            add(container.id, fragment, null)
        }
    }
}
