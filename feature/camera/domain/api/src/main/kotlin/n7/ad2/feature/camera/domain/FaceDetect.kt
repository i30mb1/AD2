package n7.ad2.feature.camera.domain

import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image

interface FaceDetect {
    fun detect(image: Image): List<DetectedFaceNormalized>
}
