package n7.ad2.camera.api

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentFactory
import n7.ad2.camera.internal.CameraFragment

class CameraFragmentFactory(private val deps: CameraDependencies) : FragmentFactory() {

    override fun instantiate(classLoader: ClassLoader, className: String): Fragment = when (className) {
        CameraFragment::class.java.name -> CameraFragment(mapOf(CameraDependencies::class.java to deps))
        else -> super.instantiate(classLoader, className)
    }
}
