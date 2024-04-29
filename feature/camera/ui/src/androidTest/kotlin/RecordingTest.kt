import android.content.Context
import androidx.lifecycle.testing.TestLifecycleOwner
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.camera.domain.impl.CameraProvider
import n7.ad2.feature.camera.domain.impl.CameraSettingsImpl
import n7.ad2.feature.camera.domain.impl.recorder.RecorderCameraX
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class RecordingTest {

    @get:Rule val permission: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA,
    )

    //    @get:Rule val coroutineRule = CoroutineTestRule()
    private val context = ApplicationProvider.getApplicationContext<Context>()
    private val recorder by lazy {
        RecorderCameraX(
            context,
            CameraSettingsImpl(),
            CameraProvider(context, CameraSettingsImpl(), TestLifecycleOwner()),
            DispatchersProvider(),
        )
    }

    @Test
//    @UiThreadTest
    fun test() = runTest {
//        Threads.checkMainThread()
        recorder.init()
        val file = recorder.start()
        println(file.name)
    }
}