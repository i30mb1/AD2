package n7.ad2.feature.camera.domain.impl.test

import android.content.Context
import android.graphics.BitmapFactory
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import java.nio.FloatBuffer
import n7.ad2.feature.camera.domain.impl.R
import n7.ad2.feature.camera.domain.impl.processor.buffer.FloatBufferDirect
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class TestByteBuffer {

    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.rgbw)
    private val byteBuffer = FloatBufferDirect()

    @Test
    fun testByteBuffer() {
        val result = byteBuffer.get(bitmap) as FloatBuffer

        // вот так считываем все значения из floatBuffer
        result.rewind()
        val list = buildList {
            while (result.hasRemaining()) add(result.get())
        }
        println()
    }
}
