package n7.ad2.parser.items

import n7.ad2.parser.fetchWithApi
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Text sections of dota2.fandom.com item pages that the Valve datafeed does not have. Each language is taken from
 * its own wiki (RU from /ru, EN from the English one), nothing is translated. Game values never come from here.
 */
internal object WikiItemParser {

    class WikiItem(
        val tips: List<String>,
        val trivia: List<String>,
        val additionalInformation: List<String>,
        /** "Initiators rely on it: Earthshaker, Sand King, ..." */
        val recommendedFor: List<String>,
    )

    private val tipTitles = setOf("tips", "советы")
    private val triviaTitles = setOf("trivia", "факты", "интересные факты")
    private val additionalTitles = setOf("additional information", "дополнительная информация")
    private val recommendedTitles = setOf("recommended heroes", "рекомендован героям", "рекомендовано героям", "рекомендуется героям")

    fun load(englishName: String, language: Language): WikiItem? {
        val base = if (language == Language.RU) "https://dota2.fandom.com/ru/wiki/" else "https://dota2.fandom.com/wiki/"
        val doc = fetchWithApi(base + englishName.replace(" ", "_")) ?: return null
        return parse(doc)
    }

    fun parse(doc: Document): WikiItem {
        val sections = sections(doc)
        fun lines(titles: Set<String>) = sections.filter { it.title.lowercase() in titles }.flatMap { section ->
            section.elements.filter { it.tagName() == "li" && it.isTopLevelItem() }.map { it.cleanText() }
        }.filter { it.length > 1 }.distinct()

        val recommended = sections.filter { it.title.lowercase() in recommendedTitles }.flatMap { section ->
            section.elements.filter { it.tagName() == "li" && it.isTopLevelItem() }.mapNotNull { item ->
                val text = item.cleanText().removeSuffix(":").removeSuffix(".")
                val heroes = item.select("ul li a[title]").map { it.attr("title") }.distinct()
                when {
                    heroes.isEmpty() -> text.takeIf { it.length > 1 }
                    text.isEmpty() -> heroes.joinToString()
                    else -> "$text: ${heroes.joinToString()}"
                }
            }
        }.distinct()

        return WikiItem(
            tips = lines(tipTitles),
            trivia = lines(triviaTitles),
            additionalInformation = lines(additionalTitles),
            recommendedFor = recommended,
        )
    }

    private class Section(val title: String, val elements: List<Element>)

    /** Walked in document order: on some EN pages a Lua error leaves unclosed tags and breaks the nesting. */
    private fun sections(doc: Document): List<Section> {
        val all = doc.allElements
        val headings = all.filter { it.tagName() == "h2" && it.selectFirst("span.mw-headline") != null }
        return headings.mapIndexed { index, heading ->
            val start = all.indexOf(heading)
            val end = headings.getOrNull(index + 1)?.let { all.indexOf(it) } ?: all.size
            Section(heading.selectFirst("span.mw-headline")!!.text().trim(), all.subList(start + 1, end))
        }
    }

    private fun Element.isTopLevelItem(): Boolean = parents().none { it.tagName() == "li" || it.tagName() == "table" }

    private fun Element.cleanText(): String {
        val copy = clone()
        copy.select("ul, ol, .ext-audiobutton, audio, sup.reference, style, script, .error, .scribunto-error").remove()
        val text = copy.text()
            .replace("Link ▶️", "")
            .replace(Regex("""\[\d+]"""), "")
            .replace(Regex("""\s+"""), " ")
            .replace(Regex("""\s+([.,:;!?)])"""), "$1")
            .replace("( ", "(")
            .trim()
        return if (wikiErrors.containsMatchIn(text)) "" else text
    }

    private val wikiErrors = Regex("""Lua error|Expression error|Ошибка Lua|Ошибка выражения|scribunto""", RegexOption.IGNORE_CASE)
}
