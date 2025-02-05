package n7.ad2.feature.camera.domain.impl.processor.buffer.fill

import android.graphics.Color
import java.nio.FloatBuffer

/**
 * заполняем буффер без испольования промежуточной колекции RRRGGGBBB
 */
public class FillBufferValue : FillBuffer {

    override fun fillBuffer(pixels: IntArray, byteBuffer: FloatBuffer) {
        val valuesPerChannel = pixels.size
        for ((pixelIndex, pixelValue) in pixels.withIndex()) {
            val r = Color.red(pixelValue)
            val g = Color.green(pixelValue)
            val b = Color.blue(pixelValue)

            val redIndex = pixelIndex
            val greenIndex = redIndex + valuesPerChannel
            val blueIndex = greenIndex + valuesPerChannel

            val normalizedRed = normalizeValue(r)
            val normalizedGreen = normalizeValue(g)
            val normalizedBlue = normalizeValue(b)

            byteBuffer.put(redIndex, normalizedRed)
            byteBuffer.put(greenIndex, normalizedGreen)
            byteBuffer.put(blueIndex, normalizedBlue)
        }
    }
}
