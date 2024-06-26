package n7.ad2.feature.camera.domain.impl

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry

class CameraLifecycle : LifecycleOwner {

    private val lifecycleRegister = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegister

    init {
        lifecycleRegister.currentState = Lifecycle.State.INITIALIZED
    }

    public fun onUiShown() {
        lifecycleRegister.currentState = Lifecycle.State.RESUMED
    }

    public fun onUiHidden() {
        lifecycleRegister.currentState = Lifecycle.State.CREATED
    }
}
