package n7.ad2.camera.internal

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LifecycleRegistry
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class CameraLifecycle @Inject constructor(): LifecycleOwner {

    private val lifecycleRegister = LifecycleRegistry(this)
    override val lifecycle: Lifecycle = lifecycleRegister

    init {
        lifecycleRegister.currentState = Lifecycle.State.RESUMED
    }
}
