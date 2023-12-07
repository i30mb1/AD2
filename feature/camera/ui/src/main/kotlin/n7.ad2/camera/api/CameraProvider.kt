package n7.ad2.camera.api

import androidx.fragment.app.Fragment
import n7.ad2.camera.internal.CameraFragment
import n7.ad2.navigator.api.CameraApi

class CameraProvider : CameraApi {
    override fun getFragment(): Class<out Fragment> = CameraFragment::class.java
}
