package n7.ad2.microbenchmark

import android.content.Context
import android.graphics.BitmapFactory
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import n7.ad2.feature.camera.domain.impl.processor.buffer.ByteBuffer
import n7.ad2.feature.camera.domain.impl.processor.buffer.ByteBufferDirect
import n7.ad2.feature.camera.domain.impl.processor.buffer.FloatBuffer
import n7.ad2.feature.camera.domain.impl.processor.buffer.FloatBufferDirect
import n7.ad2.feature.camera.domain.impl.processor.buffer.fill.FillBufferValue
import n7.ad2.micro.benchmark.test.R
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
internal class BenchmarkBuffer {

    @get:Rule val benchmarkRule = BenchmarkRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.face)

    // тут от самого худшего до самого лучшего (внизу лучше)
    private val byteBuffer = ByteBuffer()
    private val byteBufferDirect = ByteBufferDirect()
    private val floatBufferValue = FloatBuffer(FillBufferValue())
    private val floatBufferArray = FloatBuffer()
    private val floatBufferDirectArray = FloatBufferDirect()

    @Test
    fun benchByteBufferDirect() { // BlackHole
        benchmarkRule.measureRepeated {
            byteBufferDirect.get(bitmap)
        }
    }

    @Test
    fun benchFloatBufferValue() {
        benchmarkRule.measureRepeated {
            floatBufferValue.get(bitmap)
        }
    }

    @Test
    fun benchFloatBufferArray() {
        benchmarkRule.measureRepeated {
            floatBufferArray.get(bitmap)
        }
    }

    @Test
    fun benchFloatBufferDirectArray() {
        benchmarkRule.measureRepeated {
            floatBufferDirectArray.get(bitmap)
        }
    }
}
