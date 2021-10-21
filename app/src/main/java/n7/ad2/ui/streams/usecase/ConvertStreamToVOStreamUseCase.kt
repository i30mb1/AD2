package n7.ad2.ui.streams.usecase

import n7.ad2.data.source.remote.model.Stream
import n7.ad2.ui.streams.domain.vo.VOSimpleStream
import n7.ad2.ui.streams.domain.vo.VOStream
import n7.ad2.utils.extension.toPx
import javax.inject.Inject

class ConvertStreamToVOStreamUseCase @Inject constructor(

) {

    private val width = 160.toPx
    private val height = 90.toPx

    operator fun invoke(stream: Stream): VOStream {
        val title = stream.title
        val streamImage = stream.thumbnailUrl.replace("{width}", "$width").replace("{height}", "$height")
        return VOSimpleStream(
            title,
            streamImage
        )
    }

}