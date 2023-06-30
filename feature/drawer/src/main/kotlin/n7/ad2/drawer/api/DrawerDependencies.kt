package n7.ad2.drawer.api

import android.app.Application
import com.squareup.moshi.Moshi
import n7.ad2.Resources
import n7.ad2.apppreference.Preference
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.app.logger.Logger
import n7.ad2.provider.Provider
import okhttp3.OkHttpClient

interface DrawerDependencies : Dependencies {
    val application: Application
    val res: Resources
    val preference: Preference
    val provider: Provider
    val logger: Logger
    val client: OkHttpClient
    val moshi: Moshi
    val dispatchersProvider: DispatchersProvider
}