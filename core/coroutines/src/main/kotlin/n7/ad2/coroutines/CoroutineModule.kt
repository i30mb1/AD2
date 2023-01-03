package n7.ad2.coroutines

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@dagger.Module
object CoroutineModule {

    @dagger.Reusable
    @dagger.Provides
    fun provideDispatchers(): DispatchersProvider = DispatchersProvider()

    @dagger.Provides
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

}