package n7.ad2

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineScheduler
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import n7.ad2.coroutines.DispatchersProvider
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * This class is a unit test rule which watches for tests starting and finishing.
 * It contains a reference to a TestCoroutineDispatcher, and as tests are starting and stopping it overrides the default Dispatchers.Main
 * dispatcher and replaces the default with our test dispatcher.
 */
@ExperimentalCoroutinesApi
class CoroutineTestRule(
    val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(TestCoroutineScheduler()),
) : TestWatcher() {

    val testDispatchers = object : DispatchersProvider() {
        override val Main: CoroutineDispatcher = testDispatcher
        override val Default: CoroutineDispatcher = testDispatcher
        override val IO: CoroutineDispatcher = testDispatcher
    }

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
    }

}
