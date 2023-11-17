package n7.ad2.xo.demo

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import n7.ad2.xo.api.XoFragmentFactory
import n7.ad2.xo.api.XoProvider

class XoActivity(
    private val fragmentFactory: XoFragmentFactory,
) : FragmentActivity() {

    private val fragment by lazy { XoProvider().getFragment() }
    private val permission by lazy {
        AppPermission(this) {
//            GetWifiDirectServers(this).invoke().launchIn(lifecycleScope)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        supportFragmentManager.fragmentFactory = fragmentFactory
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        val container = FragmentContainerView(this)
        container.id = View.generateViewId()
        setContentView(container)
        supportFragmentManager.commitNow {
            add(container.id, fragment, null)
        }
        permission.run()
    }
}
