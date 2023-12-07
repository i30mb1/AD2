package n7.ad2.camera.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import n7.ad2.camera.demo.di.DaggerCameraApplicationComponent
import n7.ad2.camera.api.CameraFragmentFactory
import n7.ad2.camera.demo.di.CameraApplicationComponent
import n7.ad2.ktx.lazyUnsafe


internal class CameraAppComponentFactory : AppComponentFactory() {

    private val factory by lazyUnsafe { DaggerCameraApplicationComponent.factory() }
    private lateinit var component: CameraApplicationComponent

    override fun instantiateApplication(
        classLoader: ClassLoader,
        className: String,
    ): Application = when (className) {
        CameraApplication::class.java.name -> CameraApplication { application -> component = factory.create(application) }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(
        cl: ClassLoader,
        className: String,
        intent: Intent?,
    ): Activity = when (className) {
        CameraActivity::class.java.name -> CameraActivity(
            CameraFragmentFactory(component)
        )

        else -> super.instantiateActivity(cl, className, intent)
    }
}
