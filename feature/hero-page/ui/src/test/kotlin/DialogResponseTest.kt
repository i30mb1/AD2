import android.os.Build
import androidx.core.os.bundleOf
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.runner.screenshot.Screenshot
import n7.ad2.core.ui.R
import n7.ad2.hero.page.internal.responses.DialogResponse
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode

@RunWith(AndroidJUnit4::class)
// GraphicsMode is not supported in windows https://github.com/robolectric/robolectric/issues/8312
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(
    manifest = Config.NONE,
    sdk = [Build.VERSION_CODES.TIRAMISU],
    qualifiers = "ru-rRU-w412dp-h732dp-night-navhidden",
)
class DialogResponseTest {

    @Test
    fun test() {
        val fragmentArgs = bundleOf("selectedListItem" to 0)
        val scenario = launchFragmentInContainer<DialogResponse>(
            fragmentArgs = fragmentArgs,
            themeResId = R.style.AD2Theme,
        )
        scenario.onFragment {
            scenario.moveToState(Lifecycle.State.RESUMED)
        }

        val bitmap = Screenshot.capture().bitmap
    }
}
