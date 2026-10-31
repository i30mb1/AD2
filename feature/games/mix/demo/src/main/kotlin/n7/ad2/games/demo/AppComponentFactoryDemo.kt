package n7.ad2.games.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import n7.ad2.games.api.GamesDependencies
import n7.ad2.games.api.GamesFragmentFactory
import n7.ad2.games.demo.di.ApplicationComponentDemo
import n7.ad2.games.demo.di.DaggerApplicationComponentDemo
import n7.ad2.ktx.lazyUnsafe
import n7.ad2.xo.api.XoDependencies

internal class AppComponentFactoryDemo : AppComponentFactory() {

    private val factory by lazyUnsafe { DaggerApplicationComponentDemo.factory() }
    private lateinit var component: ApplicationComponentDemo

    override fun instantiateApplication(classLoader: ClassLoader, className: String): Application = when (className) {
        MyApplicationDemo::class.java.name -> MyApplicationDemo { application ->
            component = factory.create(application)
            mapOf(GamesDependencies::class.java to component, XoDependencies::class.java to component)
        }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(cl: ClassLoader, className: String, intent: Intent?): Activity = when (className) {
        GamesActivityDemo::class.java.name -> GamesActivityDemo(
            GamesFragmentFactory(component),
        )

        else -> super.instantiateActivity(cl, className, intent)
    }
}
