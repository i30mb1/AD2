package n7.ad2.parser.heroes.wiki

import org.jsoup.nodes.Document
import org.jsoup.nodes.Element

/**
 * Parts of dota2.fandom.com hero pages that the Valve datafeed does not have.
 *
 * Pages are walked in document order from heading to heading instead of relying on nesting: on some EN pages
 * a Lua error leaves an unclosed `<strong class="error">` and everything after it ends up inside that element.
 */
internal object WikiHeroParser {

    /** [entity] is a hero or an item the lines are about, e.g. a counter hero or a recommended item. */
    data class Block(val entity: String?, val lines: List<String>)

    data class Section(val title: String, val level: Int, val blocks: List<Block>) {
        val textLength: Int get() = blocks.sumOf { block -> block.lines.sumOf { it.length } }
    }

    /** [tag] marks notes about a talent, Aghanim's upgrade etc. [indent] is 1 for a nested note. */
    data class Note(val text: String, val tag: String?, val indent: Int)

    data class NoteSection(val title: String?, val notes: List<Note>)

    data class HeroPage(
        val recommendedItems: List<Section>,
        /** key is the ability name in lower case, the same on the wiki and in the datafeed */
        val abilityNotes: Map<String, List<NoteSection>>,
    )

    private val cosmeticTags = setOf("COSMETIC", "КОСМЕТИЧЕСКИЙ ЭФФЕКТ")
    private val generalNoteTitles = setOf("ПРИМЕЧАНИЯ", "NOTES")

    fun parseHeroPage(doc: Document): HeroPage = HeroPage(
        recommendedItems = sections(doc)
            .dropWhile { !it.title.startsWith("Recommended Items") && !it.title.startsWith("Рекомендуемые предметы") }
            .let { list -> if (list.isEmpty()) emptyList() else parseRecommendedItems(list.first().heading, doc) },
        abilityNotes = parseAbilityNotes(doc),
    )

    /** Counters subpage: "Bad against", "Good against", "Works well with" with their "Others"/"Items" parts. */
    fun parseCounters(doc: Document): List<Section> = sections(doc).map { section ->
        Section(section.title, section.level, entityBlocks(section.elements))
    }.filter { it.blocks.isNotEmpty() }

    data class Guide(val playstyle: String?, val pros: List<String>, val cons: List<String>, val tips: List<Section>)

    /**
     * Guide subpage: playstyle with pros and cons, then tips. Skill build and talent tables are not text and
     * are skipped, the items part repeats the recommended items of the hero page.
     */
    fun parseGuide(doc: Document): Guide {
        val table = doc.select("table").firstOrNull { table ->
            table.selectFirst("th")?.text()?.trim()?.let { it == "Playstyle" || it == "Стиль игры" } == true
        }
        val rows = table?.select("tr").orEmpty()
        val playstyle = rows.getOrNull(1)?.cleanText()?.takeIf { it.length > 20 }
        val prosCons = rows.getOrNull(3)?.children()?.filter { it.tagName() == "td" }.orEmpty()
        fun items(cell: Element?) = cell?.select("li")?.map { it.cleanText() }?.filter { it.isNotEmpty() }.orEmpty()
        val tips = sections(doc)
            .filterNot { it.title.matches(templateHeading) || it.title in guideItemTitles }
            .map { section -> Section(section.title, section.level, entityBlocks(section.elements)) }
            .filter { it.blocks.isNotEmpty() }
        return Guide(playstyle, items(prosCons.getOrNull(0)), items(prosCons.getOrNull(1)), tips)
    }

    /** Empty sections of the RU guide template, e.g. "СТИЛЬ1" or "Способность 2". */
    private val templateHeading = Regex("""^(СТИЛЬ\d+|Способность \d+|STYLE\d+|Ability \d+)$""")
    private val guideItemTitles = setOf("Items", "Предметы")

    // ===== recommended items =====

    private fun parseRecommendedItems(heading: Element, doc: Document): List<Section> {
        val result = mutableListOf<Section>()
        var title: String? = null
        var blocks = mutableListOf<Block>()
        fun flush() {
            if (title != null && blocks.isNotEmpty()) result += Section(title!!, 3, blocks)
            blocks = mutableListOf()
        }
        elementsAfter(heading, doc).forEach { element ->
            when {
                element.tagName() == "p" && element.selectFirst("b") != null && element.text().trim().endsWith(":") -> {
                    flush()
                    title = element.text().trim().removeSuffix(":").trim()
                }
                element.tagName() == "li" && element.isTopLevelItem() -> {
                    val item = element.select("a[title]").firstOrNull()?.attr("title")
                    blocks += Block(item, listOf(element.cleanText()).filter { it.isNotEmpty() })
                }
            }
        }
        flush()
        return result
    }

    // ===== ability notes =====

    private fun parseAbilityNotes(doc: Document): Map<String, List<NoteSection>> {
        val result = linkedMapOf<String, List<NoteSection>>()
        doc.select("div.ability-background").forEach { ability ->
            val name = ability.selectFirst("span[style*='color:#fff']")?.text()?.trim()?.takeIf { it.isNotEmpty() } ?: return@forEach
            // the notes column is the closest "flex:1 1 450px" sibling of the ability card or of one of its parents
            val notesColumn = (sequenceOf(ability) + ability.parents().asSequence())
                .mapNotNull { parent -> parent.nextElementSibling()?.takeIf { it.attr("style").contains("flex:1 1 450px") } }
                .firstOrNull() ?: return@forEach
            val sections = parseNoteColumn(notesColumn)
            if (sections.isNotEmpty()) result[name.lowercase()] = sections
        }
        return result
    }

