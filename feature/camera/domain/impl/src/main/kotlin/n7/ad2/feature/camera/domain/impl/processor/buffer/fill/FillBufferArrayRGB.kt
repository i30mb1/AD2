package n7.ad2.feature.camera.domain.impl.processor.buffer.fill

import android.graphics.Color
import java.nio.FloatBuffer

/**
 * заполняем буффер используя промежуточные коллекции RGBRGBRGB
 */
public class FillBufferArrayRGB : FillBuffer {

    private companion object {
        const val CHANNELS_COUNT = 3
    }

    override fun fillBuffer(pixels: IntArray, byteBuffer: FloatBuffer) {
        val channelSize = byteBuffer.capacity() / CHANNELS_COUNT
        val normalizedPixels = FloatArray(channelSize * CHANNELS_COUNT)

        for ((index, pixel) in pixels.withIndex()) {
            val r = Color.red(pixel)
            normalizedPixels[index * CHANNELS_COUNT] = normalizeValue(r)
            val g = Color.green(pixel)
            normalizedPixels[index * CHANNELS_COUNT + 1] = normalizeValue(g)
            val b = Color.blue(pixel)
            normalizedPixels[index * CHANNELS_COUNT + 2] = normalizeValue(b)
        }
        byteBuffer.put(normalizedPixels)
    }
}
