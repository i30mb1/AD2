package n7.ad2.feature.camera.domain.impl.processor

import android.app.Application
import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.model.DetectedFace
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels
import org.jetbrains.kotlinx.dl.onnx.inference.executionproviders.ExecutionProvider
import org.jetbrains.kotlinx.dl.onnx.inference.facealignment.FaceDetectionModel

class ProcessorKotlinDL(
    application: Application,
) : Processor {

    private val hub = ONNXModelHub(application)
    private val internalModel = hub.loadModel(ONNXModels.FaceDetection.UltraFace320, ExecutionProvider.CPU())
    private val model: FaceDetectionModel = FaceDetectionModel(internalModel)

    override fun analyze(image: Image): ProcessorState {
        val bitmap = image.source as Bitmap
        val faces: List<DetectedObject> = model.detectFaces(bitmap, 1)
        val face = faces.firstOrNull()
//        if (face != null) {
//            val canvas = Canvas(bitmap)
//            canvas.drawRect(
//                face.xMin * bitmap.width,
//                face.yMin * bitmap.height,
//                face.xMax * bitmap.width,
//                face.yMax * bitmap.height,
//                Paint().apply {
//                    color = Color.RED
//                }
//            )
//        }
        val detectedFace = if (face != null) DetectedFace(
            face.xMin, face.xMax, face.yMin, face.yMax, face.probability
        ) else null
        return ProcessorState(detectedFace)
    }
}


