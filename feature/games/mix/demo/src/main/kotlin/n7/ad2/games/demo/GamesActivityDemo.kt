package n7.ad2.games.demo

import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.FragmentTransaction
import androidx.fragment.app.commitNow
import n7.ad2.android.MainFragmentNavigator
import n7.ad2.games.api.GamesFragmentFactory
import n7.ad2.games.api.GamesProvider

/** Как MainActivity в приложении: экраны игр открываются через [MainFragmentNavigator] с back stack */
class GamesActivityDemo(private val fragmentFactory: GamesFragmentFactory) :
    FragmentActivity(),
    MainFragmentNavigator {

    private val fragment by lazy { GamesProvider().getFragment() }
    private var containerId = View.NO_ID

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = FragmentContainerView(this)
        container.id = R.id.games_demo_container
        containerId = container.id
        setContentView(container)
        if (savedInstanceState == null) {
            supportFragmentManager.commitNow {
                add(container.id, fragment, null)
            }
        }
    }

    override fun setMainFragment(fragment: Fragment, body: FragmentTransaction.() -> Unit) {
        supportFragmentManager.beginTransaction().apply {
            body()
            replace(containerId, fragment)
        }.commit()
    }
}
