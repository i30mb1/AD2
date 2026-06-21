package n7.ad2.news.demo.internal.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.database_guides.api.DatabaseModule
import n7.ad2.domain.wiring.NewsModule
import n7.ad2.news.ui.api.NewsDependencies

@ApplicationScope
@dagger.Component(
    modules = [
        ApplicationDemoModule::class,
        CoroutineModule::class,
        DatabaseModule::class,
        NewsModule::class,
    ],
)
internal interface ApplicationComponentDemo : NewsDependencies {

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance application: Application): ApplicationComponentDemo
    }
}
