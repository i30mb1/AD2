package n7.ad2.feature.camera.domain.impl.processor.buffer

import android.graphics.Bitmap
import java.nio.Buffer
import java.nio.ByteBuffer
import java.nio.ByteOrder
import n7.ad2.feature.camera.domain.impl.processor.buffer.fill.FillBuffer
import n7.ad2.feature.camera.domain.impl.processor.buffer.fill.FillBufferArray


public class FloatBufferDirect(
    fillBuffer: FillBuffer = FillBufferArray(),
) : InputBuffer, FillBuffer by fillBuffer {

    override fun get(bitmap: Bitmap): Buffer {
        val colorChannelsCount = 3

        val byteBuffer = ByteBuffer
            .allocateDirect(bitmap.width * bitmap.height * colorChannelsCount * Float.SIZE_BYTES)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()

        val pixels = IntArray(bitmap.width * bitmap.height)
        bitmap.getPixels(pixels, 0, bitmap.width, 0, 0, bitmap.width, bitmap.height)

        fillBuffer(pixels, byteBuffer)
        byteBuffer.rewind()
        return byteBuffer
    }
}
