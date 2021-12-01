package n7.ad2.heroes.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.android.Dependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.logger.AD2Logger
import n7.ad2.provider.Provider

interface HeroesDependencies : Dependencies {
    val application: Application
    val provider: Provider
    val logger: AD2Logger
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}