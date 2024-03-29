package n7.ad2.streams.internal.di

import com.squareup.moshi.Moshi
import dagger.Lazy
import n7.ad2.streams.internal.data.remote.retrofit.TwitchApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchGQLApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchHLSApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchInterceptor
import okhttp3.OkHttpClient
import javax.inject.Qualifier

@Qualifier
private annotation class Internal

@dagger.Module
internal object StreamsModule {

    @Internal
    @dagger.Provides
    @dagger.Reusable
    fun provideOkHttpClient(okHttpClientBuilder: OkHttpClient.Builder): OkHttpClient {
        return okHttpClientBuilder.addInterceptor(TwitchInterceptor()).build()
    }

    @dagger.Provides
    @dagger.Reusable
    fun provideTwitchApi(@Internal client: Lazy<OkHttpClient>, moshi: Moshi): TwitchApi {
        return TwitchApi.get(client, moshi)
    }

    @dagger.Provides
    @dagger.Reusable
    fun provideTwitchGQL(client: Lazy<OkHttpClient>, moshi: Moshi): TwitchGQLApi {
        return TwitchGQLApi.get(client, moshi)
    }

    @dagger.Provides
    @dagger.Reusable
    fun provideTwitchHLSApi(client: Lazy<OkHttpClient>, moshi: Moshi): TwitchHLSApi {
        return TwitchHLSApi.get(client, moshi)
    }

}