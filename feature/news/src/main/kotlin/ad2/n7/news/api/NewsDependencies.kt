package ad2.n7.news.api

import android.app.Application
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.AD2Logger
import n7.ad2.provider.Provider

interface NewsDependencies : Dependencies {
    val application: Application
    val logger: AD2Logger
    val provider: Provider
    val dispatchersProvider: DispatchersProvider
}