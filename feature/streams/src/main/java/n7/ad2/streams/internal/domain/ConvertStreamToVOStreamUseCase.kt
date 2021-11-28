package n7.ad2.streams.internal.domain

import n7.ad2.android.extension.toPx
import n7.ad2.streams.internal.data.remote.Stream
import n7.ad2.streams.internal.domain.vo.VOStream
import javax.inject.Inject

internal class ConvertStreamToVOStreamUseCase @Inject constructor() {

    private val width = 160.toPx
    private val height = 90.toPx

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