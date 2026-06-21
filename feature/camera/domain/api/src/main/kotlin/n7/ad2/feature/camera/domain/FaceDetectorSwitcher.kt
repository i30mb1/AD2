package n7.ad2.feature.camera.domain

import kotlinx.coroutines.flow.StateFlow

interface FaceDetectorSwitcher {
    val detectors: List<FaceDetector>
    val current: StateFlow<FaceDetector>
    fun select(detector: FaceDetector)
}
