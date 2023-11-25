package n7.ad2.games.demo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import n7.ad2.games.api.GamesFragmentFactory
import n7.ad2.games.api.GamesProvider

class GamesActivityDemo(
    private val fragmentFactory: GamesFragmentFactory,
) : FragmentActivity() {

    private val fragment by lazy { GamesProvider().getFragment() }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
       enableEdgeToEdge()
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commitNow {
            add(container.id, fragment, null)
        }
    }
}
