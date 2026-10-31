package n7.ad2.itempage.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import n7.ad2.AppLocale
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.itempage.internal.domain.model.Ability
import n7.ad2.itempage.internal.domain.model.LocalItemInfo
import n7.ad2.itempage.internal.domain.vo.VOAbility
import n7.ad2.itempage.internal.domain.vo.VOBonus
import n7.ad2.itempage.internal.domain.vo.VOHero
import n7.ad2.itempage.internal.domain.vo.VOItemLink
import n7.ad2.itempage.internal.domain.vo.VOItemPage
import n7.ad2.itempage.internal.domain.vo.VORecipe
import n7.ad2.itempage.internal.domain.vo.VORecommendation
import n7.ad2.itempage.internal.domain.vo.VOSection
import n7.ad2.itempage.internal.domain.vo.VOSectionKind
import javax.inject.Inject

private const val RECIPE_KEY = "recipe"

internal class GetItemInfoUseCase @Inject constructor(private val res: Resources, private val dispatchers: DispatchersProvider) {

    private val json = Json { ignoreUnknownKeys = true }

    operator fun invoke(itemName: String, appLocale: AppLocale): Flow<VOItemPage> = flow {
        val item = readItem(itemName, appLocale) ?: error("no description for item $itemName")
        val cost = ItemTextParser.parseCost(item.cost)
        val heroes = mutableMapOf<String, Boolean>()
        val isHero = { name: String -> heroes.getOrPut(name) { hasAsset("heroes/${heroKey(name)}/full.webp") } }

        val page = VOItemPage(
            title = item.name ?: itemName,
            imageUrl = imageUrl(itemName),
            cost = cost,
            boughtFrom = item.boughtFrom?.let(ItemTextParser::plainText)?.takeIf { it.isNotEmpty() },
            description = item.description?.let(ItemTextParser::plainText)?.takeIf { it.isNotEmpty() },
            bonuses = item.bonuses.orEmpty().map(ItemTextParser::plainText).filter { it.isNotEmpty() }.map { bonus ->
                val (value, text) = ItemTextParser.splitBonus(bonus)
                VOBonus(value, text)
            },
            recipe = recipe(item, cost, appLocale),
            upgrades = item.upgradeIn.orEmpty().map { name -> link(itemKey(name), appLocale, fallbackName = name) },
            abilities = item.abilities.orEmpty().mapNotNull(::ability),
            properties = item.properties.orEmpty().map(ItemTextParser::plainText).mapNotNull(ItemTextParser::toFact),
            recommendations = item.recommendedFor.orEmpty().map(ItemTextParser::plainText).filter { it.isNotEmpty() }.map { line ->
                val (text, names) = ItemTextParser.splitRecommendation(line, isHero)
                VORecommendation(text, names.map { name -> VOHero(name, "file:///android_asset/heroes/${heroKey(name)}/full.webp") })
            },
            sections = listOfNotNull(
                section(VOSectionKind.TIPS, item.tips),
                section(VOSectionKind.ADDITIONAL, item.additionalInformation),
                section(VOSectionKind.LORE, item.lore),
                section(VOSectionKind.TRIVIA, item.trivia),
            ),
        )
        emit(page)
    }.flowOn(dispatchers.IO)

    /** Components and, when the components cost less than the item, the recipe scroll between them and the result. */
    private fun recipe(item: LocalItemInfo, cost: Int?, appLocale: AppLocale): VORecipe? {
        val keys = item.consistFrom.orEmpty()
        if (keys.isEmpty()) return null
        val parts = keys.filterNot { it.contains(RECIPE_KEY) }.map { key -> link(key, appLocale, fallbackName = key) }
        return VORecipe(parts, ItemTextParser.recipeCost(cost, parts.map { it.cost }))
    }

    private fun link(key: String, appLocale: AppLocale, fallbackName: String): VOItemLink {
        val linked = readItem(key, appLocale)
        return VOItemLink(key, linked?.name ?: fallbackName, imageUrl(key), ItemTextParser.parseCost(linked?.cost))
    }

    private fun ability(ability: Ability): VOAbility? {
        val fullName = ability.abilityName?.let(ItemTextParser::plainText)?.takeIf { it.isNotEmpty() } ?: return null
        val (kind, name) = ItemTextParser.splitAbilityName(fullName)
        val (text, facts) = ItemTextParser.splitDescription(ability.description.orEmpty())
        return VOAbility(
            kind = kind,
            name = name,
            audioUrl = ability.audioUrl?.takeIf { it.isNotBlank() },
            cooldown = ability.cooldown?.let(ItemTextParser::plainText)?.takeIf { it.isNotEmpty() },
            mana = ability.mana?.let(ItemTextParser::plainText)?.takeIf { it.isNotEmpty() },
            params = ability.params.orEmpty().map(ItemTextParser::plainText).filter { it.isNotEmpty() },
            text = text + listOfNotNull(ability.story?.let(ItemTextParser::plainText)?.takeIf { it.isNotEmpty() }),
            facts = facts,
            effects = ability.effects.orEmpty().map(ItemTextParser::plainText).filter { it.isNotEmpty() },
            notes = ability.notes.orEmpty().map(ItemTextParser::plainText).filter { it.isNotEmpty() },
        )
    }

    private fun section(kind: VOSectionKind, lines: List<String>?): VOSection? {
        val items = lines.orEmpty().map(ItemTextParser::plainText).filter { it.isNotEmpty() }
        return if (items.isEmpty()) null else VOSection(kind, items)
    }

    /** Falls back to English: wiki-only items and fresh patches may not have a localized file yet. Null when the item has no file at all. */
    private fun readItem(key: String, appLocale: AppLocale): LocalItemInfo? {
        val text = runCatching { readText("items/$key/${appLocale.value}/description.json") }
            .recoverCatching { readText("items/$key/${AppLocale.English.value}/description.json") }
            .getOrNull() ?: return null
        return runCatching { json.decodeFromString<LocalItemInfo>(text) }.getOrNull()
    }

    private fun readText(path: String): String = res.getAssets(path).bufferedReader().use { it.readText() }

    private fun hasAsset(path: String): Boolean = runCatching { res.getAssets(path).close() }.isSuccess

    private fun imageUrl(key: String): String = "file:///android_asset/items/$key/full.webp"

    /** "Arcane Blink" -> "arcane_blink", the folder names of the assets. */
    private fun itemKey(name: String): String = name.lowercase().replace(" ", "_")

    private fun heroKey(name: String): String = name.lowercase().replace(" ", "_")
}
