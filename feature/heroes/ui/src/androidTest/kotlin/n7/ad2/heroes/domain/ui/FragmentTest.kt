package n7.ad2.heroes.domain.ui

import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import io.mockk.every
import io.mockk.mockk
import io.mockk.mockkStatic
import io.mockk.slot
import n7.ad2.android.findDependencies
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.heroes.ui.internal.HeroesFragment
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentTest {

    private val fragmentSlot = slot<HeroesFragment>()

    @Before
    fun before() {
        mockkStatic("n7.ad2.android.DependenciesKt")
        every { HeroesFragment().findDependencies<HeroesDependencies>() } returns mockk()
    }

    @Test
    fun test() {
        val scenario = launchFragment<HeroesFragment>(
            initialState = Lifecycle.State.INITIALIZED,
//            themeResId = R.style.Theme_AppCompat,
        )
//        scenario.onFragment { fragment ->
//            scenario.moveToState(Lifecycle.State.RESUMED)
//        }
    }
}
