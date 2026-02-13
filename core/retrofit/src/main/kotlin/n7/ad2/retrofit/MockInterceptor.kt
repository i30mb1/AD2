package n7.ad2.retrofit

import n7.ad2.AppSettings
import n7.ad2.app.logger.Logger
import okhttp3.Interceptor
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Protocol
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody.Companion.toResponseBody
import javax.inject.Inject

class MockInterceptor @Inject constructor(private val appSettings: AppSettings, private val logger: Logger) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        if (!appSettings.isMockInterceptorEnabled) return chain.proceed(request)
        val uri = chain.request().url.toUri().toString()
        val responseString = when {
            uri.endsWith("") -> ""
            else -> error("$uri is not mocked")
        }
        logger.log("$uri mocked")
        return request.buildResponse(responseString)
    }
}

private fun Request.buildFileResponse(filePath: String): Response {
    val json = load(filePath)
    return buildResponse(json)
}

private fun Request.buildResponse(message: String) = Response.Builder()
    .body(message.toResponseBody("application/json".toMediaTypeOrNull()))
    .addHeader("content-type", "application/json")
    .request(this)
    .protocol(Protocol.HTTP_2)
    .code(200)
    .message("Ok")
    .build()

private fun load(_filePath: String): String {
    return "{}" // Mock empty JSON response
}
