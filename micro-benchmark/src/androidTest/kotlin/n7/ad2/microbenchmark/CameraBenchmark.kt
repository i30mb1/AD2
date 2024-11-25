package n7.ad2.microbenchmark

import android.content.Context
import androidx.benchmark.junit4.BenchmarkRule
import androidx.benchmark.junit4.measureRepeated
import androidx.camera.core.UseCaseGroup
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.annotation.UiThreadTest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.last
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.RecorderState
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.CameraSettingsImpl
import n7.ad2.feature.camera.domain.impl.recorder.RecorderCameraX
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class CameraBenchmark {

    @get:Rule val benchmarkRule = BenchmarkRule()
    val coroutineRule = CoroutineTestRule()
    private val lifecycle = TestLifecycleOwner()
    private val context: Context = InstrumentationRegistry.getInstrumentation().context
    private val recorder by lazy {
        RecorderCameraX(context, Logger(), DispatchersProvider())
    }
    private val provider = CameraProvider(context, CameraSettingsImpl(isFrontCamera = true), lifecycle)

    @Test
    @UiThreadTest
    fun testCamera() = runTest {
        val usecase = recorder.init()
        provider.bind(
            UseCaseGroup.Builder()
                .addUseCase(usecase)
                .build()
        )
        benchmarkRule.measureRepeated {
            runBlocking {
                recorder.startOnce()
                val file = recorder.state
                    .filterIsInstance<RecorderState.Completed>()
                    .last()
                    .file
                file.isFile
            }
        }
    }
}
