package n7.ad2.games.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import android.util.Log
import n7.ad2.games.api.GamesFragmentFactory
import n7.ad2.games.demo.di.ApplicationComponentDemo
import n7.ad2.ktx.lazyUnsafe


internal class AppComponentFactoryDemo : AppComponentFactory() {

    private val factory by lazyUnsafe { DaggerApplicationComponentDemo.factory() }
    private lateinit var component: ApplicationComponentDemo

    override fun instantiateApplication(
        classLoader: ClassLoader,
        className: String,
    ): Application = when (className) {
        MyApplicationDemo::class.java.name -> MyApplicationDemo { application -> component = factory.create(application) }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(
        cl: ClassLoader,
        className: String,
        intent: Intent?,
    ): Activity = when (className) {
        GamesActivityDemo::class.java.name -> GamesActivityDemo(
            GamesFragmentFactory(component),
            { message -> Log.d("N7", message) }
        )

        else -> super.instantiateActivity(cl, className, intent)
    }
}
