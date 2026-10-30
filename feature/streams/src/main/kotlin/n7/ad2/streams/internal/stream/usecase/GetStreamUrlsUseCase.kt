package n7.ad2.streams.internal.stream.usecase

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.streams.internal.data.remote.model.StreamGQLRequest
import n7.ad2.streams.internal.data.remote.model.Variables
import n7.ad2.streams.internal.data.remote.retrofit.TwitchGQLApi
import java.net.URLEncoder
import javax.inject.Inject

class GetStreamUrlsUseCase @Inject constructor(private val dispatchers: DispatchersProvider, private val twitchGQLApi: TwitchGQLApi) {

    operator fun invoke(streamerName: String) = flow {
        val requestObject = StreamGQLRequest(variables = Variables(streamerName = streamerName))

        val response = twitchGQLApi.getStreamGQL(requestObject)
        val token = safeEncode(response.data.streamPlaybackAccessToken.value)
        val signature = response.data.streamPlaybackAccessToken.signature
        val p = java.util.Random().nextInt(1_000_000)
        emit("https://usher.ttvnw.net/api/channel/hls/$streamerName.m3u8?player=twitchweb&token=$token&sig=$signature&allow_audio_only=true&allow_source=true&type=any&p=$p")
    }.flowOn(dispatchers.Default)

    private fun safeEncode(s: String): String = URLEncoder.encode(s, "utf-8")
}
