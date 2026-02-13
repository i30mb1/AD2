package n7.ad2.xo.demo.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.xo.api.XoDependencies

@ApplicationScope
@dagger.Component(
    modules = [
        CoroutineModule::class,
        XoApplicationModule::class,
    ],
)
internal interface XoApplicationComponent : XoDependencies {

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): XoApplicationComponent
    }
}
