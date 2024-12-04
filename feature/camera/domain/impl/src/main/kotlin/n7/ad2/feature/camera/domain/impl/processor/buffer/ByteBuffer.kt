package n7.ad2.feature.camera.domain.impl.processor.buffer

import android.graphics.Bitmap
import android.graphics.Color
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder

public class ByteBuffer : InputBuffer {

    override fun get(bitmap: Bitmap): Buffer {
        val colorChannelsCount = 3

        val byteBuffer = ByteBuffer
            .allocate(bitmap.width * bitmap.height * colorChannelsCount * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        fillByteBuffer(pixels, byteBuffer)
        byteBuffer.rewind()
        return byteBuffer
    }

    private fun fillByteBuffer(pixels: IntArray, byteBuffer: ByteBuffer) {
        val valuesPerChannel = pixels.size * Float.SIZE_BYTES
        for ((pixelIndex, pixelValue) in pixels.withIndex()) {
            val r = Color.red(pixelValue)
            val g = Color.green(pixelValue)
            val b = Color.blue(pixelValue)

            val redIndex = pixelIndex * Float.SIZE_BYTES
            val greenIndex = redIndex + valuesPerChannel
            val blueIndex = greenIndex + valuesPerChannel
            val normalizedRed = normalizeValue(r)
            val normalizedGreen = normalizeValue(g)
            val normalizedBlue = normalizeValue(b)

            byteBuffer.putFloat(redIndex, normalizedRed)
            byteBuffer.putFloat(greenIndex, normalizedGreen)
            byteBuffer.putFloat(blueIndex, normalizedBlue)
        }
    }

    private fun normalizeValue(value: Int): Float {
        val mean = 127.5f
        val std = 127.5f
        return (value - mean) / std
    }
}
