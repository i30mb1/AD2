package n7.ad2.games.demo.di

import android.app.Application
import n7.ad.games.domain.wiring.GamesModule
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.di.RetrofitModule
import n7.ad2.games.api.GamesDependencies
import n7.ad2.games.demo.MyApplicationDemo
import n7.ad2.heroes.domain.wiring.HeroesModule

@ApplicationScope
@dagger.Component(
    modules = [
        ApplicationDemoModule::class,
        CoroutineModule::class,
        RetrofitModule::class,
        DatabaseModule::class,
        GamesModule::class,
        HeroesModule::class,
    ]
)
internal interface ApplicationComponentDemo : GamesDependencies {

    fun inject(myApplicationDemo: MyApplicationDemo)

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): ApplicationComponentDemo
    }
}
