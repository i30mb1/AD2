@file:Suppress("FoldInitializerAndIfToElvis")

package n7.ad2.feature.camera.domain.impl.processor

import android.app.Application
import android.graphics.Bitmap
import android.graphics.Canvas
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import kotlin.math.roundToInt
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.model.DetectedFaceNormalized
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState
import org.tensorflow.lite.Interpreter

@Suppress("MagicNumber")
class ProcessorBlazeFace(
    application: Application,
) : Processor {

    private val interpreter: Interpreter = GetMLModelChannel(application).get()
    private val outputScores = FloatArray(100)
    private val outputBoxes = Array(100) { FloatArray(4) }
    private val byteBuffer = ByteBuffer
        .allocateDirect(INPUT_WIDTH * INPUT_HEIGHT * CHANNELS_COUNT * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    private val pixels = IntArray(CHANNEL_SIZE)
    private val normalizedPixels = FloatArray(CHANNEL_SIZE * CHANNELS_COUNT)
    private val paddedBitmap = Bitmap.createBitmap(INPUT_HEIGHT, INPUT_WIDTH, Bitmap.Config.ARGB_8888)
    private val paddedCanvas = Canvas(paddedBitmap)
    private val normalizedPadFaces = mutableListOf<DetectedFaceNormalized>()
    private var padX = 0
    private var padY = 0

    override fun analyze(image: Image): ProcessorState {
        val bitmap = image.source as Bitmap
        val longestMaxSizeBitmap = longestMaxSize(bitmap)
        val padBitmap = pad(longestMaxSizeBitmap)
        val inputBuffer = preProcessToInputBuffer(padBitmap)

        if (inputBuffer == null) error("inputBuffer == null")

        val outputs: Map<Int, Any> = hashMapOf(0 to outputScores, 1 to outputBoxes)

        val inputs: Array<Any> = arrayOf(inputBuffer)
        interpreter.runForMultipleInputsOutputs(inputs, outputs)

        normalizedPadFaces.clear()
        outputScores.forEachIndexed { index, score ->
            if (score > PROBABILITY_THRESHOLD) {
                val faceBox = outputBoxes[index]
                val faceRect = DetectedFaceNormalized(faceBox[0], faceBox[2], faceBox[1], faceBox[3], score)
                normalizedPadFaces.add(faceRect)
            }
        }
        if (normalizedPadFaces.isEmpty()) return ProcessorState(image, null)

        val normalizedFaces = normalizedPadFaces.map {
            convertCoordinates(it, longestMaxSizeBitmap.height, longestMaxSizeBitmap.width)
        }
        val absoluteFaces = normalizedFaces.map {
            DetectedFaceNormalized(
                (it.xMin * bitmap.width),
                (it.xMax * bitmap.width),
                (it.yMin * bitmap.height),
                (it.yMax * bitmap.height),
                it.probability,
            )
        }

        return ProcessorState(image, normalizedFaces.firstOrNull())
    }

    private fun convertCoordinates(
        faceRect: DetectedFaceNormalized,
        originalHeight: Int,
        originalWeight: Int,
    ): DetectedFaceNormalized {
        val absoluteFace = faceRect.map { x, y -> x * INPUT_WIDTH to y * INPUT_HEIGHT }
        return DetectedFaceNormalized(
            (absoluteFace.xMin - padX / 2) / originalWeight,
            (absoluteFace.xMax - padX / 2) / originalWeight,
            (absoluteFace.yMin - padY / 2) / originalHeight,
            (absoluteFace.yMax - padY / 2) / originalHeight,
            faceRect.probability,
        )
    }

    private fun longestMaxSize(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        val scale = INPUT_WIDTH.toFloat() / maxOf(width, height)
        return if (scale != 1.0f) {
            val newWidth = (width * scale).roundToInt()
            val newHeight = (height * scale).roundToInt()
            padX = INPUT_WIDTH - newWidth
            padY = INPUT_HEIGHT - newHeight
            Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
        } else {
            bitmap
        }
    }

    private fun pad(bitmap: Bitmap): Bitmap {
        val width = bitmap.width
        val height = bitmap.height
        paddedCanvas.drawRGB(255, 255, 255)
        val xOffset = (INPUT_WIDTH - width) / 2
        val yOffset = (INPUT_HEIGHT - height) / 2
        paddedCanvas.drawBitmap(bitmap, xOffset.toFloat(), yOffset.toFloat(), null)
        return paddedBitmap
    }

    private fun preProcessToInputBuffer(bitmap: Bitmap): Buffer? {
        bitmap.getPixels(pixels, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT)
        fillBuffer(pixels, byteBuffer)
        return byteBuffer
    }

    private fun fillBuffer(pixels: IntArray, buffer: FloatBuffer) {
        buffer.rewind()
        for ((index, pixel) in pixels.withIndex()) {
            normalizedPixels[index * CHANNELS_COUNT] = normalizeValue(pixel.red, MEAN_R, STD_R)
            normalizedPixels[index * CHANNELS_COUNT + 1] = normalizeValue(pixel.green, MEAN_G, STD_G)
            normalizedPixels[index * CHANNELS_COUNT + 2] = normalizeValue(pixel.blue, MEAN_B, STD_B)
        }
        buffer.put(normalizedPixels)
    }

    private fun normalizeValue(value: Int, mean: Float, std: Float): Float {
        return (value - mean) / std
    }

    companion object {
        const val INPUT_WIDTH = 128
        const val INPUT_HEIGHT = 128

        private const val PROBABILITY_THRESHOLD = 0.3f

        private const val CHANNELS_COUNT = 3
        private const val CHANNEL_SIZE = INPUT_WIDTH * INPUT_HEIGHT

        private const val MEAN_R = 111.787f
        private const val STD_R = 68.389f
        private const val MEAN_G = 116.956f
        private const val MEAN_B = 130.41f
        private const val STD_G = 68.919f
        private const val STD_B = 70.756f
    }
}
