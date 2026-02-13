package n7.ad2.feature.camera.wiring.di

import android.app.Application
import androidx.lifecycle.lifecycleScope
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
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.impl.preview.PreviewerCameraX
import n7.ad2.feature.camera.domain.impl.processor.ProcessorBlazeFace
import n7.ad2.feature.camera.domain.impl.processor.ProcessorMetrics
import n7.ad2.feature.camera.domain.impl.recorder.RecorderCameraX
import n7.ad2.feature.camera.domain.impl.streamer.StreamerCameraX
import javax.inject.Singleton

@dagger.Module
interface CameraModule {

    companion object {
        @dagger.Provides
        @Singleton
        fun provideProcessing(application: Application, fpsTimer: FPSTimer): Processor = ProcessorMetrics(
            ProcessorBlazeFace(application),
            fpsTimer,
        )

        @dagger.Provides
        @Singleton
        fun provideSettings(): CameraSettings = CameraSettingsImpl()

        @dagger.Provides
        @Singleton
        fun provideLifecycle(): CameraLifecycle = CameraLifecycle()

        @dagger.Provides
        @Singleton
        fun provideFPSTimer(logger: Logger): FPSTimer = FPSTimer(logger)

        @dagger.Provides
        @dagger.Reusable
        fun provideController(previewer: Previewer, processor: Processor, streamer: Streamer, recorder: Recorder, cameraProvider: CameraProvider, lifecycleOwner: CameraLifecycle, dispatcher: DispatchersProvider, logger: Logger, fpsTimer: FPSTimer): Controller = Controller(
            previewer,
            processor,
            recorder,
            streamer,
            lifecycleOwner,
            cameraProvider,
            dispatcher,
            fpsTimer,
        )

        @dagger.Provides
        @Singleton
        fun provideCameraProvider(application: Application, cameraSettings: CameraSettings, lifecycleOwner: CameraLifecycle, logger: Logger): CameraProvider = CameraProvider(application, cameraSettings, lifecycleOwner, logger)

        @dagger.Provides
        @Singleton
        fun providePreviewer(): Previewer = PreviewerCameraX()

        @dagger.Provides
        @Singleton
        fun provideRecorder(context: Application, logger: Logger, dispatcher: DispatchersProvider, lifecycleOwner: CameraLifecycle): Recorder = RecorderCameraX(context, logger, dispatcher, lifecycleOwner.lifecycleScope)

        @dagger.Provides
        @Singleton
        fun provideStreamer(cameraSettings: CameraSettings, lifecycle: CameraLifecycle, fpsTimer: FPSTimer, dispatcher: DispatchersProvider): Streamer = StreamerCameraX(
            cameraSettings,
            dispatcher,
            lifecycle,
            fpsTimer,
        )
    }
}
