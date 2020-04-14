package n7.ad2

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.*
import org.junit.rules.TestWatcher
import org.junit.runner.Description

@ExperimentalCoroutinesApi
fun CoroutineTestRule.runBlockingTest(block: suspend TestCoroutineScope.() -> Unit) {
    testDispatcher.runBlockingTest(block)
}

/**
 * This class is a unit test rule which watches for tests starting and finishing.
 * It contains a reference to a TestCoroutineDispatcher, and as tests are starting and stopping it overrides the default Dispatchers.Main
 * dispatcher and replaces the default with our test dispatcher.
 */
@ExperimentalCoroutinesApi
class CoroutineTestRule(
    val testDispatcher: TestCoroutineDispatcher = TestCoroutineDispatcher()
) : TestWatcher() {

    override fun starting(description: Description?) {
        super.starting(description)
        Dispatchers.setMain(testDispatcher)
    }

    override fun finished(description: Description?) {
        super.finished(description)
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
    }
}
