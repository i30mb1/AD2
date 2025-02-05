package n7.ad2.heroes.domain.ui

import android.app.Application
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.R
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow
import kotlinx.coroutines.flow.flowOf
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.app.logger.model.AppLog
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.heroes.ui.api.HeroesFragmentFactory
import n7.ad2.heroes.ui.internal.HeroesFragment
import n7.ad2.navigator.Navigator
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentTest {

    private val rule = CoroutineTestRule()
    private val deps = object : HeroesDependencies {
        override val application: Application
            get() = TODO("Not yet implemented")
        override val res: Resources
            get() = TODO("Not yet implemented")
        override val navigator: Navigator
            get() = TODO("Not yet implemented")
        override val logger: Logger = object : Logger {
            override fun log(text: String, params: Map<String, Any>) = Unit
            override fun getSubscriptionCount(): Int = 0
            override fun getLogFlow(): Flow<AppLog> = emptyFlow()
            override fun getLogsFlow(): Flow<List<AppLog>> = emptyFlow()
        }
        override val getHeroesUseCase: GetHeroesUseCase = object : GetHeroesUseCase {
            override fun invoke(): Flow<List<Hero>> = flowOf(
                listOf(Hero("Jeka", "", false, "str"))
            )

        }
        override val dispatchersProvider: DispatchersProvider = rule.dispatchers
        override val updateStateViewedForHeroUseCase: UpdateStateViewedForHeroUseCase
            get() = TODO("Not yet implemented")
    }

    @Test
    fun test() {
        val scenario = launchFragment<HeroesFragment>(
            initialState = Lifecycle.State.INITIALIZED,
            themeResId = R.style.Theme_AppCompat,
            fragmentArgs = null,
            factory = HeroesFragmentFactory(deps),
        )
        scenario.onFragment {
            scenario.moveToState(Lifecycle.State.RESUMED)
        }
        Thread.sleep(10_000)
    }
}
