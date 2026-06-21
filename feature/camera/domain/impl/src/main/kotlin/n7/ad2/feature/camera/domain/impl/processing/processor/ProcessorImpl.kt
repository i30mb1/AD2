package n7.ad2.feature.camera.domain.impl.processing.processor

import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.CameraSettings
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.Rotation
import n7.ad2.feature.camera.domain.impl.processing.FaceFromPhotoExtractor
import n7.ad2.feature.camera.domain.impl.processing.blurriness.Blurriness
import n7.ad2.feature.camera.domain.impl.processing.headpose.HeadPose
import n7.ad2.feature.camera.domain.impl.processing.headpose.HeadPoseOutput
import n7.ad2.feature.camera.domain.impl.processing.illumination.IlluminationDetect
import n7.ad2.feature.camera.domain.impl.processing.toFaceBox
import n7.ad2.feature.camera.domain.model.DetectedFace
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

internal class ProcessorImpl(
    private val faceDetect: FaceDetect,
    private val illumination: IlluminationDetect,
    private val headPose: HeadPose,
    private val blurriness: Blurriness,
    private val faceExtractor: FaceFromPhotoExtractor,
    private val settings: CameraSettings,
) : Processor {

    private val modelsName: List<String> = listOf(headPose.modelName, blurriness.modelName)

    override suspend fun init() = Unit

    override suspend fun analyze(image: Image, isRecording: Boolean): ProcessorState {
        val bitmap = image.source as Bitmap
        val illuminationValue = illumination.inference(bitmap)

        val normalizedFaces = faceDetect.detect(image)
        if (normalizedFaces.isEmpty()) {
            return ProcessorState(image, emptyList(), illuminationValue, modelsName)
        }

        val faces = normalizedFaces.mapNotNull { normalizedFace ->
            val box = normalizedFace.toFaceBox(bitmap.width, bitmap.height)
            val faceImage = faceExtractor.extract(bitmap, box) ?: return@mapNotNull null
            val headPoseOutput = if (settings.isHeadPoseEnabled) {
                headPose.inference(faceImage)
            } else {
                HeadPoseOutput(0f, Rotation.ZERO)
            }
            if (faceImage !== bitmap) faceImage.recycle()
            val blur = if (settings.isBlurrinessEnabled) blurriness.inference(bitmap, box) else 0f
            DetectedFace(normalizedFace, headPoseOutput.rotation, headPoseOutput.occlusionScore, blur)
        }

        return ProcessorState(image, faces, illuminationValue, modelsName)
    }

    override suspend fun stop() = Unit
}
