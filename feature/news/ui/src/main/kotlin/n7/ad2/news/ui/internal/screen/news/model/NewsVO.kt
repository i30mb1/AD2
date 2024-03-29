package n7.ad2.news.ui.internal.screen.news.model

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
