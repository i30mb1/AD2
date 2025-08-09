package n7.ad2.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Simple baseline profile generator focused on app startup and main screen loading.
 * This is a minimal, reliable test that covers the critical startup path.
 */
@RunWith(AndroidJUnit4::class)
class StartupBaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateStartupBaselineProfile() {
        baselineProfileRule.collect(packageName = "n7.ad2") {
            // This will generate a baseline profile for app startup
            // which is the most important optimization

            // Start the main activity
            startActivityAndWait()

            // Wait for app to be fully loaded and responsive
            device.wait(Until.hasObject(By.pkg("n7.ad2")), 20_000)

            // Let the app fully initialize - this captures the critical startup path
            device.waitForIdle(5_000)

            // Perform minimal interactions to ensure full initialization
            // This helps capture any lazy-loaded components
            device.click(device.displayWidth / 2, device.displayHeight / 2)
            device.waitForIdle(1_000)

            // Basic navigation interaction if possible
            device.pressBack()
            device.waitForIdle(1_000)

            // Let system settle
            device.waitForIdle(2_000)
        }
    }

    @Test
    fun generateColdStartBaselineProfile() {
        baselineProfileRule.collect(packageName = "n7.ad2") {
            // Multiple cold starts to capture comprehensive startup profile
            repeat(3) {
                // Kill app to ensure cold start
                device.executeShellCommand("am force-stop n7.ad2")
                device.waitForIdle(2_000)

                // Cold start
                startActivityAndWait()

                // Wait for full initialization
                device.wait(Until.hasObject(By.pkg("n7.ad2")), 15_000)
                device.waitForIdle(3_000)

                // Basic interaction
                device.click(device.displayWidth / 2, device.displayHeight / 2)
                device.waitForIdle(1_000)
            }
        }
    }
}
