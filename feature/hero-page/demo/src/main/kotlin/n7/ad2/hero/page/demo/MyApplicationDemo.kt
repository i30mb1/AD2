package n7.ad2.hero.page.demo

import android.app.Application
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies

internal class MyApplicationDemo(
    private val provideDependencies: (application: Application) -> DependenciesMap,
) : Application(), HasDependencies {

    override lateinit var dependenciesMap: DependenciesMap

    override fun onCreate() {
        dependenciesMap = provideDependencies(this)
        super.onCreate()
    }
}
