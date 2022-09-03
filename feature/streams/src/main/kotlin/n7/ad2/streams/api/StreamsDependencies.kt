package n7.ad2.streams.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.Logger
import okhttp3.OkHttpClient

interface StreamsDependencies : Dependencies {
    val application: Application
    val logger: Logger
    val client: OkHttpClient
    val clientBuilder: OkHttpClient.Builder
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}