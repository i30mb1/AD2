package n7.ad2.xo.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import n7.ad2.xo.demo.di.DaggerXoApplicationComponent
import n7.ad2.xo.api.XoFragmentFactory
import n7.ad2.xo.demo.di.XoApplicationComponent
import n7.ad2.ktx.lazyUnsafe


internal class XoAppComponentFactory : AppComponentFactory() {

    private val factory by lazyUnsafe { DaggerXoApplicationComponent.factory() }
    private lateinit var component: XoApplicationComponent

    override fun instantiateApplication(
        classLoader: ClassLoader,
        className: String,
    ): Application = when (className) {
        XoApplication::class.java.name -> XoApplication { application -> component = factory.create(application) }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(
        cl: ClassLoader,
        className: String,
        intent: Intent?,
    ): Activity = when (className) {
        XoActivity::class.java.name -> XoActivity(
            XoFragmentFactory(component)
        )

        else -> super.instantiateActivity(cl, className, intent)
    }
}
