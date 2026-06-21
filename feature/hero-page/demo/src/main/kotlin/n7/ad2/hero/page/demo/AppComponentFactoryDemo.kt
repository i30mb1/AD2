package n7.ad2.hero.page.demo

import android.app.Activity
import android.app.AppComponentFactory
import android.app.Application
import android.content.Intent
import n7.ad2.hero.page.api.HeroPageDependencies
import n7.ad2.hero.page.demo.di.DaggerApplicationComponentDemo

internal class AppComponentFactoryDemo : AppComponentFactory() {

    override fun instantiateApplication(classLoader: ClassLoader, className: String): Application = when (className) {
        MyApplicationDemo::class.java.name -> MyApplicationDemo { application ->
            mapOf(HeroPageDependencies::class.java to DaggerApplicationComponentDemo.factory().create(application))
        }
        else -> super.instantiateApplication(classLoader, className)
    }

    override fun instantiateActivity(classLoader: ClassLoader, className: String, intent: Intent?): Activity = when (className) {
        HeroPageActivityDemo::class.java.name -> HeroPageActivityDemo()
        else -> super.instantiateActivity(classLoader, className, intent)
    }
}
