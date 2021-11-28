package n7.ad2.ui.streams.usecase

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.data.source.remote.model.StreamGQLRequest
import n7.ad2.data.source.remote.model.StreamsQuality
import n7.ad2.data.source.remote.model.Variables
import n7.ad2.data.source.remote.retrofit.TwitchGQLApi
import n7.ad2.data.source.remote.retrofit.TwitchHLSApi
import java.net.URLEncoder
import java.util.regex.Pattern
import javax.inject.Inject

class GetStreamUrlsUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val twitchGQLApi: TwitchGQLApi,
    private val twitchHLSApi: TwitchHLSApi,
) {

    operator fun invoke(streamerName: String) = flow {
        val requestObject = StreamGQLRequest(variables = Variables(streamerName = streamerName))

        val response = twitchGQLApi.getStreamGQL(requestObject)
        val token = safeEncode(response.data.streamPlaybackAccessToken.value)
        val signature = response.data.streamPlaybackAccessToken.signature
        val p = java.util.Random().nextInt(6)
        val streamURL = "http://usher.twitch.tv/api/channel/hls/$streamerName.m3u8?player=twitchweb&token=$token&sig=$signature&allow_audio_only=true&allow_source=true&type=any&p=$p"
        val result = parseM3(streamerName, p, token, signature, streamURL)
        emit(streamURL)
    }.flowOn(dispatchers.Default)

    private suspend fun parseM3(streamerName: String, p: Int, token: String, signature: String, streamURL: String): List<StreamsQuality> {
        val response = twitchHLSApi.getUrls(streamerName = streamerName, p = p, sig = signature, token = token)
        val body = response.body().toString()
        val result = mutableListOf<StreamsQuality>()
        result.add(StreamsQuality("Auto", "Auto", streamURL))
        val pattern = Pattern.compile("GROUP-ID=\"(.+)\",NAME=\"(.+)\".+\\n.+\\n(https?://\\S+)")
        val matcher = pattern.matcher(body)
        while (matcher.find()) result.add(StreamsQuality(matcher.group(1)!!, matcher.group(2)!!, matcher.group(3)!!))
        return result
    }

    private fun safeEncode(s: String): String = URLEncoder.encode(s, "utf-8")

}