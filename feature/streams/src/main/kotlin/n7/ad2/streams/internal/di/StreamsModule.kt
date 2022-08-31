package n7.ad2.streams.internal.di

import com.squareup.moshi.Moshi
import dagger.Lazy
import dagger.Module
import dagger.Provides
import n7.ad2.streams.internal.data.remote.retrofit.TwitchApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchGQLApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchHLSApi
import n7.ad2.streams.internal.data.remote.retrofit.TwitchInterceptor
import okhttp3.OkHttpClient
import javax.inject.Qualifier
import javax.inject.Singleton

@Qualifier
private annotation class InternalOkHttpClient

@Module
internal class StreamsModule {

    @InternalOkHttpClient
    @Provides
    fun provideOkHttpClient(okHttpClientBuilder: OkHttpClient.Builder): OkHttpClient {
        return okHttpClientBuilder.addInterceptor(TwitchInterceptor()).build()
    }

    @Provides
    @Singleton
    fun provideTwitchApi(@InternalOkHttpClient client: Lazy<OkHttpClient>, moshi: Moshi): TwitchApi {
        return TwitchApi.get(client, moshi)
    }

    @Provides
    @Singleton
    fun provideTwitchGQL(client: Lazy<OkHttpClient>, moshi: Moshi): TwitchGQLApi {
        return TwitchGQLApi.get(client, moshi)
    }

    @Provides
    @Singleton
    fun provideTwitchHLSApi(client: Lazy<OkHttpClient>, moshi: Moshi): TwitchHLSApi {
        return TwitchHLSApi.get(client, moshi)
    }

}