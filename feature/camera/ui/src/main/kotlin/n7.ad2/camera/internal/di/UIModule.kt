package n7.ad2.camera.internal.di

import n7.ad2.camera.internal.RecordingDelay
import n7.ad2.camera.internal.RecordingDelayImpl
import n7.ad2.coroutines.DispatchersProvider

@dagger.Module
class UIModule {

    @dagger.Provides
    @dagger.Reusable
    fun provideRecordingDelay(dispatchersProvider: DispatchersProvider): RecordingDelay = RecordingDelayImpl(dispatchersProvider)
}
