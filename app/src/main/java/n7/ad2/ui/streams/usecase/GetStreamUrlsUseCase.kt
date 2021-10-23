package n7.ad2.ui.streams.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import n7.ad2.data.source.remote.model.StreamGQLRequest
import n7.ad2.data.source.remote.model.Variables
import n7.ad2.data.source.remote.retrofit.TwitchGQLApi
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.Request
import okhttp3.RequestBody
import javax.inject.Inject

class GetStreamUrlsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val twitchGQLApi: TwitchGQLApi,
) {

    operator fun invoke(streamerName: String) = flow<Unit> {
        val request2 = StreamGQLRequest(variables = Variables(streamerName = streamerName))
        val adapter = Moshi.Builder().build().adapter(StreamGQLRequest::class.java)
        val json = adapter.toJson(request2)
        val request = Request.Builder()
            .url("https://gql.twitch.tv/gql")
            .header("Client-ID", "kimne78kx3ncx6brgo4mv6wki5h1ko")
            .post(RequestBody.create("application/json".toMediaType(), json))
            .build()

        twitchGQLApi.getStreamGQL(request2)
        val isasfd = json == request.body.toString()
//       val result = twitchGQLApi.getStreamGQL(request)
//        result.toString()
    }


}