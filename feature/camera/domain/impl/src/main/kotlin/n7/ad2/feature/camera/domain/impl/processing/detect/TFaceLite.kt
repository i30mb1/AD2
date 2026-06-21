package n7.ad2.feature.camera.domain.impl.processing.detect

import android.graphics.Bitmap
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import n7.ad2.feature.camera.domain.FaceDetect
import n7.ad2.feature.camera.domain.impl.processing.ImageEditor
import n7.ad2.feature.camera.domain.impl.processing.MLInterpreter
import n7.ad2.feature.camera.domain.impl.processing.MLModel
import n7.ad2.feature.camera.domain.impl.processing.TfLiteModel
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

@Suppress("MagicNumber")
class TFaceLite(
    model: MLModel,
    private val editor: ImageEditor,
) : TfLiteModel(), FaceDetect {

    override val modelVersion: String = model.params.versionString
    override val modelName: String = model.params.nameString
    override val interpreter: MLInterpreter = model.interpreter

    private val outputScores = FloatArray(MAX_FACES)
    private val outputBoxes = Array(MAX_FACES) { FloatArray(4) }
    private val byteBuffer = ByteBuffer
        .allocateDirect(INPUT_WIDTH * INPUT_HEIGHT * CHANNELS_COUNT * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    private val pixels = IntArray(CHANNEL_SIZE)
    private val normalizedPixels = FloatArray(CHANNEL_SIZE * CHANNELS_COUNT)
    private val outputs: Map<Int, Any> = hashMapOf(0 to outputScores, 1 to outputBoxes)
    private val inputs: Array<Any> = arrayOf<Any>(byteBuffer)

    override fun detect(image: Image): List<DetectedFaceNormalized> {
        val bitmap = image.source as Bitmap
        val resized = editor.resize(bitmap, INPUT_WIDTH, INPUT_HEIGHT)
        resized.getPixels(pixels, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT)
        if (resized !== bitmap) resized.recycle()
        fillBuffer(pixels, byteBuffer)

        outputScores.fill(0f)
        outputBoxes.forEach { it.fill(0f) }
        inputs[0] = byteBuffer
        runInference(inputs, outputs)

        val faces = mutableListOf<DetectedFaceNormalized>()
        outputScores.forEachIndexed { index, score ->
            if (score > PROBABILITY_THRESHOLD) {
                val faceBox = outputBoxes[index]
                // bbox в формате [x_min, y_min, x_max, y_max] (нормализованные 0-1)
                faces.add(DetectedFaceNormalized(faceBox[0], faceBox[2], faceBox[1], faceBox[3], score))
            }
        }
        return faces
    }

    private fun fillBuffer(pixels: IntArray, buffer: FloatBuffer) {
        buffer.rewind()
        for (index in pixels.indices) {
            val pixel = pixels[index]
            normalizedPixels[index * CHANNELS_COUNT] = normalizeValue(pixel.red, MEAN_R, STD_R)
            normalizedPixels[index * CHANNELS_COUNT + 1] = normalizeValue(pixel.green, MEAN_G, STD_G)
            normalizedPixels[index * CHANNELS_COUNT + 2] = normalizeValue(pixel.blue, MEAN_B, STD_B)
        }
        buffer.put(normalizedPixels)
        buffer.rewind()
    }

    private fun normalizeValue(value: Int, mean: Float, std: Float): Float = (value - mean) / std

    companion object {
        private const val INPUT_WIDTH = 128
        private const val INPUT_HEIGHT = 128
        private const val CHANNELS_COUNT = 3
        private const val CHANNEL_SIZE = INPUT_WIDTH * INPUT_HEIGHT
        private const val MAX_FACES = 10
        private const val PROBABILITY_THRESHOLD = 0.3f

        private const val MEAN_R = 130.389f
        private const val MEAN_G = 116.939f
        private const val MEAN_B = 111.737f
        private const val STD_R = 70.767f
        private const val STD_G = 68.995f
        private const val STD_B = 68.4701f
    }
}
