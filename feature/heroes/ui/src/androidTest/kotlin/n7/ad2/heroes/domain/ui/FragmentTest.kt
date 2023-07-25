package n7.ad2.heroes.domain.ui

import android.app.Application
import androidx.fragment.app.testing.launchFragment
import androidx.lifecycle.Lifecycle
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.android.material.R
import com.squareup.moshi.Moshi
import n7.ad2.Resources
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.heroes.domain.usecase.UpdateStateViewedForHeroUseCase
import n7.ad2.heroes.ui.api.HeroesDependencies
import n7.ad2.heroes.ui.internal.HeroesFragment
import n7.ad2.navigator.Navigator
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FragmentTest {

    private val deps = object : HeroesDependencies {
        override val application: Application
            get() = TODO("Not yet implemented")
        override val res: Resources
            get() = TODO("Not yet implemented")
        override val navigator: Navigator
            get() = TODO("Not yet implemented")
        override val logger: Logger
            get() = TODO("Not yet implemented")
        override val moshi: Moshi
            get() = TODO("Not yet implemented")
        override val getHeroesUseCase: GetHeroesUseCase
            get() = TODO("Not yet implemented")
        override val dispatchersProvider: DispatchersProvider
            get() = TODO("Not yet implemented")
        override val updateStateViewedForHeroUseCase: UpdateStateViewedForHeroUseCase
            get() = TODO("Not yet implemented")
    }

    @Test
    fun test() {
        val scenario = launchFragment(
            initialState = Lifecycle.State.INITIALIZED,
            themeResId = R.style.Theme_AppCompat,
            instantiate = {
                HeroesFragment().apply {
                    this.dependenciesMap = mapOf(HeroesDependencies::class.java to deps)
                }
            }
        )
        scenario.onFragment {
            scenario.moveToState(Lifecycle.State.RESUMED)
        }
    }
}
