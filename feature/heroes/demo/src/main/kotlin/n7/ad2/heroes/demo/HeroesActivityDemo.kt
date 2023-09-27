package n7.ad2.heroes.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import n7.ad2.heroes.ui.api.HeroesProvider
import n7.ad2.heroes.ui.internal.HeroesFragmentFactory

internal class HeroesActivityDemo(
    private val fragmentFactory: HeroesFragmentFactory,
) : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commit {
            // нужно запустить worker который заполняет базу данных героями!
            val fragment: Class<out Fragment> = HeroesProvider().getFragment()
            add(container.id, fragment, null)
        }
    }
}
