import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.plus
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.impl.CameraSettingsImpl
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.impl.streamer.StreamerCameraX
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow
import java.util.concurrent.Executor
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible

@RunWith(AndroidJUnit4::class)
@Config(
    shadows = [ImageAnalysisShadow::class],
    manifest = Config.NONE,
)
class StreamerCameraXTest {

    private val lifecycle: LifecycleOwner = TestLifecycleOwner()
    private val streamer: Streamer = StreamerCameraX(CameraSettingsImpl(), DispatchersProvider(), lifecycle, FPSTimer(Logger()))

    @Test
    fun `WHEN subscribe THEN call bind`() = runTest {
        val imageAnalysis = getImageAnalysis(streamer)
        val job = streamer.stream.launchIn(this + UnconfinedTestDispatcher())
        Truth.assertThat(imageAnalysis.setAnalyzerCalled).isTrue()
        job.cancel()
        Truth.assertThat(imageAnalysis.clearAnalyzerCalled).isTrue()
    }

    private fun getImageAnalysis(streamer: Streamer): ImageAnalysisShadow {
        // better use ReflectionHelpers.getField<ImageAnalysis>(streamer,"imageAnalysis")
        // but it is not working when we need to extract `by lazy`
        val origin: KCallable<*> = streamer.javaClass.kotlin.members.find { it.name == "imageAnalysis" }!!
        origin.isAccessible = true
        return Shadow.extract(origin.call(streamer) as ImageAnalysis)
    }
}

@Implements(ImageAnalysis::class)
class ImageAnalysisShadow {

    var setAnalyzerCalled = false
    var clearAnalyzerCalled = false

    @Implementation
    fun setAnalyzer(executor: Executor, analyzer: ImageAnalysis.Analyzer) {
        setAnalyzerCalled = true
    }

    @Implementation
    fun clearAnalyzer() {
        clearAnalyzerCalled = true
    }
}
