import androidx.fragment.app.testing.launchFragment
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.assertion.ViewAssertions.doesNotExist
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.common.truth.Truth.assertThat
import n7.ad2.core.ui.R
import n7.ad2.hero.page.internal.responses.DialogResponse
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DialogTest {

    @Test
    fun checkDialogOpening() {
        with(
            launchFragment<DialogResponse>(
                themeResId = R.style.AD2Theme,
            )
        ) {
            onFragment { fragment ->
                assertThat(fragment.dialog).isNotNull()
                assertThat(fragment.requireDialog().isShowing).isTrue()
                fragment.dismiss()
                fragment.parentFragmentManager.executePendingTransactions()
                assertThat(fragment.dialog).isNull()
            }
        }

        onView(withText("Cancel")).check(doesNotExist())
    }
}