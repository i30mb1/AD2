package n7.ad2.streams.internal.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import n7.ad2.streams.internal.data.remote.TwitchApi
import okhttp3.OkHttpClient

@Module
internal class RetrofitModule {

    @Provides
    fun provideTwitchApi(client: OkHttpClient, moshi: Moshi): TwitchApi = TwitchApi.get(client, moshi)

}