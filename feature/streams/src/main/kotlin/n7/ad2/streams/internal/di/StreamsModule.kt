package n7.ad2.streams.internal.di

import dagger.Lazy
import javax.inject.Qualifier
import n7.ad2.streams.internal.data.remote.retrofit.TwitchApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchGQLApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchHLSApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchInterceptor
import okhttp3.OkHttpClient

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
    fun provideTwitchApi(@Internal client: Lazy<OkHttpClient>): TwitchApi {
        return TwitchApi.get(client)
    }

    @dagger.Provides
    @dagger.Reusable
    fun provideTwitchGQL(client: Lazy<OkHttpClient>): TwitchGQLApi {
        return TwitchGQLApi.get(client)
    }

    @dagger.Provides
    @dagger.Reusable
    fun provideTwitchHLSApi(client: Lazy<OkHttpClient>): TwitchHLSApi {
        return TwitchHLSApi.get(client)
    }

}