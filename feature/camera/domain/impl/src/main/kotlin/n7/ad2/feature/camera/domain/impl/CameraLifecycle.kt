package n7.ad2.feature.camera.domain.impl

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class CameraLifecycle : LifecycleOwner {

    private val lifecycleRegister = LifecycleRegistry(this)
    override fun getLifecycle(): Lifecycle = lifecycleRegister

    init {
        lifecycleRegister.currentState = Lifecycle.State.RESUMED
    }
}
