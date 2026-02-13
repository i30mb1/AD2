package n7.ad2.camera.demo

import android.os.Bundle
import android.view.View
import androidx.core.view.WindowCompat
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.commitNow
import n7.ad2.camera.api.CameraFragmentFactory
import n7.ad2.camera.api.CameraProvider

class CameraActivity(private val fragmentFactory: CameraFragmentFactory) : FragmentActivity() {

    private val fragment by lazy { CameraProvider().getFragment() }

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
    }
}
