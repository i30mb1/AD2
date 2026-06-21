package n7.ad2.feature.camera.domain.impl.processor

import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image

class ProcessorCamera2(private val faceSource: Camera2FaceSource) : FaceDetect {

    override fun detect(image: Image): List<DetectedFaceNormalized> = listOfNotNull(faceSource.face())
}
