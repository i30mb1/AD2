package n7.ad2.streams.internal.domain

import n7.ad2.streams.internal.data.remote.Stream
import n7.ad2.streams.internal.domain.vo.VOStream
import java.time.Instant
import java.time.format.DateTimeParseException
import javax.inject.Inject

private const val THUMBNAIL_WIDTH = 640
private const val THUMBNAIL_HEIGHT = 360

internal class ConvertStreamToVOStreamUseCase @Inject constructor() {

    operator fun invoke(stream: Stream): VOStream {
        // the first card is as wide as the screen, one size for every card lets Coil reuse the cache
        val streamImage = stream.thumbnailUrl.replace("{width}", "$THUMBNAIL_WIDTH").replace("{height}", "$THUMBNAIL_HEIGHT")
        return VOStream.Simple(
            title = stream.title,
            streamerName = stream.userLogin,
            imageUrl = streamImage,
            viewers = stream.viewerCount,
            displayName = stream.userName.ifEmpty { stream.userLogin },
            language = stream.language,
            startedAtMillis = parseMillis(stream.startedAt),
        )
    }

    private fun parseMillis(isoDate: String): Long = try {
        Instant.parse(isoDate).toEpochMilli()
    } catch (e: DateTimeParseException) {
        0
    }
}
