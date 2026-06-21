package n7.ad2.feature.camera.domain.impl.processor

import android.app.Application
import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels
import org.jetbrains.kotlinx.dl.onnx.inference.executionproviders.ExecutionProvider
import org.jetbrains.kotlinx.dl.onnx.inference.facealignment.FaceDetectionModel

class ProcessorKotlinDL(application: Application) : FaceDetect {

    private val hub = ONNXModelHub(application)
    private val internalModel = hub.loadModel(ONNXModels.FaceDetection.UltraFace320, ExecutionProvider.NNAPI())
    private val model: FaceDetectionModel = FaceDetectionModel(internalModel)

    override fun detect(image: Image): List<DetectedFaceNormalized> {
        val bitmap = image.source as Bitmap
        val faces: List<DetectedObject> = model.detectFaces(bitmap, 1)
        return faces.map { face ->
            DetectedFaceNormalized(
                face.xMin,
                face.xMax,
                face.yMin,
                face.yMax,
                face.probability,
            )
        }
    }
}
