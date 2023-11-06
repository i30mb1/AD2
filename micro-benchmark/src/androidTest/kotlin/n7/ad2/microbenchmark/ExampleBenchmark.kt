package n7.ad2.microbenchmark

import android.content.Context
import android.graphics.Bitmap
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Benchmark, which will execute on an Android device.
 *
 * The body of [BenchmarkRule.measureRepeated] is measured in a loop, and Studio will
 * output the result. Modify your code to see how it affects performance.
 */
@RunWith(AndroidJUnit4::class)
class ExampleBenchmark {

    @get:Rule
    val benchmarkRule = BenchmarkRule()
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
//    val bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.img800x450b)

    @Test
    fun testBitmap_CreateScaledBitmap() {
        // 308 007   ns
        benchmarkRule.measureRepeated {
//            Bitmap.createScaledBitmap(bitmap, 255, 255, true)
        }
    }

    @Test
    fun testBitmap_CreateScaledBitmap_Native() {
        // 727 475   ns
        benchmarkRule.measureRepeated {
//            extractor.resize(bitmap, 255, 255)
        }
    }
}
