package n7.ad2.retrofit

import n7.ad2.AppSettings
import n7.ad2.logger.Logger
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class MockInterceptor @Inject constructor(
    private val appSettings: AppSettings,
    private val logger: Logger,
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        if (!appSettings.isMockInterceptorEnabled) return chain.proceed(chain.request())
        val uri = chain.request().url.toUri().toString()
        val responseString = when {
            uri.endsWith("") -> ""
            else -> error("$uri is not mocked")
        }
        logger.log("$uri mocked")
        return chain.proceed(chain.request())
            .newBuilder()
            .code(200)
            .protocol(Protocol.HTTP_2)
            .message(responseString)
            .body(responseString.toByteArray().toResponseBody("application/json".toMediaTypeOrNull()))
            .addHeader("content-type", "application/json")
            .build()
    }

}