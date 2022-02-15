package n7.ad2.streams.internal.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import n7.ad2.streams.internal.data.remote.retrofit.TwitchApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchGQLApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchHLSApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchInterceptor
import okhttp3.OkHttpClient
import javax.inject.Singleton

@Module
internal class RetrofitTwitchModule {

    @Provides
    @Singleton
    fun provideTwitchApi(baseOkHttpClient: OkHttpClient.Builder, moshi: Moshi): TwitchApi {
        val client = baseOkHttpClient.addInterceptor(TwitchInterceptor()).build()
        return TwitchApi.get(client, moshi)
    }

    @Provides
    @Singleton
    fun provideTwitchGQL(client: OkHttpClient, moshi: Moshi): TwitchGQLApi = TwitchGQLApi.get(client, moshi)

    @Provides
    @Singleton
    fun provideTwitchHLSApi(client: OkHttpClient, moshi: Moshi): TwitchHLSApi = TwitchHLSApi.get(client, moshi)

}