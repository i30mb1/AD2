package n7.ad2.heroes.demo

import android.os.Bundle
import android.view.View
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import n7.ad2.heroes.ui.api.HeroesProvider

internal class HeroesActivityDemo : FragmentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commit {
            // нужно запустить worker который заполняет базу данных героями!
            val fragment = HeroesProvider().getFragment()
            add(container.id, fragment)
        }
    }
}
