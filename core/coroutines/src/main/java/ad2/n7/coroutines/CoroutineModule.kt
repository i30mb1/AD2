package ad2.n7.coroutines

import dagger.Module
import dagger.Provides
import dagger.Reusable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

@Module
class CoroutineModule {

    @Reusable
    @Provides
    fun provideDispatchers(): DispatchersProvider = DispatchersProvider()

    @Provides
    fun provideCoroutineScope(): CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

}