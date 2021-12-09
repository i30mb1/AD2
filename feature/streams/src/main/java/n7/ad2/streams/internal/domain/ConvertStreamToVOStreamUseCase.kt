package n7.ad2.streams.internal.domain

import n7.ad2.ktx.dpToPx
import n7.ad2.streams.internal.data.remote.Stream
import n7.ad2.streams.internal.domain.vo.VOStream
import javax.inject.Inject

internal class ConvertStreamToVOStreamUseCase @Inject constructor() {

    private val width = 160.dpToPx
    private val height = 90.dpToPx

    operator fun invoke(stream: Stream): VOStream {
        val title = stream.title
        val streamerName = stream.userLogin
        val streamImage = stream.thumbnailUrl.replace("{width}", "$width").replace("{height}", "$height")
        return VOStream.Simple(
            title,
            streamerName,
            streamImage
        )
    }

}