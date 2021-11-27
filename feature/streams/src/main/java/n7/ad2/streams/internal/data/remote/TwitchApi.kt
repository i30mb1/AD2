package n7.ad2.streams.internal.data.remote

import com.squareup.moshi.Moshi
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitchApi {

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
            client: OkHttpClient,
            moshi: Moshi,
        ): TwitchApi {
            return Retrofit.Builder()
                .baseUrl("https://api.twitch.tv/helix/")
                .client(client)
                .addConverterFactory(MoshiConverterFactory.create(moshi))
                .build()
                .create()
        }

    }

}