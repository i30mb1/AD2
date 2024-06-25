package n7.ad2.streams.api

import android.app.Application
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import okhttp3.OkHttpClient

interface StreamsDependencies : Dependencies {
    val application: Application
    val logger: Logger
    val okHttpClient: OkHttpClient
    val clientBuilder: OkHttpClient.Builder
    val dispatchersProvider: DispatchersProvider
}
