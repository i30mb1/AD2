package n7.ad2.ui.splash

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth
import com.nhaarman.mockitokotlin2.mock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import n7.ad2.CoroutineTestRule
import n7.ad2.test
import n7.ad2.ui.splash.domain.interactor.ShouldWeShowSplashScreenInteractor
import n7.ad2.ui.splash.domain.usecase.GetCurrentDateInYearUseCase
import n7.ad2.ui.splash.domain.usecase.GetLastDayShownSplashScreenUseCase
import n7.ad2.ui.splash.domain.usecase.GetRandomEmoteUseCase
import n7.ad2.ui.splash.domain.usecase.SaveCurrentDateUseCase
import n7.ad2.ui.splash.domain.usecase.SaveLastDayShownSplashScreenUseCase
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import java.util.Calendar

@ExperimentalCoroutinesApi
class SplashViewModelTest {

    @get:Rule
    val rule: TestRule = InstantTaskExecutorRule()

    @get:Rule
    val coroutineTestRule = CoroutineTestRule()

    private lateinit var splashViewModel: SplashViewModel

    @Before
    fun before() {
        splashViewModel = SplashViewModel(
            mock(),
            SaveCurrentDateUseCase(
                coroutineTestRule.testDispatcher,
                mock(),
                GetCurrentDateInYearUseCase(coroutineTestRule.testDispatcher, Calendar.getInstance())
            ),
            GetRandomEmoteUseCase(coroutineTestRule.testDispatcher),
            ShouldWeShowSplashScreenInteractor(
                GetCurrentDateInYearUseCase(coroutineTestRule.testDispatcher, Calendar.getInstance()),
                GetLastDayShownSplashScreenUseCase(coroutineTestRule.testDispatcher, mock()),
                SaveLastDayShownSplashScreenUseCase(coroutineTestRule.testDispatcher, mock())
            )
        )
    }

    @Test
    fun te() {
        val value = splashViewModel.textEmote.test().value()
        Truth.assertThat(value).isNotEmpty()
    }

}