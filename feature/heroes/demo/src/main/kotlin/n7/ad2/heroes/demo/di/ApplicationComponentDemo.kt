package n7.ad2.heroes.demo.di

import android.app.Application
import n7.ad2.apppreference.AppPreferenceModule
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.di.RetrofitModule
import n7.ad2.spanparser.di.SpanParserModule
import n7.ad2.updatemanager.UpdateManagerModule

@ApplicationScope
@dagger.Component(
    modules = [
//        ComponentDependenciesModule::class,
        CoroutineModule::class,
        UpdateManagerModule::class,
//        MoshiModule::class,
        RetrofitModule::class,
//        ApplicationModule::class,
        AppPreferenceModule::class,
        DatabaseModule::class,
        SpanParserModule::class,
//        HeroesModule::class,
    ]
)
interface ApplicationComponentDemo {

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): ApplicationComponentDemo
    }
}
