package n7.ad2.data.source.remote.retrofit

import n7.ad2.data.source.remote.model.Stream
import n7.ad2.data.source.remote.model.Streams
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitchApi {

    companion object {
        private const val CLIENT_ID = "vmr0piicf3e3nxw4fs0zz2e2vqak8y"
    }

    @Headers("Accept: application/vnd.twitchtv.v5+json", "Client-ID: $CLIENT_ID")
    @GET("streams?game=Dota%202&stream_type=live&client_id=$CLIENT_ID")
    suspend fun getStreams(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
    ): Streams

    @Headers("Accept: application/vnd.twitchtv.v5+json", "Client-ID: $CLIENT_ID")
    @GET("streams/{name}?client_id=$CLIENT_ID")
    suspend fun getStream(
        @Path("name") name: String,
    ): Stream
}