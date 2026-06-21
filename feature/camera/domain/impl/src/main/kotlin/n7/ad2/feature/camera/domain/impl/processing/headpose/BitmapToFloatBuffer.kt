package n7.ad2.feature.camera.domain.impl.processing.headpose

import android.graphics.Bitmap
import androidx.core.graphics.blue
import androidx.core.graphics.green
import androidx.core.graphics.red
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer

class BitmapToFloatBuffer {

    private val byteBuffer = ByteBuffer.allocateDirect(PIXELS * COLOR_CHANNELS_COUNT * Float.SIZE_BYTES)
        .order(ByteOrder.nativeOrder())
        .asFloatBuffer()
    private val pixels: IntArray = IntArray(PIXELS)
    private val normalizedPixels = FloatArray(PIXELS * COLOR_CHANNELS_COUNT)

    fun toByteBuffer(bitmap: Bitmap): Buffer? {
        try {
            bitmap.getPixels(pixels, 0, INPUT_WIDTH, 0, 0, INPUT_WIDTH, INPUT_HEIGHT)
            fillByteBuffer(pixels, byteBuffer)
        } catch (error: Exception) {
            return null
        }
        byteBuffer.rewind()
        return byteBuffer
    }

    private fun fillByteBuffer(pixelsArray: IntArray, byteBuffer: FloatBuffer) {
        byteBuffer.rewind()
        for (index in pixelsArray.indices) {
            val pixel = pixelsArray[index]
            normalizedPixels[index * COLOR_CHANNELS_COUNT] = normalizeValue(pixel.red / MAX_COLOR_VALUE, MEAN_R, STD_R)
            normalizedPixels[index * COLOR_CHANNELS_COUNT + 1] = normalizeValue(pixel.green / MAX_COLOR_VALUE, MEAN_G, STD_G)
            normalizedPixels[index * COLOR_CHANNELS_COUNT + 2] = normalizeValue(pixel.blue / MAX_COLOR_VALUE, MEAN_B, STD_B)
        }
        byteBuffer.put(normalizedPixels)
    }

    private fun normalizeValue(value: Float, mean: Float, std: Float): Float {
        return (value - mean) / std
    }

    companion object {
        private const val INPUT_WIDTH = 224
        private const val INPUT_HEIGHT = 224
        private const val PIXELS = INPUT_WIDTH * INPUT_HEIGHT
        private const val COLOR_CHANNELS_COUNT = 3
        private const val MAX_COLOR_VALUE = 255f
        private const val MEAN_R = 0.485f
        private const val MEAN_G = 0.456f
        private const val MEAN_B = 0.406f
        private const val STD_R = 0.229f
        private const val STD_G = 0.224f
        private const val STD_B = 0.225f
    }
}
