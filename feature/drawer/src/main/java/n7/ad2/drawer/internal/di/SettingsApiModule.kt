package n7.ad2.drawer.internal.di

import com.squareup.moshi.Moshi
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.adapter.StringVOMenuTypeAdapter
import okhttp3.OkHttpClient

@dagger.Module
class SettingsApiModule {

    @dagger.Provides
    fun provideSettingsApi(client: OkHttpClient, moshiBase: Moshi): SettingsApi {
        val moshi = moshiBase.newBuilder().add(StringVOMenuTypeAdapter()).build()
        return SettingsApi.get(client, moshi)
    }

}