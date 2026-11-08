package n7.ad2.parser

import org.jsoup.nodes.Element
import org.jsoup.nodes.TextNode

/**
 * Text of a dota2.fandom.com element as a reader sees it on the page. A formula becomes one line instead of its
 * MathML text followed by the TeX source. What works only with a mouse or what the wiki failed to render is cut out:
 * tooltip marks "[?]", reference marks "[1]", templates and links with a broken bracket, stray audio tags.
 * Formulas are replaced in place, so it is called on a copy.
 */
internal fun Element.wikiText(): String {
    select("span.mwe-math-element").forEach { formula ->
        formula.replaceWith(TextNode(formula.selectFirst("math")?.let { formulaText(it) }.orEmpty()))
    }
    return text().cleanWikiText()
}

internal fun String.cleanWikiText(): String = this
    .replace("Link ▶️", "")
    .replace("&nbsp;", " ")
    .replace("%%", "%")
    .replace(brokenAudioTag, "")
    .replace(fileName, "")
    .replace(fileLinkTail, "")
    .replace(brokenTemplate, "$1")
    .replace(brokenLink, "$1")
    .replace(marks, "")
    // empty brackets are left by icons of a talent or an upgrade, "25 (/) for" reads "25 for"
    .replace(Regex("""\(\s*[/+*]?\s*\)"""), "")
    .replace(Regex("""\s+"""), " ")
    .replace(Regex("""\(\s+"""), "(")
    .replace(Regex("""\s+([.,:;!?)])"""), "$1")
    .trim()

/** `sm2>vo_bloodseeker_thirst_03.mp3</sm2>`: a sound tag without its opening bracket. */
private val brokenAudioTag = Regex("""\S*sm2>\S*?</sm2>""")

/** "File:ArcWardenUltPng.jpeg": an icon the wiki failed to draw shows its file name, sometimes after a bare `[[`. */
private val fileName = Regex("""(?:\[\[\s*)?(?:File|Файл):[^|\]\n]*?\.(?:png|jpe?g|gif|svg|webp)""", RegexOption.IGNORE_CASE)

/** "|20px|link=Linken's Sphere|class=noprint]]": the rest of such an icon. */
private val fileLinkTail = Regex("""\|\d+px\|[^\[\]]*?]]""")

/** `{A|Brain Sap|Bane}}`: an ability, item or hero template with a lost brace, the first value is the name. */
private val brokenTemplate = Regex("""\{+[A-Za-z]{1,2}\|([^|{}]+)(?:\|[^{}]*)?}+""")

/** `[[Сопротивление магии}комбинированной плотностью]]`: a link with a typo, the last part is what the page shows. */
private val brokenLink = Regex("""\[\[(?:[^\[\]|}]*[|}])?([^\[\]]+)]]""")

/** "[?]" opens an explanation on hover, "[1]" is a reference. */
private val marks = Regex("""\[(?:\?}?|\d{1,2})]""")

// ===== formulas =====

private fun formulaText(math: Element): String = math.children().joinToString("") { mathText(it, compact = false) }.trim()

/** [compact] is for sub- and superscripts, where operators go without spaces: ∑ₙ₌₁ⁱ. */
private fun mathText(node: Element, compact: Boolean): String {
    val children = node.children()
    fun part(index: Int) = children.getOrNull(index)?.let { mathText(it, compact) }.orEmpty()
    return when (node.tagName()) {
        "annotation", "annotation-xml" -> ""
        "mi", "mn", "mtext", "ms" -> node.text().trim()
        "mo" -> operator(node.text().trim(), compact)
        "mspace" -> " "
        "msup", "mover" -> part(0) + script(children.getOrNull(1), superscripts, "^")
        "msub", "munder" -> part(0) + script(children.getOrNull(1), subscripts, "_")
        "msubsup", "munderover" -> part(0) + script(children.getOrNull(1), subscripts, "_") + script(children.getOrNull(2), superscripts, "^")
        "mfrac" -> group(part(0)) + "/" + group(part(1))
        "msqrt" -> "√" + group(children.joinToString("") { mathText(it, compact) })
        else -> children.joinToString("") { mathText(it, compact) }
    }
}

private val spacedOperators = setOf("×", "·", "+", "−", "-", "=", "≈", "≠", "≤", "≥", "<", ">", "÷", "±", "→")

private fun operator(text: String, compact: Boolean): String {
    // function application and invisible times have no glyph
    val visible = text.filterNot { it in '⁡'..'⁤' }
    return if (!compact && visible in spacedOperators) " $visible " else visible
}

private fun group(text: String): String = if (text.any { it == ' ' }) "($text)" else text

/** As Unicode sub/superscript characters when all of them exist, `x^(…)` otherwise. */
private fun script(node: Element?, characters: Map<Char, Char>, mark: String): String {
    val text = node?.let { mathText(it, compact = true) }.orEmpty()
    if (text.isEmpty()) return ""
    return if (text.all { it in characters }) text.map { characters.getValue(it) }.joinToString("") else mark + if (text.length == 1) text else "($text)"
}

private val superscripts = ("0123456789+-−=()ni" zip "⁰¹²³⁴⁵⁶⁷⁸⁹⁺⁻⁻⁼⁽⁾ⁿⁱ").toMap()
private val subscripts = ("0123456789+-−=()aeoxhklmnpstij" zip "₀₁₂₃₄₅₆₇₈₉₊₋₋₌₍₎ₐₑₒₓₕₖₗₘₙₚₛₜᵢⱼ").toMap()
