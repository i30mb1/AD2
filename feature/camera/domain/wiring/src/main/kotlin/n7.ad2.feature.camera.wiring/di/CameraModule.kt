package n7.ad2.feature.camera.wiring.di

import android.app.Application
import androidx.lifecycle.LifecycleOwner
import javax.inject.Singleton
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.impl.CameraLifecycle
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.CameraSettingsImpl
import n7.ad2.feature.camera.domain.impl.PreviewerCameraX
import n7.ad2.feature.camera.domain.impl.ProcessorKotlinDL
import n7.ad2.feature.camera.domain.impl.StreamerCameraX

@dagger.Module
interface CameraModule {

    companion object {
        @dagger.Provides
        @Singleton
        fun provideProcessing(application: Application): Processor {
            return ProcessorKotlinDL(application)
        }

        @dagger.Provides
        @Singleton
        fun provideSettings(): CameraSettings {
            return CameraSettingsImpl()
        }

        @dagger.Provides
        @Singleton
        fun provideLifecycle(): LifecycleOwner {
            return CameraLifecycle()
        }

        @dagger.Provides
        @Singleton
        fun provideCameraProvider(
            application: Application,
            cameraSettings: CameraSettings,
            lifecycleOwner: LifecycleOwner,
        ): CameraProvider {
            return CameraProvider(application, cameraSettings, lifecycleOwner)
        }

        @dagger.Provides
        @Singleton
        fun providePreviewer(
            cameraProvider: CameraProvider,
        ): Previewer {
            return PreviewerCameraX(cameraProvider)
        }

        @dagger.Provides
        @Singleton
        fun provideStreamer(
            cameraProvider: CameraProvider,
            lifecycle: LifecycleOwner,
        ): Streamer {
            return StreamerCameraX(
                cameraProvider,
                lifecycle,
            )
        }
    }
}
