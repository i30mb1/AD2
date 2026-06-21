package n7.ad2.feature.camera.domain.impl.processing.blurriness

import android.graphics.Bitmap
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import n7.ad2.feature.camera.domain.impl.processing.FaceBox
import n7.ad2.feature.camera.domain.impl.processing.ImageEditor
import n7.ad2.feature.camera.domain.impl.processing.MLInterpreter
import n7.ad2.feature.camera.domain.impl.processing.MLModel
import n7.ad2.feature.camera.domain.impl.processing.TfLiteModel
import java.nio.ByteBuffer
import java.nio.ByteOrder

/**
 * BlazeFace модель оценки смазанности лица.
 * Препроцессинг повторяет ноутбук TBIOML-126 (preprocess_image_for_blurriness_model):
 *   1) Smart crop: квадрат вокруг центра лица, сторона = max(faceW, faceH) * (1 + margin),
 *      ограничен меньшей стороной изображения, координаты клампятся в кадр.
 *   2) padToSquare: если crop не квадратный — paddingom до квадрата с чёрным.
 *   3) Resize 128×128.
 *   4) Normalize ImageNet: (px/255 - mean) / std.
 */
@Suppress("MagicNumber")
class Blurriness(
    model: MLModel,
    private val editor: ImageEditor,
) : TfLiteModel() {

    override val modelVersion: String = model.params.versionString
    override val modelName: String = model.params.nameString
    override val interpreter: MLInterpreter = model.interpreter

    private val byteBuffer = ByteBuffer
        .allocateDirect(INPUT_WIDTH * INPUT_HEIGHT * CHANNELS_COUNT * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    private val pixels = IntArray(INPUT_WIDTH * INPUT_HEIGHT)
    private val normalizedPixels = FloatArray(INPUT_WIDTH * INPUT_HEIGHT * CHANNELS_COUNT)
    private val output = Array(1) { FloatArray(1) }
    private val outputs: Map<Int, Any> = hashMapOf(0 to output)
    private val inputs: Array<Any> = arrayOf<Any>(byteBuffer)

    fun inference(bitmap: Bitmap, face: FaceBox): Float {
        val smartCropped = smartCrop(bitmap, face) ?: return 0f
        val square = editor.padToSquare(smartCropped)
        val resized = editor.resize(square, INPUT_WIDTH, INPUT_HEIGHT)
        fillBuffer(resized)
        if (resized !== square) resized.recycle()
        if (square !== smartCropped) square.recycle()
        if (smartCropped !== bitmap) smartCropped.recycle()

        byteBuffer.rewind()
        inputs[0] = byteBuffer
        runInference(inputs, outputs)
        return output[0][0]
    }

    /**
     * Smart crop по аналогу из ноутбука (preprocess_image_for_blurriness_model):
     *   faceSide = int(max(faceW, faceH) * (1 + margin)), но не больше min(imageW, imageH);
     *   crop = квадрат стороны faceSide, центрированный на центре лица, координаты клампятся в кадр.
     */
    private fun smartCrop(bitmap: Bitmap, face: FaceBox): Bitmap? {
        val xMinFace = face.startX.toInt()
        val yMinFace = face.startY.toInt()
        val xMaxFace = face.endX.toInt()
        val yMaxFace = face.endY.toInt()
        val faceWidth = xMaxFace - xMinFace
        val faceHeight = yMaxFace - yMinFace
        val faceCenterX = (xMinFace + xMaxFace) / 2
        val faceCenterY = (yMinFace + yMaxFace) / 2
        val side = (maxOf(faceWidth, faceHeight) * (1f + CROP_MARGIN))
            .toInt()
            .coerceAtMost(minOf(bitmap.width, bitmap.height))
        val xMin = (faceCenterX - side / 2).coerceAtLeast(0)
        val yMin = (faceCenterY - side / 2).coerceAtLeast(0)
        val xMax = (faceCenterX + side / 2).coerceAtMost(bitmap.width)
        val yMax = (faceCenterY + side / 2).coerceAtMost(bitmap.height)
        val w = xMax - xMin
        val h = yMax - yMin
        if (w <= 0 || h <= 0) return null
        return editor.crop(bitmap, xMin, yMin, w, h)
    }

    private fun fillBuffer(bitmap: Bitmap) {
        bitmap.getPixels(pixels, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT)
        byteBuffer.rewind()
        for (index in pixels.indices) {
            val pixel = pixels[index]
            normalizedPixels[index * CHANNELS_COUNT] = normalize(pixel.red, MEAN_R, STD_R)
            normalizedPixels[index * CHANNELS_COUNT + 1] = normalize(pixel.green, MEAN_G, STD_G)
            normalizedPixels[index * CHANNELS_COUNT + 2] = normalize(pixel.blue, MEAN_B, STD_B)
        }
        byteBuffer.put(normalizedPixels)
        byteBuffer.rewind()
    }

    private fun normalize(value: Int, mean: Float, std: Float): Float =
        (value / MAX_COLOR_VALUE - mean) / std

    companion object {
        const val INPUT_WIDTH = 128
        const val INPUT_HEIGHT = 128

        private const val CHANNELS_COUNT = 3
        private const val CROP_MARGIN = 0.2f
        private const val MAX_COLOR_VALUE = 255f

        private const val MEAN_R = 0.485f
        private const val MEAN_G = 0.456f
        private const val MEAN_B = 0.406f
        private const val STD_R = 0.229f
        private const val STD_G = 0.224f
        private const val STD_B = 0.225f
    }
}
