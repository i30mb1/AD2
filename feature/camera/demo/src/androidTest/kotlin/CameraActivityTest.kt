import androidx.appcompat.app.AppCompatDelegate
import androidx.test.core.app.launchActivity
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.rule.GrantPermissionRule
import androidx.test.uiautomator.UiDevice
import n7.ad2.camera.demo.CameraActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Написать тест на проверку пересоздание процесса
 */
@RunWith(AndroidJUnit4::class)
class CameraActivityTest {

    @get:Rule val permission: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.CAMERA,
    )

    @get:Rule val activityScenarioRule = activityScenarioRule<CameraActivity>()
    private val device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation())

    @Test
    fun testEvent() {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        val scenario = launchActivity<CameraActivity>()
        val packageName = device.currentPackageName

        scenario.onActivity {
            device.pressHome()
//            device.executeShellCommand("adb shell am kill $packageName")
            device.pressRecentApps()
        }
//            device.click(device.displayWidth / 2, device.displayHeight / 2)
        Thread.sleep(10_000)
    }
}
