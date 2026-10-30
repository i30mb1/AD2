package n7.ad2.items.domain.model

/**
 * One post of the Dota 2 news feed.
 * [date] is unix seconds, [isOfficial] marks Valve's own announcements, everything else is press.
 */
class News(
    val title: String,
    val urlImage: String,
    val loadedFromPage: Int,
    val articleUrl: String,
    val date: Long,
    val source: String,
    val author: String,
    val isOfficial: Boolean,
)
