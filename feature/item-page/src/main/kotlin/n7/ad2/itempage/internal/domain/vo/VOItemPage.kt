package n7.ad2.itempage.internal.domain.vo

/** Everything the item page shows, [title] is the display name from `description.json`, the asset key is only a fallback. */
internal data class VOItemPage(
    val title: String,
    val imageUrl: String,
    val cost: Int?,
    val boughtFrom: String?,
    val description: String?,
    val bonuses: List<VOBonus>,
    val recipe: VORecipe?,
    val upgrades: List<VOItemLink>,
    val abilities: List<VOAbility>,
    val properties: List<VOFact>,
    val recommendations: List<VORecommendation>,
    val sections: List<VOSection>,
)

/** "+10" and "к силе", [value] is empty when the bonus has no number in front. */
internal data class VOBonus(val value: String, val text: String)

/** Another item the page links to, [key] is its asset folder, [cost] is null when its file is missing. */
internal data class VOItemLink(val key: String, val name: String, val imageUrl: String, val cost: Int?)

/** [recipeCost] is what is paid for the recipe scroll on top of the [parts], null when the item has no scroll. */
internal data class VORecipe(val parts: List<VOItemLink>, val recipeCost: Int?)

/** [kind] is "Активное"/"Пассивное" as the game writes it, null when the name has no kind. */
internal data class VOAbility(
    val kind: String?,
    val name: String,
    val audioUrl: String?,
    val cooldown: String?,
    val mana: String?,
    val params: List<String>,
    val text: List<String>,
    val facts: List<VOFact>,
    val effects: List<String>,
    val notes: List<String>,
)

internal data class VOFact(val key: String, val value: String)

internal data class VOHero(val name: String, val imageUrl: String)

/** A wiki sentence and the heroes it names. */
internal data class VORecommendation(val text: String, val heroes: List<VOHero>)

internal enum class VOSectionKind { TIPS, ADDITIONAL, LORE, TRIVIA }

internal data class VOSection(val kind: VOSectionKind, val items: List<String>)
