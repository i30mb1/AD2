package n7.ad2.feature.camera.domain.impl.processing.processor

import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.impl.processing.FaceFromPhotoExtractor
import n7.ad2.feature.camera.domain.impl.processing.blurriness.Blurriness
import n7.ad2.feature.camera.domain.impl.processing.headpose.HeadPose
import n7.ad2.feature.camera.domain.impl.processing.illumination.IlluminationDetect

fun Processor(
    faceDetect: FaceDetect,
    illumination: IlluminationDetect,
    headPose: HeadPose,
    blurriness: Blurriness,
    faceExtractor: FaceFromPhotoExtractor,
    settings: CameraSettings,
    fpsTimer: FPSTimer,
): Processor = ProcessorWithLogging(
    ProcessorSynchronized(
        ProcessorImpl(
            faceDetect,
            illumination,
            headPose,
            blurriness,
            faceExtractor,
            settings,
        ),
    ),
    fpsTimer,
)
