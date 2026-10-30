package n7.ad2.items.domain.internal.usecase

import n7.ad2.items.domain.model.ArticleBlock
import n7.ad2.items.domain.model.ArticleText
import org.junit.Assert.assertEquals
import org.junit.Test

class ArticleHtmlParserTest {

    @Test
    fun `paragraphs keep bold text and links`() {
        val blocks = ArticleHtmlParser.parse("""<p>Patch is out, notes <a href="https://www.dota2.com/patches/7.41f">here</a>. <b>Enjoy</b></p>""")
        assertEquals(
            listOf(
                ArticleBlock.Paragraph(
                    listOf(
                        ArticleText("Patch is out, notes "),
                        ArticleText("here", link = "https://www.dota2.com/patches/7.41f"),
                        ArticleText(". "),
                        ArticleText("Enjoy", isBold = true),
                    ),
                ),
            ),
            blocks,
        )
    }

    @Test
    fun `list items become separate blocks after the text before the list`() {
        val blocks = ArticleHtmlParser.parse("In addition:<br><ul><li>Fixed Bane</li><li>Fixed <i>Lina</i></li></ul>")
        assertEquals(
            listOf(
                ArticleBlock.Paragraph(listOf(ArticleText("In addition:"))),
                ArticleBlock.ListItem(listOf(ArticleText("Fixed Bane"))),
                ArticleBlock.ListItem(listOf(ArticleText("Fixed "), ArticleText("Lina", isItalic = true))),
            ),
            blocks,
        )
    }

    @Test
    fun `nested list goes after its parent item`() {
        val blocks = ArticleHtmlParser.parse("<ul><li>Heroes<ul><li>Axe</li></ul></li></ul>")
        assertEquals(
            listOf(
                ArticleBlock.ListItem(listOf(ArticleText("Heroes"))),
                ArticleBlock.ListItem(listOf(ArticleText("Axe"))),
            ),
            blocks,
        )
    }

    @Test
    fun `images split the text and iframes are dropped`() {
        val blocks = ArticleHtmlParser.parse("""Before<img src="https://cdn/a.png"/>After<iframe src="https://youtube"></iframe>""")
        assertEquals(
            listOf(
                ArticleBlock.Paragraph(listOf(ArticleText("Before"))),
                ArticleBlock.Image("https://cdn/a.png"),
                ArticleBlock.Paragraph(listOf(ArticleText("After"))),
            ),
            blocks,
        )
    }

    @Test
    fun `headings are bold and blank paragraphs disappear`() {
        val blocks = ArticleHtmlParser.parse("<h2>Changes</h2><p>   </p><br><br>")
        assertEquals(listOf(ArticleBlock.Heading(listOf(ArticleText("Changes", isBold = true)))), blocks)
    }

    @Test
    fun `line breaks inside a paragraph are kept without extra spaces`() {
        val blocks = ArticleHtmlParser.parse("<p>First line <br> second line</p>")
        assertEquals(listOf(ArticleBlock.Paragraph(listOf(ArticleText("First line\nsecond line")))), blocks)
    }
}
