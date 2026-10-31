package n7.ad2.android

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.fragment.app.commitNow
import com.google.common.truth.Truth
import kotlinx.coroutines.CompletableDeferred
import n7.ad2.android.extension.postponeEnterUntil
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.Robolectric
import org.robolectric.RobolectricTestRunner
import org.robolectric.shadows.ShadowLooper
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds

@RunWith(RobolectricTestRunner::class)
internal class PostponeEnterUntilTest {

    class OldFragment : Fragment() {
        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = View(inflater.context)
    }

    class NewFragment : Fragment() {
        val ready = CompletableDeferred<Unit>()
        var timeout = 5.seconds

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            postponeEnterUntil(timeout) { ready.await() }
        }

        override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View = View(inflater.context)
    }

    private val activity = Robolectric.buildActivity(FragmentActivity::class.java).setup().get()
    private val container = FrameLayout(activity).apply { id = View.generateViewId() }.also(activity::setContentView)
    private val old = OldFragment()

    init {
        activity.supportFragmentManager.commitNow { add(container.id, old) }
    }

    private fun replaceWith(fragment: Fragment) {
        activity.supportFragmentManager.beginTransaction().setReorderingAllowed(true).replace(container.id, fragment).commit()
        ShadowLooper.idleMainLooper()
    }

    @Test
    fun `old screen stays while new one has no data`() {
        val new = NewFragment()
        replaceWith(new)
        Truth.assertThat(old.view?.parent).isEqualTo(container)
        Truth.assertThat(new.view?.parent).isNull()
    }

    @Test
    fun `new screen is shown once data is ready`() {
        val new = NewFragment()
        replaceWith(new)
        new.ready.complete(Unit)
        ShadowLooper.idleMainLooper()
        Truth.assertThat(new.requireView().parent).isEqualTo(container)
        Truth.assertThat(old.view).isNull()
    }

    @Test
    fun `new screen is shown after timeout even without data`() {
        val new = NewFragment().apply { timeout = 300.milliseconds }
        replaceWith(new)
        ShadowLooper.idleMainLooperFor(java.time.Duration.ofMillis(400))
        Truth.assertThat(new.requireView().parent).isEqualTo(container)
    }
}
