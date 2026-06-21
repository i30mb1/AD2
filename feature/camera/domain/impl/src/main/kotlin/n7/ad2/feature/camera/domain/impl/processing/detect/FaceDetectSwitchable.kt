package n7.ad2.feature.camera.domain.impl.processing.detect

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.FaceDetector
import n7.ad2.feature.camera.domain.FaceDetectorSwitcher
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image

class FaceDetectSwitchable(
    private val factories: Map<FaceDetector, () -> FaceDetect>,
    private val logger: Logger,
) : FaceDetect, FaceDetectorSwitcher {

    private val cache = HashMap<FaceDetector, FaceDetect>()
    private val _current = MutableStateFlow(factories.keys.first())

    override val detectors: List<FaceDetector> = factories.keys.toList()
    override val current: StateFlow<FaceDetector> = _current.asStateFlow()

    override fun select(detector: FaceDetector) {
        if (factories.containsKey(detector)) _current.value = detector
    }

    override fun detect(image: Image): List<DetectedFaceNormalized> {
        val detector = _current.value
        return try {
            faceDetect(detector).detect(image)
        } catch (error: Throwable) {
            logger.log("FaceDetectSwitchable $detector failed: ${error.message}")
            emptyList()
        }
    }

    private fun faceDetect(detector: FaceDetector): FaceDetect = cache.getOrPut(detector) {
        factories.getValue(detector).invoke()
    }
}

fun Map<FaceDetector, () -> FaceDetect>.switchable(logger: Logger): FaceDetectSwitchable = FaceDetectSwitchable(this, logger)
