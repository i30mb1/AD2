package n7.ad2.di

import dagger.Module
import dagger.Provides
import n7.ad2.BuildConfig
import n7.ad2.logger.AD2Logger
import n7.ad2.streams.internal.data.remote.retrofit.TwitchInterceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
object RetrofitModule {

    @Provides
    @Singleton
    fun provideHttpLogger(logger: AD2Logger) = HttpLoggingInterceptor(logger::log).apply { level = HttpLoggingInterceptor.Level.BASIC }

    @Provides
    @Singleton
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
    @Singleton
    fun provideBaseOkHttpClient(baseOkHttpClientBuilder: OkHttpClient.Builder): OkHttpClient = baseOkHttpClientBuilder.build()

}