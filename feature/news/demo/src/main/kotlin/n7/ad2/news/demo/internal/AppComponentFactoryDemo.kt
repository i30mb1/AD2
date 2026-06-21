package n7.ad2.news.demo.internal

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import n7.ad2.news.demo.internal.di.ApplicationComponentDemo
import n7.ad2.news.demo.internal.di.DaggerApplicationComponentDemo
import n7.ad2.news.ui.internal.screen.news.NewsFragmentFactory

internal class AppComponentFactoryDemo : AppComponentFactory() {

    private val factory = DaggerApplicationComponentDemo.factory()
    private lateinit var component: ApplicationComponentDemo

    override fun instantiateApplication(classLoader: ClassLoader, className: String): Application = when (className) {
        MyApplicationDemo::class.java.name -> MyApplicationDemo { application -> component = factory.create(application) }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(cl: ClassLoader, className: String, intent: Intent?): Activity = when (className) {
        NewsActivityDemo::class.java.name -> NewsActivityDemo(NewsFragmentFactory(component))
        else -> super.instantiateActivity(cl, className, intent)
    }
}
