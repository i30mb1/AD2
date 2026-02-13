package n7.ad2.items.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import n7.ad2.items.demo.di.ApplicationComponentDemo
import n7.ad2.items.demo.di.DaggerApplicationComponentDemo
import n7.ad2.ktx.lazyUnsafe

internal class AppComponentFactoryDemo : AppComponentFactory() {

    private val factory by lazyUnsafe { DaggerApplicationComponentDemo.factory() }
    private lateinit var component: ApplicationComponentDemo

    override fun instantiateApplication(classLoader: ClassLoader, className: String): Application = when (className) {
        MyApplicationDemo::class.java.name -> MyApplicationDemo { application -> component = factory.create(application) }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(classLoader: ClassLoader, className: String, intent: Intent?): Activity = when (className) {
        ItemsActivityDemo::class.java.name -> ItemsActivityDemo(component.itemsFragmentFactory)
        else -> super.instantiateActivity(classLoader, className, intent)
    }
}
