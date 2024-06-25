package n7.ad2.streams.internal.data.remote.retrofit

import dagger.Lazy
import kotlinx.serialization.json.Json
import n7.ad2.streams.internal.data.remote.model.StreamGQLData
import n7.ad2.streams.internal.data.remote.model.StreamGQLRequest
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface TwitchGQLApi {

    @Headers("Client-ID: kimne78kx3ncx6brgo4mv6wki5h1ko")
    @POST("gql/")
    suspend fun getStreamGQL(
        @Body body: StreamGQLRequest,
    ): StreamGQLData

    companion object {

        fun get(
            client: Lazy<OkHttpClient>,
        ): TwitchGQLApi {
            return Retrofit.Builder()
                .baseUrl("https://gql.twitch.tv/")
                .callFactory { request -> client.get().newCall(request) }
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create()
        }

    }

}