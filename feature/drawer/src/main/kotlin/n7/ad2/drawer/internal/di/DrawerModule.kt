package n7.ad2.drawer.internal.di

import dagger.Lazy
import n7.ad2.drawer.internal.data.remote.SettingsApi
import okhttp3.OkHttpClient

@dagger.Module
internal object DrawerModule {

    @dagger.Provides
    fun provideSettingsApi(client: Lazy<OkHttpClient>): SettingsApi {
        return SettingsApi.get(client)
    }

}