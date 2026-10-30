package n7.ad2.items.domain.internal.usecase

import n7.ad2.items.domain.model.ArticleBlock
import n7.ad2.items.domain.model.ArticleText
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode

/**
 * Turns article html into [ArticleBlock]s the screen draws natively.
 * Pure: no IO here so it can be unit-tested.
 *
 * Inline text is collected with its style until a block tag (paragraph, heading, list, image...) ends it.
 * Tags the app can't show (iframes, scripts) are dropped, unknown tags keep only their text.
 */
internal object ArticleHtmlParser {

    private val SKIPPED_TAGS = setOf("iframe", "script", "style", "video", "object", "embed")
    private val HEADING_TAGS = setOf("h1", "h2", "h3", "h4", "h5", "h6")
    private val BLOCK_TAGS = setOf("p", "div", "section", "article", "header", "footer", "pre", "center", "figure", "figcaption")
    private val LIST_TAGS = setOf("ul", "ol")
    private val WHITESPACE = Regex("""\s+""")
    private val SPACES_AROUND_NEWLINE = Regex(""" *\n *""")

    fun parse(html: String): List<ArticleBlock> {
        val builder = Builder()
        builder.walk(Jsoup.parseBodyFragment(html).body(), Style())
        builder.flushParagraph()
        return builder.blocks
    }

    private data class Style(val isBold: Boolean = false, val isItalic: Boolean = false, val link: String? = null)

    private class Builder {
        val blocks = mutableListOf<ArticleBlock>()
        private val inline = mutableListOf<ArticleText>()

        fun walk(element: Element, style: Style) {
            element.childNodes().forEach { node -> visit(node, style) }
        }

        private fun visit(node: Node, style: Style) {
            if (node is TextNode) {
                addText(node.wholeText.replace(WHITESPACE, " "), style)
                return
            }
            if (node !is Element) return
            val tag = node.tagName().lowercase()
            when {
                tag in SKIPPED_TAGS -> Unit
                tag == "br" -> addText("\n", style)
                tag == "img" -> {
                    flushParagraph()
                    val src = node.attr("src").trim()
                    if (src.startsWith("http")) blocks.add(ArticleBlock.Image(src))
                }
                tag in HEADING_TAGS -> {
                    flushParagraph()
                    inlineOf(node, style.copy(isBold = true))?.let { blocks.add(ArticleBlock.Heading(it)) }
                }
                tag in LIST_TAGS -> {
                    flushParagraph()
                    node.children().forEach { child ->
                        if (child.tagName().lowercase() == "li") addListItem(child, style) else walkBlock(child, style)
                    }
                }
                tag == "li" -> addListItem(node, style)
                tag == "blockquote" -> {
                    flushParagraph()
                    inlineOf(node, style.copy(isItalic = true))?.let { blocks.add(ArticleBlock.Quote(it)) }
                }
                tag == "tr" -> {
                    flushParagraph()
                    val cells = node.children().map { it.text().trim() }.filter { it.isNotEmpty() }
                    if (cells.isNotEmpty()) blocks.add(ArticleBlock.Paragraph(listOf(ArticleText(cells.joinToString(" · ")))))
                }
                tag in BLOCK_TAGS -> walkBlock(node, style)
                tag == "b" || tag == "strong" -> walk(node, style.copy(isBold = true))
                tag == "i" || tag == "em" -> walk(node, style.copy(isItalic = true))
                tag == "a" -> {
                    val href = node.attr("href").trim().takeIf { it.startsWith("http") }
                    walk(node, if (href != null) style.copy(link = href) else style)
                }
                else -> walk(node, style)
            }
        }

        private fun walkBlock(element: Element, style: Style) {
            flushParagraph()
            walk(element, style)
            flushParagraph()
        }

        // the item's own text comes first, a nested list goes after it as items of its own
        private fun addListItem(element: Element, style: Style) {
            flushParagraph()
            val nested = mutableListOf<Element>()
            element.childNodes().forEach { child ->
                if (child is Element && child.tagName().lowercase() in LIST_TAGS) nested.add(child) else visit(child, style)
            }
            takeInline()?.let { blocks.add(ArticleBlock.ListItem(it)) }
            nested.forEach { visit(it, style) }
        }

        private fun inlineOf(element: Element, style: Style): List<ArticleText>? {
            walk(element, style)
            return takeInline()
        }

        private fun addText(text: String, style: Style) {
            if (text.isEmpty()) return
            val last = inline.lastOrNull()
            if (last != null && last.isBold == style.isBold && last.isItalic == style.isItalic && last.link == style.link) {
                inline[inline.lastIndex] = last.copy(text = last.text + text)
            } else {
                inline.add(ArticleText(text, style.isBold, style.isItalic, style.link))
            }
        }

        fun flushParagraph() {
            takeInline()?.let { blocks.add(ArticleBlock.Paragraph(it)) }
        }

        /** The collected inline text without outer spaces and blank lines, null when there is nothing to show. */
        private fun takeInline(): List<ArticleText>? {
            if (inline.isEmpty()) return null
            val parts = inline.map { part -> part.copy(text = part.text.replace(SPACES_AROUND_NEWLINE, "\n")) }.toMutableList()
            inline.clear()
            parts[0] = parts[0].copy(text = parts[0].text.trimStart())
            parts[parts.lastIndex] = parts[parts.lastIndex].copy(text = parts[parts.lastIndex].text.trimEnd())
            val result = parts.filter { it.text.isNotEmpty() }
            return result.takeIf { list -> list.any { it.text.isNotBlank() } }
        }
    }
}
