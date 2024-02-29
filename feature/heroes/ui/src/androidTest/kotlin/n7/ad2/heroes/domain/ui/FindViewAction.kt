package n7.ad2.heroes.domain.ui

import android.view.View
import androidx.test.espresso.UiController
import androidx.test.espresso.ViewAction
import androidx.test.espresso.matcher.ViewMatchers
import org.hamcrest.Matcher

class FindViewAction : ViewAction {

    lateinit var targetView: View

    override fun getDescription(): String {
        return "extract view from activity"
    }

    override fun getConstraints(): Matcher<View> {
        return ViewMatchers.isAssignableFrom(View::class.java)
    }

    override fun perform(uiController: UiController, view: View) {
        targetView = view
    }
}
