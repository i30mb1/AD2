package n7.ad2.drawer.internal.di

import com.squareup.moshi.Moshi
import n7.ad2.drawer.internal.data.remote.SettingsApi
import okhttp3.OkHttpClient

@dagger.Module
class SettingsApiModule {

    @dagger.Provides
    fun provideSettingsApi(client: OkHttpClient, moshi: Moshi) = SettingsApi.get(client, moshi)

}