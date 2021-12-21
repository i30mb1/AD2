package n7.ad2.drawer.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.app_preference.AppPreference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.AD2Logger
import n7.ad2.provider.Provider
import okhttp3.OkHttpClient

interface DrawerDependencies : Dependencies {
    val application: Application
    val appPreference: AppPreference
    val provider: Provider
    val logger: AD2Logger
    val client: OkHttpClient
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}