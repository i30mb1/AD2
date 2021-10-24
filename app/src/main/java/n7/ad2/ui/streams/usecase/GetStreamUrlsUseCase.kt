package n7.ad2.ui.streams.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.data.source.remote.model.Quality
import n7.ad2.data.source.remote.model.StreamGQLRequest
import n7.ad2.data.source.remote.model.Variables
import n7.ad2.data.source.remote.retrofit.TwitchGQLApi
import n7.ad2.data.source.remote.retrofit.TwitchHLSApi
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.UnsupportedEncodingException
import java.net.URLEncoder
import java.util.LinkedHashMap
import java.util.concurrent.TimeUnit
import java.util.regex.Pattern
import javax.inject.Inject

class GetStreamUrlsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val twitchGQLApi: TwitchGQLApi,
    private val twitchHLSApi: TwitchHLSApi,
) {

    val client = OkHttpClient.Builder()
        .readTimeout(5, TimeUnit.SECONDS)
        .connectTimeout(3, TimeUnit.SECONDS)
        .build()

    operator fun invoke(streamerName: String) = flow<String> {
        val requestObject = StreamGQLRequest(variables = Variables(streamerName = streamerName))

        val body2 = twitchGQLApi.getStreamGQL(requestObject)
        val token = safeEncode(body2.data.streamPlaybackAccessToken.value)
        val signature = body2.data.streamPlaybackAccessToken.signature
        val p = java.util.Random().nextInt(6)
        val streamURL = "http://usher.twitch.tv/api/channel/hls/$streamerName.m3u8?player=twitchweb&&token=$token&sig=$signature&allow_audio_only=true&allow_source=true&type=any&p=1"
        val resultUrl = parseM3U8(streamURL)["auto"]
        parseM3(streamerName, p, token, signature, streamURL)
        emit(resultUrl!!.url)
    }.flowOn(ioDispatcher)

    suspend fun parseM3(streamerName: String, p: Int, token: String, signature: String, streamURL: String) {
        val result = twitchHLSApi.getUrls(
            streamerName = streamerName,
            p = 2,
            sig = signature,
            token = token,
        )
        result.isSuccessful
    }


    fun parseM3U8(urlToRead: String): LinkedHashMap<String, Quality> {
        val request = Request.Builder()
            .url(urlToRead)
            .header("Referer", "https://player.twitch.tv")
            .header("Origin", "https://player.twitch.tv")
            .build()
        val response: Response = client.newCall(request).execute()
        val result = response.body.toString()
        val resultList: LinkedHashMap<String, Quality> = LinkedHashMap<String, Quality>()
        resultList["auto"] = Quality("Auto", urlToRead)
        val p = Pattern.compile("GROUP-ID=\"(.+)\",NAME=\"(.+)\".+\\n.+\\n(https?://\\S+)")
        val m = p.matcher(result)
        while (m.find()) {
            resultList[m.group(1)] = Quality(m.group(2), m.group(3))
        }
        return resultList
    }

    fun safeEncode(s: String): String {
        return try {
            URLEncoder.encode(s, "utf-8")
        } catch (ignore: UnsupportedEncodingException) {
            s
        }
    }

    class SimpleResponse(response: Response) {
        var code: Int
        var body: String? = null
        var response: Response

        init {
            assert(response.body != null)
            code = response.code
            this.response = response
            try {
                body = response.body!!.string()
            } catch (ignored: IOException) {
            }
        }
    }


}