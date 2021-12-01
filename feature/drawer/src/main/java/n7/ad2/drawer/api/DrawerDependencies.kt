package n7.ad2.drawer.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.android.Dependencies
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.logger.AD2Logger
import okhttp3.OkHttpClient

interface DrawerDependencies : Dependencies {
    val application: Application
    val logger: AD2Logger
    val client: OkHttpClient
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}