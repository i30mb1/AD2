package n7.ad2.macrobenchmark

import androidx.benchmark.macro.junit4.BaselineProfileRule
import androidx.test.uiautomator.By
import androidx.test.uiautomator.Direction
import androidx.test.uiautomator.Until
import org.junit.Rule
import org.junit.Test

class BaselineProfileGenerator {

    @get:Rule
    val baselineProfileRule = BaselineProfileRule()

    @Test
    fun generate() {
//        baselineProfileRule.collectBaselineProfile("n7.ad2") {
//            startActivityAndWait()
//            device.wait(Until.hasObject(By.res("rv")), 5000)
//            val rv = device.findObject(By.res("rv"))
//            rv.fling(Direction.DOWN)
//        }
    }

}
