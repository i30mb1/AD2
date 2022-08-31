package n7.ad2.drawer.internal.di

import com.squareup.moshi.Moshi
import dagger.Lazy
import n7.ad2.drawer.internal.data.remote.SettingsApi
import n7.ad2.drawer.internal.data.remote.adapter.StringVOMenuTypeAdapter
import okhttp3.OkHttpClient

@dagger.Module
internal class DrawerModule {

    @dagger.Provides
    fun provideSettingsApi(client: Lazy<OkHttpClient>, moshiBase: Moshi): SettingsApi {
        val moshi = moshiBase.newBuilder().add(StringVOMenuTypeAdapter()).build()
        return SettingsApi.get(client, moshi)
    }

}