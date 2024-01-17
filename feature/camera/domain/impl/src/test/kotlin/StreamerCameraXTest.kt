import androidx.camera.core.ImageAnalysis
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.testing.TestLifecycleOwner
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.StreamerCameraX
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class StreamerCameraXTest {

    @get:Rule val coroutineRule = CoroutineTestRule()

    private val imageAnalysis: ImageAnalysis = mockk(relaxed = true)
    private val cameraProvider: CameraProvider = mockk(relaxed = true)
    private lateinit var lifecycle: LifecycleOwner
    private lateinit var streamer: StreamerCameraX

    @Before
    fun before() {
        lifecycle = TestLifecycleOwner()
        streamer = StreamerCameraX(cameraProvider, lifecycle)
        streamer._imageAnalysis = { imageAnalysis }
    }

    @Test
    fun `WHEN subscribe THEN call bind`() = runTest {
        val job = streamer.stream.launchIn(this)
        delay(1.seconds)
        coVerify(exactly = 1) { cameraProvider.bind(any()) }
        verify(exactly = 1) { imageAnalysis.setAnalyzer(any(), any()) }
        job.cancelAndJoin()
        delay(StreamerCameraX.SUBSCRIBE_DELAY)
        coVerify(exactly = 1) { cameraProvider.unbind(any()) }
        verify(exactly = 1) { imageAnalysis.clearAnalyzer() }
    }
}
