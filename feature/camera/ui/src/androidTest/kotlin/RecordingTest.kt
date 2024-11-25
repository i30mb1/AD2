import android.content.Context
import androidx.camera.core.UseCaseGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.annotation.UiThreadTest
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.StandardTestDispatcher
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.feature.camera.domain.RecorderState
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.CameraSettingsImpl
import n7.ad2.feature.camera.domain.impl.recorder.RecorderCameraX
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordingTest {

//    @get:Rule val permission: GrantPermissionRule = GrantPermissionRule.grant(
//        android.Manifest.permission.CAMERA,
//    )

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())
    private val lifecycle = TestLifecycleOwner(initialState = Lifecycle.State.RESUMED)
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val recorder = RecorderCameraX(context, Logger(), coroutineRule.dispatchers, lifecycle.lifecycleScope)
    private val provider = CameraProvider(context, CameraSettingsImpl(isFrontCamera = true), lifecycle)

    @Test
    @UiThreadTest
    fun test(): Unit = runBlocking {
        val usecase = recorder.init()
        provider.bind(
            UseCaseGroup.Builder()
                .addUseCase(usecase)
                .build()
        )
        recorder.startOnce()
        val file = recorder.state
            .onEach { println("Hello% $it") }
            .filterIsInstance<RecorderState.Completed>()
            .first()
            .file
        file.isFile
    }
}
