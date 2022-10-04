package ad2.n7.news.internal.domain.model

data class NewsVO(
    val id: Int,
    val title: String,
    val image: Image,
)

@JvmInline
value class Image(val origin: String) {
    val icon: String
        get() = "$origin:1"
}