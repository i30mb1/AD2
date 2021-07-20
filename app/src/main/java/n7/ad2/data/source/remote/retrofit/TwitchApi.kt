package n7.ad2.data.source.remote.retrofit

import n7.ad2.data.source.remote.model.Streams
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Path
import retrofit2.http.Query

interface TwitchApi {

    companion object {
        private const val CLIENT_ID = "gp762nuuoqcoxypju8c569th9wz7q5"
        private const val ACCESS_TOKEN = "6qla87p9en5fcye3aucbb04xrwx4z3"
    }

    @Headers("client-id: $CLIENT_ID", "Authorization: Bearer $ACCESS_TOKEN")
    @GET("streams?game_id=29595")
    suspend fun getStreams(
        @Query("first") first: Int = 100,
        @Query("after") after: String = "",
        @Query("before") before: String = "",
    ): Streams

    @Headers("client-id: $CLIENT_ID", "Authorization: Bearer $ACCESS_TOKEN")
    @GET("streams?game_id=29595")
    suspend fun getStream(
        @Path("user_id") userId: String,
    ): Streams
}