package n7.ad2.hero.page.demo.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.hero.page.api.HeroPageDependencies
import n7.ad2.heroes.domain.wiring.HeroesModule
import n7.ad2.spanparser.di.SpanParserModule

@ApplicationScope
@dagger.Component(
    modules = [
        ApplicationDemoModule::class,
        CoroutineModule::class,
        HeroesModule::class,
        SpanParserModule::class,
    ],
)
internal interface ApplicationComponentDemo : HeroPageDependencies {

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance application: Application): ApplicationComponentDemo
    }
}
