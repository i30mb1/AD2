import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.screenshot.captureToBitmap
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule
import n7.ad2.xo.demo.XoActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentManagerTest {

    @get:Rule val permission: GrantPermissionRule = GrantPermissionRule.grant(
        android.Manifest.permission.NEARBY_WIFI_DEVICES,
        android.Manifest.permission.ACCESS_COARSE_LOCATION,
        android.Manifest.permission.ACCESS_FINE_LOCATION,
    )

    private val fragment = EmptyFragment()

    @Test
    fun asdf() {
        val activity: ActivityScenario<XoActivity> = launchActivity<XoActivity>()
        activity.onActivity {
            addFragment(it.supportFragmentManager)
            addFragment(it.supportFragmentManager)
        }
        val bitmap = Espresso.onView(ViewMatchers.isRoot()).captureToBitmap()
        activity.close()
    }

    private fun addFragment(fm: FragmentManager) {
        val transaction = fm.beginTransaction()
        transaction.add(fragment, null)
        transaction.commit()
    }
}

class EmptyFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        return TextView(context).apply { text = "Hello World!" }
    }
}