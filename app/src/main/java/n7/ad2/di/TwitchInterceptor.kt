package n7.ad2.di

import okhttp3.Interceptor
import okhttp3.Response

class TwitchInterceptor : Interceptor {

    companion object {
        private const val CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5"
        private const val ACCESS_TOKEN = "6qla87p9en5fcye3aucbb04xrwx4z3"
    }

    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("client-id", CLIENT_ID)
            .addHeader("Authorization", "Bearer $ACCESS_TOKEN")
            .build()
        return chain.proceed(request)
    }

}