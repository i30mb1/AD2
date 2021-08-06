package n7.ad2.data.source.remote.retrofit

import n7.ad2.data.source.remote.model.Streams
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

}