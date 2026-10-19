package n7.ad2.heroes.demo

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commit
import androidx.fragment.app.commitNow
import n7.ad2.android.MainFragmentNavigator
import n7.ad2.feature.heroes.ui.api.HeroesFragmentFactory
import n7.ad2.feature.heroes.ui.api.HeroesProvider

internal class HeroesActivityDemo(private val fragmentFactory: HeroesFragmentFactory) : FragmentActivity(), MainFragmentNavigator {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        // fragments (and the back stack) are restored into android.R.id.content by the FragmentManager itself
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                val fragment: Class<out Fragment> = HeroesProvider().getFragment()
                add(android.R.id.content, fragment, null)
            }
        }
    }

    override fun setMainFragment(fragment: Fragment, body: FragmentTransaction.() -> Unit) {
        supportFragmentManager.commit {
            replace(android.R.id.content, fragment)
            body()
        }
    }
}
