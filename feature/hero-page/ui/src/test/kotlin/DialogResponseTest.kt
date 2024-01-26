import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.screenshot.Screenshot
import n7.ad2.hero.page.internal.responses.DialogResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config

@RunWith(AndroidJUnit4::class)
// GraphicsMode is not supported in windows https://github.com/robolectric/robolectric/issues/8312
// @GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    manifest = Config.NONE,
)
class DialogResponseTest {

    @Test
    fun test() {
        val fragmentArgs = bundleOf("selectedListItem" to 0)
        val scenario = launchFragmentInContainer<DialogResponse>(
            fragmentArgs = fragmentArgs,
            themeResId = com.google.android.material.R.style.Base_ThemeOverlay_AppCompat_Dark
        )
        scenario.onFragment {
            scenario.moveToState(Lifecycle.State.RESUMED)
        }

        val bitmap = Screenshot.capture().bitmap
        val bitmap2 = Espresso.onView(ViewMatchers.isRoot()).captureToBitmap()
        println("")
    }
}

