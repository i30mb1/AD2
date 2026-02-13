import androidx.test.ext.junit.rules.ActivityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import n7.ad2.ui.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class Test {

    @get:Rule
    val activityRule = ActivityScenarioRule(MainActivity::class.java)

    @Test
    fun appLaunchesSuccessfully() {
//        activityRule.scenario.moveToState(Lifecycle.State.RESUMED)
//        Espresso.onView(ViewMatchers.withId(R.id.tv_fps))
//            .check(ViewAssertions.matches(ViewMatchers.isDisplayed()))
    }
}
