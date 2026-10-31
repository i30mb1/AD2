package n7.ad2.games.demo

import android.app.Application
import n7.ad2.android.DependenciesMap
import n7.ad2.android.HasDependencies

/**
 * [dependenciesMap] нужен экранам, которые открываются из списка игр (игра, лобби, рекорды, XO):
 * они ищут зависимости вверх по иерархии до Application
 */
internal class MyApplicationDemo(private val application: (component: Application) -> DependenciesMap) :
    Application(),
    HasDependencies {

    override var dependenciesMap: DependenciesMap = emptyMap()

    override fun onCreate() {
        dependenciesMap = application(this)
        super.onCreate()
    }
}
