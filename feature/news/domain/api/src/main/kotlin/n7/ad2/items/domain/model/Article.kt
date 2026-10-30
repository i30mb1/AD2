package n7.ad2.items.domain.model

/** Article body split into blocks, so the screen draws it natively instead of through a WebView. */
class Article(val blocks: List<ArticleBlock>)

sealed interface ArticleBlock {
    data class Heading(val text: List<ArticleText>) : ArticleBlock
    data class Paragraph(val text: List<ArticleText>) : ArticleBlock
    data class ListItem(val text: List<ArticleText>) : ArticleBlock
    data class Quote(val text: List<ArticleText>) : ArticleBlock
    data class Image(val url: String) : ArticleBlock
}

/** A piece of text with one style, [link] is set for the text of a link. */
data class ArticleText(val text: String, val isBold: Boolean = false, val isItalic: Boolean = false, val link: String? = null)
