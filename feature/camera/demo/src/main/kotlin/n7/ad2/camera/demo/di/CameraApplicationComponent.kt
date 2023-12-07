package n7.ad2.camera.demo.di

import android.app.Application
import n7.ad2.coroutines.CoroutineModule
import n7.ad2.dagger.ApplicationScope
import n7.ad2.camera.api.CameraDependencies

@ApplicationScope
@dagger.Component(
    modules = [
        CoroutineModule::class,
        CameraApplicationModule::class,
    ]
)
internal interface CameraApplicationComponent : CameraDependencies {

    @dagger.Component.Factory
    interface Factory {
        fun create(@dagger.BindsInstance applicationContext: Application): CameraApplicationComponent
    }
}
