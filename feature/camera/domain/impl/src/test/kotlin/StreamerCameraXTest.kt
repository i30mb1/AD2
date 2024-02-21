
import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth
import io.mockk.mockk
import io.mockk.verify
import java.util.concurrent.Executor
import kotlin.reflect.KCallable
import kotlin.reflect.jvm.isAccessible
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.StreamerCameraX
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.Implementation
import org.robolectric.annotation.Implements
import org.robolectric.shadow.api.Shadow

@RunWith(AndroidJUnit4::class)
@Config(
    shadows = [ImageAnalysisShadow::class],
    manifest = Config.NONE,
)
class StreamerCameraXTest {

    @get:Rule val coroutineRule = CoroutineTestRule()

    private val cameraProvider: CameraProvider = mockk(relaxed = true)
    private lateinit var lifecycle: LifecycleOwner
    private lateinit var streamer: Streamer

    @Before
    fun before() {
        lifecycle = TestLifecycleOwner()
        streamer = StreamerCameraX(cameraProvider, lifecycle)
    }

    @Test
    fun `WHEN subscribe THEN call bind`() = runTest {
        val imageAnalysis = getImageAnalysis(streamer)
        val job = streamer.stream.launchIn(this)
        delay(1.seconds)
        verify(exactly = 1) { cameraProvider.bind(any()) }
        Truth.assertThat(imageAnalysis.setAnalyzerCalled).isTrue()
        job.cancelAndJoin()
        delay(StreamerCameraX.SUBSCRIBE_DELAY)
        verify(exactly = 1) { cameraProvider.unbind(any()) }
        Truth.assertThat(imageAnalysis.clearAnalyzerCalled).isTrue()
    }

    private fun getImageAnalysis(streamer: Streamer): ImageAnalysisShadow {
        // TODO do not work with `by lazy` ReflectionHelpers.getField<ImageAnalysis>(streamer,"imageAnalysis")
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
