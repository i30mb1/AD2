package n7.ad2.heroes.demo

import android.os.Bundle
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commit
import n7.ad2.heroes.ui.api.HeroesProvider

class HeroesActivityDemo : FragmentActivity() {

    private val container by lazy { FragmentContainerView(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(container)
        supportFragmentManager.commit {
            val fragment = HeroesProvider().getFragment()
            add(fragment, null)
        }
    }

}
