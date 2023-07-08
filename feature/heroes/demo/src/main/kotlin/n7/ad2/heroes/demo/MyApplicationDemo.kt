package n7.ad2.heroes.demo

import android.app.Application
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies
import n7.ad2.heroes.demo.di.ApplicationComponentDemo
import n7.ad2.heroes.demo.di.DaggerApplicationComponentDemo
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.ktx.lazyUnsafe
import javax.inject.Inject

class MyApplicationDemo : Application(), HasDependencies {

    @Inject lateinit var heroesDependencies: HeroesDependencies

    override lateinit var dependenciesMap: DependenciesMap

    private val component: ApplicationComponentDemo by lazyUnsafe { DaggerApplicationComponentDemo.factory().create(this) }

    override fun onCreate() {
        component.inject(this)
        dependenciesMap = mapOf(HeroesDependencies::class.java to heroesDependencies)
        super.onCreate()
    }
}
