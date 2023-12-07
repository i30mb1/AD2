package n7.ad2.camera.internal.di

import n7.ad2.camera.api.CameraDependencies
import n7.ad2.camera.internal.CameraFragment
import n7.ad2.camera.internal.CameraViewModel

@dagger.Component(
    dependencies = [
        CameraDependencies::class
    ],
)
internal interface CameraComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: CameraDependencies): CameraComponent
    }

    fun inject(cameraFragment: CameraFragment)

    val cameraViewModelFactory: CameraViewModel.Factory
}
