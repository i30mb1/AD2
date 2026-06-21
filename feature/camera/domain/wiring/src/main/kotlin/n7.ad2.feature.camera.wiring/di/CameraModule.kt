package n7.ad2.feature.camera.wiring.di

import android.app.Application
import androidx.lifecycle.lifecycleScope
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.FaceDetector
import n7.ad2.feature.camera.domain.FaceDetectorSwitcher
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
import n7.ad2.feature.camera.domain.impl.processing.FaceFromPhotoExtractor
import n7.ad2.feature.camera.domain.impl.processing.ImageEditor
import n7.ad2.feature.camera.domain.impl.processing.MLModelName
import n7.ad2.feature.camera.domain.impl.processing.ModelManager
import n7.ad2.feature.camera.domain.impl.processing.ModelManagerAndroid
import n7.ad2.feature.camera.domain.impl.processing.blurriness.Blurriness
import n7.ad2.feature.camera.domain.impl.processing.detect.FaceDetectSwitchable
import n7.ad2.feature.camera.domain.impl.processing.detect.TFaceLite
import n7.ad2.feature.camera.domain.impl.processing.detect.switchable
import n7.ad2.feature.camera.domain.impl.processing.headpose.HeadPose
import n7.ad2.feature.camera.domain.impl.processing.illumination.IlluminationDetect
import n7.ad2.feature.camera.domain.impl.processing.processor.Processor as createProcessor
import n7.ad2.feature.camera.domain.impl.processor.Camera2FaceSource
import n7.ad2.feature.camera.domain.impl.processor.ProcessorBlazeFace
import n7.ad2.feature.camera.domain.impl.processor.ProcessorCamera2
import n7.ad2.feature.camera.domain.impl.processor.ProcessorKotlinDL
import n7.ad2.feature.camera.domain.impl.processor.ProcessorTF
import n7.ad2.feature.camera.domain.impl.recorder.RecorderCameraX
import n7.ad2.feature.camera.domain.impl.recorder.RecorderSingleCompletion
import n7.ad2.feature.camera.domain.impl.recorder.RecorderWithLogging
import n7.ad2.feature.camera.domain.impl.streamer.StreamerCameraX
import n7.ad2.feature.camera.domain.impl.streamer.StreamerWithLogging
import javax.inject.Singleton

@dagger.Module
interface CameraModule {

    companion object {
        @dagger.Provides
        @Singleton
        fun provideModelManager(application: Application): ModelManager = ModelManagerAndroid(application)

        @dagger.Provides
        @Singleton
        fun provideImageEditor(): ImageEditor = ImageEditor()

        @dagger.Provides
        @Singleton
        fun provideIllumination(cameraSettings: CameraSettings, editor: ImageEditor): IlluminationDetect =
            IlluminationDetect(cameraSettings, editor)

        @dagger.Provides
        @Singleton
        fun provideHeadPose(modelManager: ModelManager, editor: ImageEditor): HeadPose =
            HeadPose(modelManager.getMLModel(MLModelName.HEADPOSE_OCCLUSION), editor)

        @dagger.Provides
        @Singleton
        fun provideBlurriness(modelManager: ModelManager, editor: ImageEditor): Blurriness =
            Blurriness(modelManager.getMLModel(MLModelName.BLAZE_FACE), editor)

        @dagger.Provides
        @Singleton
        fun provideFaceExtractor(editor: ImageEditor): FaceFromPhotoExtractor = FaceFromPhotoExtractor(editor)

        @dagger.Provides
        @Singleton
        fun provideCamera2FaceSource(application: Application, cameraSettings: CameraSettings): Camera2FaceSource =
            Camera2FaceSource(application, cameraSettings)

        @dagger.Provides
        @Singleton
        fun provideFaceDetectSwitchable(
            application: Application,
            modelManager: ModelManager,
            editor: ImageEditor,
            faceSource: Camera2FaceSource,
            logger: Logger,
        ): FaceDetectSwitchable = linkedMapOf<FaceDetector, () -> FaceDetect>(
            FaceDetector.BLAZE_FACE to { ProcessorBlazeFace(application) },
            FaceDetector.ULTRA_FACE to { ProcessorKotlinDL(application) },
            FaceDetector.TF to { ProcessorTF(application) },
            FaceDetector.CAMERA2 to { ProcessorCamera2(faceSource) },
            FaceDetector.TFACE_LITE to { TFaceLite(modelManager.getMLModel(MLModelName.TFACE_LITE), editor) },
        ).switchable(logger)

        @dagger.Provides
        @Singleton
        fun provideFaceDetect(switchable: FaceDetectSwitchable): FaceDetect = switchable

        @dagger.Provides
        @Singleton
        fun provideFaceDetectorSwitcher(switchable: FaceDetectSwitchable): FaceDetectorSwitcher = switchable

        @dagger.Provides
        @Singleton
        fun provideProcessing(
            faceDetect: FaceDetect,
            illumination: IlluminationDetect,
            headPose: HeadPose,
            blurriness: Blurriness,
            faceExtractor: FaceFromPhotoExtractor,
            cameraSettings: CameraSettings,
            fpsTimer: FPSTimer,
        ): Processor = createProcessor(faceDetect, illumination, headPose, blurriness, faceExtractor, cameraSettings, fpsTimer)

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
        fun provideController(
            previewer: Previewer,
            processor: Processor,
            streamer: Streamer,
            recorder: Recorder,
            cameraProvider: CameraProvider,
            lifecycleOwner: CameraLifecycle,
            dispatcher: DispatchersProvider,
            logger: Logger,
            fpsTimer: FPSTimer,
        ): Controller = Controller(
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
        fun provideCameraProvider(application: Application, cameraSettings: CameraSettings, lifecycleOwner: CameraLifecycle, logger: Logger): CameraProvider =
            CameraProvider(application, cameraSettings, lifecycleOwner, logger)

        @dagger.Provides
        @Singleton
        fun providePreviewer(): Previewer = PreviewerCameraX()

        @dagger.Provides
        @Singleton
        fun provideRecorder(context: Application, logger: Logger, dispatcher: DispatchersProvider, lifecycleOwner: CameraLifecycle): Recorder =
            RecorderWithLogging(
                RecorderSingleCompletion(
                    RecorderCameraX(context, logger, dispatcher, lifecycleOwner.lifecycleScope),
                ),
                logger,
            )

        @dagger.Provides
        @Singleton
        fun provideStreamer(
            cameraSettings: CameraSettings,
            lifecycle: CameraLifecycle,
            fpsTimer: FPSTimer,
            dispatcher: DispatchersProvider,
            faceSource: Camera2FaceSource,
            logger: Logger,
        ): Streamer = StreamerWithLogging(
            StreamerCameraX(cameraSettings, dispatcher, lifecycle, fpsTimer, faceSource),
            logger,
        )
    }
}