    private fun parseNoteColumn(column: Element): List<NoteSection> {
        val root = column.selectFirst("div[style*='word-wrap']") ?: column
        val sections = mutableListOf<NoteSection>()
        var title: String? = null
        var notes = mutableListOf<Note>()
        fun flush() {
            if (notes.isNotEmpty()) sections += NoteSection(title, notes)
            notes = mutableListOf()
        }
        root.children().forEach { child ->
            when {
                child.isBadge() -> {
                    val badge = child.text().trim()
                    // the first badge is the general "Notes" label next to the ability id
                    if (badge.any { it.isLetter() }) {
                        flush()
                        title = badge.takeUnless { it.uppercase() in generalNoteTitles }
                    }
                }
                child.tagName() == "ul" -> child.children().filter { it.tagName() == "li" }.forEach { item ->
                    collectNotes(item, 0, notes)
                }
            }
        }
        flush()
        return sections.filter { section -> section.title?.uppercase() !in cosmeticTags }
    }

    private fun collectNotes(item: Element, indent: Int, into: MutableList<Note>) {
        val badge = item.children().firstOrNull { it.isBadge() }?.text()?.trim()
        if (badge != null && badge.uppercase() in cosmeticTags) return
        val text = item.cleanText()
        if (text.isNotEmpty()) into += Note(text, badge?.takeIf { it.any(Char::isLetter) }, indent)
        item.children().filter { it.tagName() == "ul" }.flatMap { it.children() }.filter { it.tagName() == "li" }.forEach { nested ->
            collectNotes(nested, indent + 1, into)
        }
    }

    /** Small colored label the wiki uses for "NOTES", "TALENT", "SCEPTER"... */
    private fun Element.isBadge(): Boolean = tagName() == "div" && attr("style").contains("display:inline-block") &&
        attr("style").contains("border-radius") && text().length in 1..40

    // ===== sections =====

    private class RawSection(val heading: Element, val title: String, val level: Int, val elements: List<Element>)

    private fun sections(doc: Document): List<RawSection> {
        val all = doc.allElements
        val headings = all.filter { it.tagName() in setOf("h2", "h3", "h4") && it.selectFirst("span.mw-headline") != null }
        return headings.mapIndexed { index, heading ->
            val start = all.indexOf(heading)
            val end = headings.getOrNull(index + 1)?.let { all.indexOf(it) } ?: all.size
            RawSection(
                heading = heading,
                title = heading.selectFirst("span.mw-headline")!!.text().trim(),
                level = heading.tagName().drop(1).toInt(),
                elements = all.subList(start + 1, end),
            )
        }
    }

    private fun elementsAfter(heading: Element, doc: Document): List<Element> {
        val all = doc.allElements
        val start = all.indexOf(heading)
        val end = (start + 1 until all.size).firstOrNull { all[it].tagName() == "h2" } ?: all.size
        return all.subList(start + 1, end)
    }

    /**
     * A bold hero/item name in a 12pt div starts a block, list items and paragraphs are its lines.
     * Paragraphs and items inside tables are skipped: those are skill builds, talent trees and icons.
     */
    private fun entityBlocks(elements: List<Element>): List<Block> {
        val blocks = mutableListOf<Block>()
        var entity: String? = null
        var lines = mutableListOf<String>()
        fun flush() {
            if (lines.isNotEmpty()) blocks += Block(entity, lines)
            lines = mutableListOf()
        }
        elements.forEach { element ->
            when {
                element.tagName() == "div" && element.attr("style").contains("font-size:12pt") -> {
                    flush()
                    entity = element.selectFirst("a[title]")?.attr("title") ?: element.text().trim()
                }
                element.parents().any { it.tagName() == "table" } -> Unit
                element.tagName() == "li" && element.isTopLevelItem() -> element.cleanText().takeIf { it.isNotEmpty() }?.let { lines += it }
                element.tagName() == "p" && element.parents().none { it.tagName() == "li" } ->
                    element.cleanText().takeIf { it.length > 1 }?.let { lines += it }
            }
        }
        flush()
        return blocks
    }

    private fun Element.isTopLevelItem(): Boolean = parents().none { it.tagName() == "li" }

    /**
     * Text of the element without nested lists, audio buttons and icons. Values the EN wiki failed to calculate
     * leave Lua error messages, those are cut out and a line still mentioning one is dropped.
     */
    private fun Element.cleanText(): String {
        val copy = clone()
        copy.select("ul, ol, .ext-audiobutton, audio, sup.reference, .noprint img, style, script, .error, .scribunto-error").remove()
        copy.children().filter { it.isBadge() }.forEach { it.remove() }
        val text = copy.text()
            .replace("Link ▶️", "")
            .replace(Regex("""\s+"""), " ")
            .replace(Regex("""\(\s*[/+*]?\s*\)"""), "")
            .replace(Regex("""\s+([.,:;!?)])"""), "$1")
            .replace("( ", "(")
            .replace(Regex("""\s{2,}"""), " ")
            .trim()
        return if (wikiErrors.containsMatchIn(text)) "" else text
    }

    private val wikiErrors = Regex("""Lua error|Expression error|Ошибка Lua|Ошибка выражения|scribunto""", RegexOption.IGNORE_CASE)
}
