package n7.ad2.di

import dagger.Module
import dagger.Provides
import n7.ad2.AppInformation
import n7.ad2.dagger.ApplicationScope
import n7.ad2.logger.AD2Logger
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

@Qualifier
private annotation class InternalApi

@Module
object RetrofitModule {

    @InternalApi
    @Provides
    @ApplicationScope
    fun provideHttpLogger(logger: AD2Logger): HttpLoggingInterceptor {
        val result = HttpLoggingInterceptor(logger::log)
        result.level = HttpLoggingInterceptor.Level.BASIC
        return result
    }

    @Provides
    @ApplicationScope
    fun provideBaseOkHttpClientBuilder(
        @InternalApi httpLoggingInterceptor: HttpLoggingInterceptor,
        appInformation: AppInformation,
    ): OkHttpClient.Builder {
//        https://www.droidcon.com/2019/08/07/dagger-party-tricks/
        require(Thread.currentThread().name != "main") { "init should not be on main thread" }
        val builder = OkHttpClient.Builder()
            .connectTimeout(10, TimeUnit.SECONDS)
            .writeTimeout(10, TimeUnit.SECONDS)
            .readTimeout(10, TimeUnit.SECONDS)

        if (appInformation.isDebug) builder.addNetworkInterceptor(httpLoggingInterceptor)

        return builder
    }

    @Provides
    @ApplicationScope
    fun provideBaseOkHttpClient(baseOkHttpClientBuilder: OkHttpClient.Builder): OkHttpClient {
        return baseOkHttpClientBuilder.build()
    }

}