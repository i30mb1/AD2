package n7.ad2.camera.internal

import android.app.Application
import android.graphics.Bitmap
import javax.inject.Inject
import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModelHub
import org.jetbrains.kotlinx.dl.onnx.inference.ONNXModels

class Processing @Inject constructor(
    private val application: Application,
) {

    private val hub = ONNXModelHub(application)
    private val model = ONNXModels.FaceDetection.UltraFace320.pretrainedModel(hub)

    fun analyze(bitmap: Bitmap): ProcessingState {
        val faces = model.detectFaces(bitmap, 1)
        return ProcessingState(faces.firstOrNull())
    }
}

class ProcessingState(
    val detectedObject: DetectedObject?,
)
