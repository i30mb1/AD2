package n7.ad2.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Generates baseline profile for the main application flow
 */
@RunWith(AndroidJUnit4::class)
class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generateMainFlowBaselineProfile() {
        baselineProfileRule.collect(packageName = "n7.ad2") {
            // Start main activity and wait for it to load
            startActivityAndWait()

            // Wait for splash screen to disappear and main content to load
            device.wait(Until.hasObject(By.pkg("n7.ad2")), 15_000)

            // Wait for app to fully initialize
            device.waitForIdle(3_000)

            // Look for any recyclerview (more generic approach)
            var foundInteraction = false

            // Try to find menu recyclerview
            val menuRecyclerView = device.wait(
                Until.findObject(By.res("rv_menu")),
                3_000,
            )

            if (menuRecyclerView != null) {
                // Scroll through main menu to trigger loading
                menuRecyclerView.fling(Direction.DOWN)
                device.waitForIdle(1_000)
                menuRecyclerView.fling(Direction.UP)
                device.waitForIdle(1_000)
                foundInteraction = true
            }

            // If menu not found, look for any recyclerview
            if (!foundInteraction) {
                val anyRecyclerView = device.findObjects(By.clazz("androidx.recyclerview.widget.RecyclerView"))
                if (anyRecyclerView.isNotEmpty()) {
                    anyRecyclerView.first().fling(Direction.DOWN)
                    device.waitForIdle(1_000)
                    foundInteraction = true
                }
            }

            // Try generic interactions to warm up the app
            if (!foundInteraction) {
                // Swipe gestures to trigger UI interactions
                val displayWidth = device.displayWidth
                val displayHeight = device.displayHeight

                // Horizontal swipe
                device.swipe(
                    displayWidth / 4,
                    displayHeight / 2,
                    displayWidth * 3 / 4,
                    displayHeight / 2,
                    10,
                )
                device.waitForIdle(1_000)

                // Vertical swipe
                device.swipe(
                    displayWidth / 2,
                    displayHeight * 3 / 4,
                    displayWidth / 2,
                    displayHeight / 4,
                    10,
                )
                device.waitForIdle(1_000)
            }

            // Try to find settings button with different approaches
            val settingsButton = device.findObject(By.res("iv_settings"))
                ?: device.findObject(By.desc("Settings"))
                ?: device.findObject(By.textContains("Settings"))

            if (settingsButton != null) {
                try {
                    settingsButton.click()
                    device.waitForIdle(2_000)
                    device.pressBack()
                    device.waitForIdle(1_000)
                } catch (e: Exception) {
                    // Ignore click failures
                }
            }

            // Additional warm-up interactions
            device.waitForIdle(2_000)
        }
    }
}
