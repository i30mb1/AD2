package n7.ad2.items.demo.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.di.RetrofitModule
import n7.ad2.items.api.ItemsDependencies
import n7.ad2.items.api.ItemsFragmentFactory
import n7.ad2.items.demo.MyApplicationDemo
import n7.ad2.items.domain.wiring.ItemsModule

@ApplicationScope
@dagger.Component(
    modules = [
        ApplicationDemoModule::class,
        CoroutineModule::class,
        RetrofitModule::class,
        DatabaseModule::class,
        ItemsModule::class,
    ],
)
internal interface ApplicationComponentDemo : ItemsDependencies {

    val itemsFragmentFactory: ItemsFragmentFactory

    fun inject(myApplicationDemo: MyApplicationDemo)

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): ApplicationComponentDemo
    }
}
