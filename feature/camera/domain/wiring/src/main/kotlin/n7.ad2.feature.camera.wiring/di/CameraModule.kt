package n7.ad2.feature.camera.wiring.di

import android.app.Application
import javax.inject.Singleton
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.Previewer
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Recorder
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.impl.CameraLifecycle
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.CameraSettingsImpl
import n7.ad2.feature.camera.domain.impl.Controller
import n7.ad2.feature.camera.domain.impl.preview.PreviewerCameraX
import n7.ad2.feature.camera.domain.impl.processor.ProcessorKotlinDL
import n7.ad2.feature.camera.domain.impl.recorder.RecorderCameraX
import n7.ad2.feature.camera.domain.impl.streamer.StreamerCameraX

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
        fun provideLifecycle(): CameraLifecycle {
            return CameraLifecycle()
        }

        @dagger.Provides
        @dagger.Reusable
        fun provideController(
            previewer: Previewer,
            processor: Processor,
            streamer: Streamer,
            recorder: Recorder,
            cameraProvider: CameraProvider,
            lifecycleOwner: CameraLifecycle,
        ): Controller {
            return Controller(
                previewer,
                processor,
                recorder,
                streamer,
                lifecycleOwner,
                cameraProvider,
            )
        }

        @dagger.Provides
        @Singleton
        fun provideCameraProvider(
            application: Application,
            cameraSettings: CameraSettings,
            lifecycleOwner: CameraLifecycle,
        ): CameraProvider {
            return CameraProvider(application, cameraSettings, lifecycleOwner)
        }

        @dagger.Provides
        @Singleton
        fun providePreviewer(
            cameraProvider: CameraProvider,
        ): Previewer {
            return PreviewerCameraX()
        }

        @dagger.Provides
        @Singleton
        fun provideRecorder(
            context: Application,
            logger: Logger,
            dispatcher: DispatchersProvider,
        ): Recorder {
            return RecorderCameraX(context, logger, dispatcher)
        }

        @dagger.Provides
        @Singleton
        fun provideStreamer(
            cameraSettings: CameraSettings,
            lifecycle: CameraLifecycle,
            logger: Logger,
        ): Streamer {
            return StreamerCameraX(
                    cameraSettings,
                    lifecycle,
                    logger,
            )
        }
    }
}
