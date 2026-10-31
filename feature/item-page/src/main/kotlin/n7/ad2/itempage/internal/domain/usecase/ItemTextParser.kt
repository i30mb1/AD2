package n7.ad2.itempage.internal.domain.usecase

import n7.ad2.itempage.internal.domain.vo.VOFact

private val tagRegex = Regex("<[^>]+>")
private val breakRegex = Regex("""<br\s*/?>""", RegexOption.IGNORE_CASE)
private val bonusRegex = Regex("""^([+\-−]?\d[\d.,]*%?)\s+(.+)$""")
private const val MAX_FACT_KEY_LENGTH = 40

/** Turns the texts of `description.json` (Valve tooltips with `<br>`/`<span>` markup, wiki sentences) into pieces the screen shows separately. */
internal object ItemTextParser {

    /** "+10 к силе" -> "+10" and "к силе"; a bonus without a number in front stays whole in the text. */
    fun splitBonus(bonus: String): Pair<String, String> {
        val match = bonusRegex.find(bonus.trim()) ?: return "" to bonus.trim()
        return match.groupValues[1] to match.groupValues[2]
    }

    /** "Активное: Avatar" -> "Активное" and "Avatar", a name without a kind gives a null kind. */
    fun splitAbilityName(name: String): Pair<String?, String> {
        val colon = name.indexOf(": ")
        if (colon <= 0) return null to name.trim()
        return name.substring(0, colon).trim() to name.substring(colon + 2).trim()
    }

    /**
     * Splits the ability tooltip into the paragraphs of text and the "Key: value" lines after them,
     * "...отрицательных эффектов.<br><br>Длительность: 9/8/7 сек." -> one paragraph and one fact.
     * The first paragraph is always text, even with a colon inside.
     */
    fun splitDescription(html: String): Pair<List<String>, List<VOFact>> {
        val lines = html.split(breakRegex).map(::plainText).filter { it.isNotEmpty() }
        val text = mutableListOf<String>()
        val facts = mutableListOf<VOFact>()
        lines.forEachIndexed { index, line ->
            val fact = if (index == 0) null else toFact(line)
            if (fact != null) facts += fact else text += line
        }
        return text to facts
    }

    /** "Начальные заряды: 3" -> a fact, a sentence that only has a colon somewhere far inside it is not one. */
    fun toFact(line: String): VOFact? {
        val colon = line.indexOf(": ")
        if (colon <= 0 || colon > MAX_FACT_KEY_LENGTH) return null
        val value = line.substring(colon + 2).trim()
        if (value.isEmpty()) return null
        return VOFact(line.substring(0, colon).trim(), value)
    }

    /**
     * The wiki writes recommendations as "Why these heroes: Hero, Hero, Hero".
     * Gives the sentence and the names after the last colon; [isHero] keeps only real heroes, so a colon in the middle of a sentence does not cut it.
     */
    fun splitRecommendation(line: String, isHero: (String) -> Boolean): Pair<String, List<String>> {
        val colon = line.lastIndexOf(':')
        if (colon <= 0) return line.trim() to emptyList()
        val names = line.substring(colon + 1).split(',').map { it.trim() }.filter { it.isNotEmpty() }
        if (names.isEmpty() || !names.all(isHero)) return line.trim() to emptyList()
        return line.substring(0, colon).trim() to names
    }

    /** What is left to pay for the recipe after the components, null when there is no recipe. */
    fun recipeCost(itemCost: Int?, componentCosts: List<Int?>): Int? {
        if (itemCost == null || componentCosts.any { it == null }) return null
        return (itemCost - componentCosts.sumOf { it ?: 0 }).takeIf { it > 0 }
    }

    /** "4050" or "4 050" -> 4050, anything else -> null. */
    fun parseCost(cost: String?): Int? = cost?.filter(Char::isDigit)?.toIntOrNull()

    fun plainText(html: String): String = html
        .replace(breakRegex, "\n")
        .replace(tagRegex, "")
        .replace("&nbsp;", " ")
        .replace("&amp;", "&")
        .replace("&lt;", "<")
        .replace("&gt;", ">")
        .replace("&quot;", "\"")
        .replace("&#39;", "'")
        .trim()
}
