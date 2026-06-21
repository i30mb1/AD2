package n7.ad2.feature.camera.domain.impl.processing.headpose

import android.graphics.Bitmap
import n7.ad2.feature.camera.domain.Rotation
import n7.ad2.feature.camera.domain.impl.processing.ImageEditor
import n7.ad2.feature.camera.domain.impl.processing.MLInterpreter
import n7.ad2.feature.camera.domain.impl.processing.MLModel
import n7.ad2.feature.camera.domain.impl.processing.TfLiteModel
import java.nio.FloatBuffer
import kotlin.math.PI
import kotlin.math.atan2
import kotlin.math.sqrt

class HeadPose(
    model: MLModel,
    private val imageEditor: ImageEditor,
) : TfLiteModel() {

    private val bitmapToFloatBuffer = BitmapToFloatBuffer()
    override val modelVersion: String = model.params.versionString
    override val interpreter: MLInterpreter = model.interpreter
    override val modelName: String = model.params.nameString

    private val rotationOutput = Array(1) { Array(ROTATION_MATRIX_SIZE) { FloatArray(ROTATION_MATRIX_SIZE) } }
    private val scoreOutput = FloatBuffer.allocate(1)
    private val outputs: Map<Int, Any> = hashMapOf(0 to rotationOutput, 1 to scoreOutput)
    private val inputs: Array<Any> = arrayOf<Any>(scoreOutput)

    fun inference(faceBitmap: Bitmap): HeadPoseOutput {
        val croppedBitmap = imageEditor.resizeToMinSideAndCenterCrop(faceBitmap, MIN_SIZE.toInt(), INPUT_WIDTH, INPUT_HEIGHT)
        val buffer = bitmapToFloatBuffer.toByteBuffer(croppedBitmap)
        if (croppedBitmap !== faceBitmap) croppedBitmap.recycle()
        if (buffer == null) return HeadPoseOutput(0f, Rotation.ZERO)

        buffer.rewind()
        scoreOutput.rewind()
        inputs[0] = buffer
        runInference(inputs, outputs)

        return HeadPoseOutput(
            occlusionScore = scoreOutput[0].coerceIn(0f, 1f),
            rotation = getRotation(rotationOutput[0]),
        )
    }

    private fun getRotation(rotationMatrix: Array<FloatArray>): Rotation {
        val magnitude = sqrt(
            rotationMatrix[0][0] * rotationMatrix[0][0] +
                rotationMatrix[1][0] * rotationMatrix[1][0],
        )
        val x: Float
        val y: Float
        val z: Float
        if (magnitude >= MAGNITUDE_TOO_SMALL_THRESHOLD) {
            x = atan2(rotationMatrix[2][1], rotationMatrix[2][2]) // pitch
            y = atan2(-rotationMatrix[2][0], magnitude) // yaw
            z = atan2(rotationMatrix[1][0], rotationMatrix[0][0]) // roll
        } else {
            x = atan2(-rotationMatrix[1][2], rotationMatrix[1][1])
            y = atan2(-rotationMatrix[2][0], magnitude)
            z = 0f
        }

        return Rotation(
            x = x * RADIANS_TO_DEGREES,
            y = y * RADIANS_TO_DEGREES,
            z = z * RADIANS_TO_DEGREES,
        )
    }

    companion object {
        private const val MIN_SIZE = 256f
        private const val INPUT_WIDTH = 224
        private const val INPUT_HEIGHT = 224
        private const val ROTATION_MATRIX_SIZE = 3
        private const val MAGNITUDE_TOO_SMALL_THRESHOLD = 1e-6f
        private const val RADIANS_TO_DEGREES = 180f / PI.toFloat()
    }
}
