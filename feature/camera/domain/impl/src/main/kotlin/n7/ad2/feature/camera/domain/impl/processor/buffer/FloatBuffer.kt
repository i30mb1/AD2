package n7.ad2.feature.camera.domain.impl.processor.buffer

import android.graphics.Bitmap
import java.nio.Buffer
import java.nio.FloatBuffer
import n7.ad2.feature.camera.domain.impl.processor.buffer.fill.FillBuffer
import n7.ad2.feature.camera.domain.impl.processor.buffer.fill.FillBufferArray

public class FloatBuffer(
    fillBuffer: FillBuffer = FillBufferArray(),
) : InputBuffer, FillBuffer by fillBuffer {

    override fun get(bitmap: Bitmap): Buffer {
        val colorChannelsCount = 3
        val byteBuffer = FloatBuffer
            .allocate(bitmap.width * bitmap.height * colorChannelsCount)
        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)
        fillBuffer(pixels, byteBuffer)
        return byteBuffer
    }
}
