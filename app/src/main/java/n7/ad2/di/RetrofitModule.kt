package n7.ad2.di

import dagger.Module
import dagger.Provides
import n7.ad2.BuildConfig
import n7.ad2.dagger.ApplicationScope
import n7.ad2.logger.AD2Logger
import n7.ad2.streams.internal.data.remote.retrofit.TwitchInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit

@Module
object RetrofitModule {

    @Provides
    @ApplicationScope
    fun provideHttpLogger(logger: AD2Logger) = HttpLoggingInterceptor(logger::log).apply { level = HttpLoggingInterceptor.Level.BASIC }

    @Provides
    @ApplicationScope
    fun provideBaseOkHttpClientBuilder(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient.Builder {
        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)
            .addInterceptor(TwitchInterceptor())

        if (BuildConfig.DEBUG) builder.addNetworkInterceptor(httpLoggingInterceptor)

        return builder
    }

    @Provides
    @ApplicationScope
    fun provideBaseOkHttpClient(baseOkHttpClientBuilder: OkHttpClient.Builder): OkHttpClient = baseOkHttpClientBuilder.build()

}