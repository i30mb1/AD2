import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.test.ext.junit.rules.activityScenarioRule
import androidx.test.ext.junit.runners.AndroidJUnit4
import n7.ad2.xo.demo.XoActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
/**
 * невозможно работать с robolectric если конструктор у активити или конструктор у application с параметрами
 */
class FragmentManagerTestWithUnit {

    private val fragment = EmptyFragment()
    @get:Rule var activity = activityScenarioRule<XoActivity>()

    @Test
    fun testFragment() {
        activity.scenario.onActivity {
            addFragment(it.supportFragmentManager)
//            addFragment(it.supportFragmentManager)
        }
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