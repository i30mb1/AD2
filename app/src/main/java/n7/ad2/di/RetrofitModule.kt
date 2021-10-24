package n7.ad2.di

import com.squareup.moshi.Moshi
import dagger.Module
import dagger.Provides
import n7.ad2.BuildConfig
import n7.ad2.data.source.remote.retrofit.TwitchApi
import n7.ad2.data.source.remote.retrofit.TwitchGQLApi
import n7.ad2.data.source.remote.retrofit.TwitchHLSApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object RetrofitModule {

    private const val CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5"
    private const val ACCESS_TOKEN = "6qla87p9en5fcye3aucbb04xrwx4z3"

    @Provides
    @Singleton
    fun provideHttpLoggingInterceptor(): HttpLoggingInterceptor = HttpLoggingInterceptor(object : HttpLoggingInterceptor.Logger {
        override fun log(message: String) {

        }
    }).apply {
        level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BASIC else HttpLoggingInterceptor.Level.NONE
    }

    @Provides
    @Singleton
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient = OkHttpClient.Builder()
        .connectTimeout(10, TimeUnit.SECONDS)
        .writeTimeout(10, TimeUnit.SECONDS)
        .readTimeout(10, TimeUnit.SECONDS)
        .addInterceptor { chain ->
            val request = chain.request().newBuilder()
                .addHeader("client-id", CLIENT_ID)
                .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
                .build()
            return@addInterceptor chain.proceed(request)
        }
        .addInterceptor(httpLoggingInterceptor)
        .build()

    @Provides
    @Singleton
    fun provideTwitchApi(client: OkHttpClient, moshi: Moshi): TwitchApi = TwitchApi.get(client, moshi)

    @Provides
    @Singleton
    fun provideTwitchGQL(client: OkHttpClient, moshi: Moshi): TwitchGQLApi = TwitchGQLApi.get(client, moshi)

    @Provides
    @Singleton
    fun provideTwitchHLSApi(client: OkHttpClient, moshi: Moshi): TwitchHLSApi = TwitchHLSApi.get(client, moshi)

}