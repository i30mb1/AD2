package n7.ad2.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestDispatcher
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.rules.TestWatcher
import org.junit.runner.Description

/**
 * This class is a unit test rule which watches for tests starting and finishing.
 * It contains a reference to a TestCoroutineDispatcher, and as tests are starting and stopping it overrides the default Dispatchers.Main
 * dispatcher and replaces the default with our test dispatcher.
 * @see: <a href="https://github.com/Kotlin/kotlinx.coroutines/blob/master/kotlinx-coroutines-test/MIGRATION.md">more info here</a>
 */
@ExperimentalCoroutinesApi
class CoroutineTestRule(
    private val testDispatcher: TestDispatcher = UnconfinedTestDispatcher(),
) : TestWatcher() {
    val testScope = TestScope(testDispatcher)
    val dispatchers = object : DispatchersProvider() {
        override val Main: CoroutineDispatcher = UnconfinedTestDispatcher(testDispatcher.scheduler)
        override val Default: CoroutineDispatcher = UnconfinedTestDispatcher(testDispatcher.scheduler)
        override val IO: CoroutineDispatcher = UnconfinedTestDispatcher(testDispatcher.scheduler)
    }

    override fun starting(description: Description) {
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description) {
        Dispatchers.resetMain()
    }

}