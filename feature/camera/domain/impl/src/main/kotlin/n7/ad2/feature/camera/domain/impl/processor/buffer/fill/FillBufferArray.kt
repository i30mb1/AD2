package n7.ad2.feature.camera.domain.impl.processor.buffer.fill

import android.graphics.Color
import java.nio.FloatBuffer

/**
 * заполняем буффер используя промежуточные коллекции RRRGGGBBB
 */
public class FillBufferArray : FillBuffer {

    private companion object {
        const val CHANNELS_COUNT = 3
    }

    override fun fillBuffer(pixels: IntArray, byteBuffer: FloatBuffer) {
        val channelSize = byteBuffer.capacity() / CHANNELS_COUNT
        val redList = FloatArray(channelSize)
        val greenList = FloatArray(channelSize)
        val blueList = FloatArray(channelSize)

        for ((index, pixel) in pixels.withIndex()) {
            val r = Color.red(pixel)
            redList[index] = normalizeValue(r)
            val g = Color.green(pixel)
            greenList[index] = normalizeValue(g)
            val b = Color.blue(pixel)
            blueList[index] = normalizeValue(b)
        }
        byteBuffer.put(redList)
        byteBuffer.put(greenList)
        byteBuffer.put(blueList)
    }
}
