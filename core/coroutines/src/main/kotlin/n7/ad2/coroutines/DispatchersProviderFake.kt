package n7.ad2.coroutines

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.test.UnconfinedTestDispatcher

class DispatchersProviderFake : DispatchersProvider() {
    override val Main: CoroutineDispatcher = UnconfinedTestDispatcher()
    override val Default: CoroutineDispatcher = UnconfinedTestDispatcher()
    override val IO: CoroutineDispatcher = UnconfinedTestDispatcher()
}