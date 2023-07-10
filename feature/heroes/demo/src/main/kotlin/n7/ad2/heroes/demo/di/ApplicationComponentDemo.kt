package n7.ad2.heroes.demo.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.di.RetrofitModule
import n7.ad2.heroes.demo.MyApplicationDemo
import n7.ad2.heroes.ui.api.HeroesDependencies

@ApplicationScope
@dagger.Component(
    modules = [
        ApplicationDemoModule::class,
        CoroutineModule::class,
        RetrofitModule::class,
        DatabaseModule::class,
    ]
)
interface ApplicationComponentDemo : HeroesDependencies {

    fun inject(myApplicationDemo: MyApplicationDemo)

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): ApplicationComponentDemo
    }
}
