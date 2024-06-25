package n7.ad2.streams.internal.data.remote.retrofit

import dagger.Lazy
import kotlinx.serialization.json.Json
import n7.ad2.streams.internal.data.remote.Streams
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.kotlinx.serialization.asConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

internal interface TwitchApi {

    @GET("streams?game_id=29595")
    suspend fun getStreams(
        @Query("first") first: Int = 100,
        @Query("after") after: String = "",
        @Query("before") before: String = "",
    ): Streams

    @GET("streams?game_id=29595")
    suspend fun getStream(
        @Path("user_id") userId: String,
    ): Streams

    companion object {

        fun get(
            client: Lazy<OkHttpClient>,
        ): TwitchApi {
            return Retrofit.Builder()
                .baseUrl("https://api.twitch.tv/helix/")
                .callFactory { request -> client.get().newCall(request) }
                .addConverterFactory(Json.asConverterFactory("application/json".toMediaType()))
                .build()
                .create()
        }

    }

}