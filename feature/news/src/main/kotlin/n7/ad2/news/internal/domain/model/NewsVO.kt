package n7.ad2.news.internal.domain.model

internal data class NewsVO(
    val id: Int,
    val title: String,
    val image: Image,
)

@JvmInline
internal value class Image(val origin: String) {
    val icon: String
        get() = "$origin:1"
}