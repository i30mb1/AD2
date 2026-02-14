package n7.ad2.di

import dagger.multibindings.IntoSet
import dagger.multibindings.Multibinds
import n7.ad2.AppInformation
import n7.ad2.app.logger.Logger
import n7.ad2.retrofit.MockInterceptor
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier

@Qualifier
private annotation class InternalApi

@dagger.Module
interface RetrofitModule {

    @InternalApi
    @IntoSet
    @dagger.Binds
    @dagger.Reusable
    fun provideMockInterceptor(mockInterceptor: MockInterceptor): Interceptor

    @InternalApi
    @Multibinds
    fun provideInterceptors(): Set<@JvmSuppressWildcards Interceptor>

    companion object {

        @InternalApi
        @dagger.Provides
        @dagger.Reusable
        fun provideHttpLogger(logger: Logger): HttpLoggingInterceptor {
            val result = HttpLoggingInterceptor(logger::log)
            result.level = HttpLoggingInterceptor.Level.BASIC
            return result
        }

        @dagger.Provides
        @dagger.Reusable
        fun provideBaseOkHttpClientBuilder(
            @InternalApi httpLoggingInterceptor: HttpLoggingInterceptor,
            @InternalApi interceptors: Set<@JvmSuppressWildcards Interceptor>,
            appInformation: AppInformation,
        ): OkHttpClient.Builder {
//        https://www.droidcon.com/2019/08/07/dagger-party-tricks/
            require(Thread.currentThread().name != "main") { "init should not be on main thread" }
            val builder = OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(10, TimeUnit.SECONDS)

            if (appInformation.isDebug) builder.addNetworkInterceptor(httpLoggingInterceptor)
            for (interceptor in interceptors) builder.addInterceptor(interceptor)

            return builder
        }

        @dagger.Provides
        @dagger.Reusable
        fun provideBaseOkHttpClient(baseOkHttpClientBuilder: OkHttpClient.Builder): OkHttpClient = baseOkHttpClientBuilder.build()
    }
}
