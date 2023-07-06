package n7.ad2.heroes.demo

import android.app.Application
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.heroes.demo.di.ApplicationComponentDemo
import n7.ad2.ktx.lazyUnsafe
import javax.inject.Inject

class MyApplicationDemo : Application(), HasDependencies {

    @Inject override lateinit var dependenciesMap: DependenciesMap

    private val component: ApplicationComponentDemo by lazyUnsafe { DaggerApplicationComponentDemo.factory().create(this) }

    override fun onCreate() {
        super.onCreate()
    }
}
