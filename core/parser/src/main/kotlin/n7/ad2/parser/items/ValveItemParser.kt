package n7.ad2.parser.items

import java.io.File
import java.net.URI
import java.net.http.HttpClient
import java.net.http.HttpRequest
import java.net.http.HttpResponse
import java.time.Duration
import n7.ad2.parser.assetsDatabaseItems
import n7.ad2.parser.saveFile
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser

/**
 * Rebuilds `items/<key>/<en|ru>/description.json`, `items/names.json`, `items/list.json` and `items/removed.json`.
 *
 * The app shows only items that are in the game now: buyable ones, Roshan drops and the current neutral pool.
 * `list.json` gives the section of every item the prepackaged items.db (2023) lacks or files under a stale section,
 * `removed.json` lists app items that left the game; the app syncs its table with both on start.
 * Assets of a removed item are deleted unless a hero page still shows its icon, then only the icon is kept.
 *
 * Sources:
 *  1. Valve datafeed (current patch, RU + EN): name, lore, cost, recipe, bonuses, abilities, notes, cooldown, mana,
 *     cast range, channel time, health cost, charges, shop stock, neutral tier, what the item builds into.
 *  2. Game files `scripts/npc/items.txt` (SteamDatabase): shop, target type, spell immunity, dispel, share/sell/disassemble rules.
 *  3. dota2.fandom.com (RU wiki for RU, EN wiki for EN, via api.php): tips, trivia, additional information, recommended heroes.
 *     When the wiki has nothing (or `ITEMS_WIKI=false`), the lists of the previous file of the same language are kept.
 */
fun main() {
    val existingFolders = File(assetsDatabaseItems).listFiles { file -> file.isDirectory }?.map { it.name }?.sorted().orEmpty()
    val only = System.getenv("ITEMS")?.split(",")?.map { it.trim().lowercase().replace(" ", "_") }?.filter { it.isNotEmpty() }.orEmpty()
    val useWiki = System.getenv("ITEMS_WIKI")?.lowercase() != "false"

    val english = ValveItems.itemList(Language.EN)
    val keyToItem = english.filterNot { it.isRecipe }.filter { it.englishName.isNotBlank() }.groupBy { it.folderName }.mapValues { (_, list) -> list.minBy { it.id } }
    val idToFolder = english.associate { it.id to it.folderName }
    val recipes = english.filter { it.isRecipe }
    val refreshIcons = System.getenv("ITEMS_ICONS")?.lowercase() == "refresh"
    val meta = loadItemMeta()
    val neutralTiers = loadNeutralTiers()

    val inGame = keyToItem.filterValues { it.isInGame(meta[it.name], neutralTiers) }
    val upgradeIn = upgradeIn(recipes, inGame.values, idToFolder)
    val previousList = readExtras()
    val appFolders = (existingFolders + previousList.keys).distinct()
    val sections = sortedMapOf<String, String>()
    for ((folder, item) in inGame) {
        val previous = previousList[folder]?.takeUnless { it.startsWith(NEUTRAL_SECTION) }
        val section = neutralTiers[item.name]?.let { "$NEUTRAL_SECTION $it" }
            ?: renamed.values.firstOrNull { it.folder == folder }?.section
            ?: previous
            ?: "Miscellaneous".takeIf { folder !in existingFolders || previousList[folder] != null }
        if (section != null) sections[folder] = section
    }
    val removed = appFolders.filter { it !in inGame }.sorted()
    println("In the game: ${inGame.size}, listed with a section: ${sections.size}")
    println("New for the app (${inGame.keys.count { it !in appFolders }}): ${inGame.keys.filter { it !in appFolders }.joinToString()}")
    println("Removed from the game (${removed.size}): ${removed.joinToString()}")
    if (System.getenv("ITEMS_DRY_RUN") == "true") return

    saveFile(assetsDatabaseItems, "list.json", JSONArray().apply { sections.forEach { (name, type) -> add(JSONObject().apply { put("name", name); put("type", type) }) } }.toJSONString())
    // cumulative: an app that skipped several updates still has rows of items removed long ago
    val everRemoved = (readRemoved() + removed).filter { it !in inGame }.toSortedSet().toList()
    saveFile(assetsDatabaseItems, "removed.json", everRemoved.toJsonArray().toJSONString())

    val names = JSONObject()
    for ((folder, item) in inGame.toSortedMap()) {
        val existing = readExisting(folder) ?: renamed.entries.firstOrNull { it.value.folder == folder }?.let { readExisting(it.key) }
        names[folder] = item.englishName
        if (only.isNotEmpty() && folder !in only) continue
        saveIcon(folder, item.name, refreshIcons)

        val recipe = recipes.firstOrNull { it.name == "item_recipe_" + item.name.removePrefix("item_") }
        val components = recipe?.components?.mapNotNull(idToFolder::get)?.takeIf { it.isNotEmpty() }

        for (language in Language.entries) {
            val data = try {
                ValveItems.itemData(item.id, language)
            } catch (e: Exception) {
                println("  [$folder] ${language.folder}: $e")
                continue
            }
            val wiki = if (useWiki) runCatching { WikiItemParser.load(item.englishName, language) }.getOrNull() else null
            val previous = if (language == Language.EN) existing else readExisting(folder, language)
            val sources = Sources(meta[item.name], upgradeIn[folder].orEmpty(), wiki, previous)
            val json = buildItemJson(data, language, components, sources, existing)
            saveFile("$assetsDatabaseItems/$folder/${language.folder}", "description.json", json.toJSONString())
        }
        println("ok $folder")
    }
    saveFile(assetsDatabaseItems, "names.json", names.toJSONString())
    // after the loop: a renamed item reads sounds and wiki lists of its old folder
    deleteRemovedAssets(removed)
}

private const val NEUTRAL_SECTION = "Neutral Items Tier"

private class Renamed(val folder: String, val section: String)

/** Old app folder → the folder of the same game item under its current English name, with the section the old one had. */
private val renamed = mapOf(
    "blight_stone" to Renamed("orb_of_blight", "Equipment"),
    "aghanim's_shard_-_roshan" to Renamed("aghanim's_shard_-_consumable", "Boss Rewards"),
)

/** Roshan drops: not buyable, but in the game. */
private val bossRewards = setOf("item_aegis", "item_cheese", "item_refresher_shard", "item_ultimate_scepter_roshan", "item_aghanims_shard_roshan")

/** Buyable now, a Roshan drop, or in the current neutral pool. A neutral item that rotated out of the pool is not in the game. */
private fun ValveItems.ListItem.isInGame(meta: ItemMeta?, neutralTiers: Map<String, Int>): Boolean = when {
    meta == null || meta.obsolete -> false
    name in neutralTiers -> true
    meta.neutralDrop || neutralTier >= 0 -> false
    name in bossRewards -> true
    // free shop items (Observer Ward) have shop tags, event and unused leftovers do not
    else -> meta.purchasable && (meta.cost > 0 || meta.inShop)
}

/** `scripts/npc/neutral_items.txt`: valve name → tier (1-based) of the current neutral pool. */
private fun loadNeutralTiers(): Map<String, Int> {
    val root = parseKeyValues(ValveItems.download("$GAME_SCRIPTS/npc/neutral_items.txt"))
    val tiers = ((root["neutral_items"] as? Map<*, *>)?.get("neutral_tiers") as? Map<*, *>) ?: error("unexpected neutral_items.txt format")
    return tiers.flatMap { (tier, block) ->
        val items = (block as? Map<*, *>)?.get("items") as? Map<*, *> ?: return@flatMap emptyList()
        val number = (tier as String).toIntOrNull() ?: return@flatMap emptyList()
        items.keys.map { (it as String) to number }
    }.toMap()
}

/**
 * Hero pages show item icons by path (`items/<key>/full.webp`), so the icon of a removed item stays while any hero
 * description still points to it; everything else of the item folder goes.
 */
private fun deleteRemovedAssets(removed: List<String>) {
    val referenced = File(assetsDatabaseItems).parentFile.resolve("heroes").walkTopDown()
        .filter { it.name == "description.json" }
        .flatMap { file -> Regex("""items\\?/([^"\\/]+)\\?/full\.webp""").findAll(file.readText(Charsets.UTF_8)).map { it.groupValues[1] } }
        .toSet()
    for (folder in removed) {
        val directory = File(assetsDatabaseItems, folder)
        if (!directory.exists()) continue
        if (folder in referenced) {
            directory.listFiles()?.filter { it.name != "full.webp" }?.forEach { it.deleteRecursively() }
            println("  removed $folder (icon kept: a hero page shows it)")
        } else {
            directory.deleteRecursively()
            println("  removed $folder")
        }
    }
}

private fun readRemoved(): List<String> {
    val file = File(assetsDatabaseItems, "removed.json")
    if (!file.exists()) return emptyList()
    return (JSONParser().parse(file.readText(Charsets.UTF_8)) as JSONArray).filterIsInstance<String>()
}

private fun readExtras(): MutableMap<String, String> {
    val file = File(assetsDatabaseItems, "list.json")
    if (!file.exists()) return mutableMapOf()
    val list = JSONParser().parse(file.readText(Charsets.UTF_8)) as JSONArray
    return list.filterIsInstance<JSONObject>().associateTo(mutableMapOf()) { (it["name"] as String) to (it["type"] as String) }
}

/** Game file values of an item. Strings are the raw enum names, e.g. `SPELL_DISPELLABLE_YES` or `ITEM_FULLY_SHAREABLE`. */
private class ItemMeta(
    val purchasable: Boolean,
    val cost: Int,
    val obsolete: Boolean,
    /** Drops from neutral creeps in some patch; only [loadNeutralTiers] tells whether it is in the current pool. */
    val neutralDrop: Boolean,
    /** Has `ItemShopTags`: shown in the shop. */
    val inShop: Boolean,
    val secretShop: Boolean,
    val sellable: Boolean,
    val permanent: Boolean,
    val stackable: Boolean,
    val shareability: String?,
    val disassemble: String?,
    val behavior: Set<String>,
    val targetTeam: String?,
    val immunity: String?,
    val dispellable: String?,
)

/** Everything [buildItemJson] takes besides the Valve datafeed. [previous] is the current file of the same language. */
private class Sources(val meta: ItemMeta?, val upgradeIn: List<String>, val wiki: WikiItemParser.WikiItem?, val previous: JSONObject?)

/** Component folder → English names of the items its recipes produce, e.g. `ogre_axe` → `[Black King Bar, ...]`. */
private fun upgradeIn(recipes: List<ValveItems.ListItem>, items: Collection<ValveItems.ListItem>, idToFolder: Map<Long, String>): Map<String, List<String>> {
    val byValveName = items.associateBy { it.name }
    val result = mutableMapOf<String, MutableSet<String>>()
    for (recipe in recipes) {
        val crafted = byValveName["item_" + recipe.name.removePrefix("item_recipe_")] ?: continue
        recipe.components.mapNotNull(idToFolder::get).filterNot { it == crafted.folderName }.forEach { component ->
            result.getOrPut(component) { sortedSetOf() } += crafted.englishName
        }
    }
    return result.mapValues { it.value.toList() }
}

private const val GAME_SCRIPTS = "https://raw.githubusercontent.com/SteamDatabase/GameTracking-Dota2/master/game/dota/pak01_dir/scripts"

/** Valve KeyValues text (`"key" "value"` and `"key" { ... }`) → nested maps. */
private fun parseKeyValues(text: String): Map<String, Any> {
    val tokens = Regex("\"((?:[^\"\\\\]|\\\\.)*)\"|([{}])").findAll(text.replace(Regex("//[^\\n]*"), "")).iterator()
    fun readBlock(): Map<String, Any> {
        val map = LinkedHashMap<String, Any>()
        while (tokens.hasNext()) {
            val token = tokens.next()
            if (token.groups[2]?.value == "}") return map
            val key = token.groups[1]?.value ?: continue
            val value = tokens.next()
            map[key] = if (value.groups[2]?.value == "{") readBlock() else value.groups[1]?.value.orEmpty()
        }
        return map
    }
    return readBlock()
}

/** `scripts/npc/items.txt` of the game: tells buyable items from event/unused ones. */
private fun loadItemMeta(): Map<String, ItemMeta> {
    @Suppress("UNCHECKED_CAST")
    val items = parseKeyValues(ValveItems.download("$GAME_SCRIPTS/npc/items.txt")).values.firstOrNull() as? Map<String, Any> ?: error("unexpected items.txt format")
    return items.mapNotNull { (key, value) ->
        val block = value as? Map<*, *> ?: return@mapNotNull null
        fun string(name: String) = (block[name] as? String)?.trim()?.takeIf { it.isNotEmpty() }
        key to ItemMeta(
            purchasable = block["ItemPurchasable"] != "0",
            cost = string("ItemCost")?.toIntOrNull() ?: 0,
            obsolete = block["IsObsolete"] == "1",
            neutralDrop = block["ItemIsNeutralActiveDrop"] == "1" || block["ItemIsNeutralPassiveDrop"] == "1",
            inShop = string("ItemShopTags") != null,
            secretShop = block["SecretShop"] == "1",
            sellable = block["ItemSellable"] != "0",
            permanent = block["ItemPermanent"] != "0",
            stackable = block["ItemStackable"] == "1",
            shareability = string("ItemShareability"),
            disassemble = string("ItemDisassembleRule"),
            behavior = string("AbilityBehavior")?.split("|")?.map { it.trim().removePrefix("DOTA_ABILITY_BEHAVIOR_") }?.toSet().orEmpty(),
            targetTeam = string("AbilityUnitTargetTeam"),
            immunity = string("SpellImmunityType"),
            dispellable = string("SpellDispellableType"),
        )
    }.toMap()
}

/**
 * Icon from the Valve CDN (the one the game shop shows now). Downloaded when missing, or always with [refresh].
 * Stored as webp when ffmpeg exists, png bytes otherwise (Android decodes by content). On failure the old icon stays.
 */
private fun saveIcon(folder: String, valveName: String, refresh: Boolean) {
    val directory = File(assetsDatabaseItems, folder).apply { mkdirs() }
    val webp = File(directory, "full.webp")
    if (!refresh && webp.exists() && webp.length() > 0) return
    try {
        val bytes = ValveItems.downloadBytes("https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/items/${valveName.removePrefix("item_")}.png")
        val png = File(directory, "full.png").apply { writeBytes(bytes) }
        val converted = File(directory, "full.tmp.webp")
        val ok = try {
            val process = ProcessBuilder("ffmpeg", "-y", "-loglevel", "error", "-i", png.path, "-c:v", "libwebp", "-quality", "90", converted.path).redirectErrorStream(true).start()
            process.inputStream.readAllBytes()
            process.waitFor() == 0 && converted.length() > 0
        } catch (e: Exception) {
            false
        }
        (if (ok) converted else png).copyTo(webp, overwrite = true)
        png.delete()
        converted.delete()
    } catch (e: Exception) {
        println("  [$folder] no icon: $e")
    }
}

internal enum class Language(val folder: String, val valve: String) {
    EN("en", "english"),
    RU("ru", "russian"),
}

private fun readExisting(folder: String, language: Language = Language.EN): JSONObject? {
    val file = File("$assetsDatabaseItems/$folder/${language.folder}/description.json")
    if (!file.exists()) return null
    return runCatching { JSONParser().parse(file.readText(Charsets.UTF_8)) as JSONObject }.getOrNull()
}

private fun buildItemJson(data: JSONObject, language: Language, components: List<String>?, sources: Sources, sounds: JSONObject?): JSONObject {
    val values = (data["special_values"] as JSONArray).filterIsInstance<JSONObject>()
    val valueByName = values.associate { (it["name"] as String) to it.formatValues() }

    val abilities = parseAbilities(data["desc_loc"] as String, valueByName, data["name_loc"] as String)
    val cooldown = (data["cooldowns"] as JSONArray).formatPositive()
    val mana = (data["mana_costs"] as JSONArray).formatPositive()
    val active = abilities.firstOrNull { it.isActive } ?: abilities.firstOrNull()
    if (active != null) {
        active.json["cooldown"] = cooldown
        active.json["mana"] = mana
    }
    val notes = (data["notes_loc"] as JSONArray).map { it.toString().substitute(valueByName) }
    val oldAbilities = (sounds?.get("abilities") as? JSONArray)?.filterIsInstance<JSONObject>().orEmpty()
    abilities.forEachIndexed { index, ability ->
        ability.json["audioUrl"] = oldAbilities.getOrNull(index)?.get("audioUrl")
        if (index == 0 && notes.isNotEmpty()) ability.json["notes"] = JSONArray().apply { addAll(notes) }
    }

    val meta = sources.meta
    val passive = meta?.behavior?.contains("PASSIVE") == true
    val target = abilities.firstOrNull { it.isActive } ?: abilities.firstOrNull()?.takeIf { meta != null && !passive && !it.isPassive }
    val params = abilityParams(data, meta, language)
    if (target != null && params.isNotEmpty()) target.json["params"] = params.toJsonArray()

    val bonuses = values.mapNotNull { it.toBonus(language) }
    val wiki = sources.wiki
    val previous = sources.previous
    /** Fresh wiki lines first, then what the previous file of this language had. */
    fun wikiList(key: String, fresh: List<String>?): JSONArray? =
        fresh?.takeIf { it.isNotEmpty() }?.toJsonArray() ?: (previous?.get(key) as? JSONArray)?.dedupe()?.takeIf { it.isNotEmpty() }

    return JSONObject().apply {
        put("name", data["name_loc"])
        put("description", (data["lore_loc"] as? String)?.takeIf { it.isNotBlank() })
        put("cost", (data["item_cost"] as Number).toLong().takeIf { it > 0 }?.toString() ?: "0")
        put("boughtFrom", if (meta?.secretShop == true) language.pick("Secret Shop", "Секретная лавка") else null)
        put("consistFrom", components?.let { JSONArray().apply { addAll(it) } })
        put("upgradeIn", sources.upgradeIn.takeIf { it.isNotEmpty() }?.toJsonArray())
        put("bonuses", bonuses.takeIf { it.isNotEmpty() }?.let { JSONArray().apply { addAll(it) } })
        put("properties", itemProperties(data, meta, language).takeIf { it.isNotEmpty() }?.toJsonArray())
        put("abilities", JSONArray().apply { abilities.forEach { add(it.json) } })
        val valveInformation = notes.takeIf { abilities.isEmpty() && it.isNotEmpty() }
        put("additionalInformation", valveInformation?.toJsonArray() ?: wikiList("additionalInformation", wiki?.additionalInformation))
        put("tips", wikiList("tips", wiki?.tips))
        put("trivia", wikiList("trivia", wiki?.trivia))
        put("recommendedFor", wikiList("recommendedFor", wiki?.recommendedFor))
        put("lore", (previous?.get("lore") as? JSONArray)?.takeIf { it.isNotEmpty() })
    }
}

private fun List<String>.toJsonArray() = JSONArray().apply { addAll(this@toJsonArray) }

/** The old wiki parser repeated nested list items as separate lines: drops a line already contained in an earlier one. */
private fun JSONArray.dedupe(): JSONArray {
    val lines = filterIsInstance<String>().map { it.trim() }.filter { it.isNotEmpty() }
    return lines.filterIndexed { index, line -> lines.take(index).none { it != line && it.contains(line) } && lines.indexOf(line) == index }.toJsonArray()
}

private fun Language.pick(english: String, russian: String) = if (this == Language.EN) english else russian

/** Lines under the active ability: how it is cast and what it goes through. */
private fun abilityParams(data: JSONObject, meta: ItemMeta?, language: Language): List<String> = buildList {
    val behavior = meta?.behavior.orEmpty()
    if ("PASSIVE" !in behavior) {
        val kind = when {
            "UNIT_TARGET" in behavior || "OPTIONAL_UNIT_TARGET" in behavior -> language.pick("unit target", "на юнита")
            "POINT" in behavior || "OPTIONAL_POINT" in behavior -> language.pick("point target", "на точку")
            "NO_TARGET" in behavior -> language.pick("no target", "без цели")
            else -> null
        }
        val extras = listOfNotNull(
            language.pick("area", "по области").takeIf { "AOE" in behavior },
            language.pick("channelled", "поддерживаемая").takeIf { "CHANNELLED" in behavior },
            language.pick("toggle", "переключаемая").takeIf { "TOGGLE" in behavior },
            language.pick("autocast", "автоприменение").takeIf { "AUTOCAST" in behavior },
        )
        if (kind != null) add(language.pick("Cast", "Применение") + ": " + (listOf(kind) + extras).joinToString())
        when (meta?.targetTeam?.removePrefix("DOTA_UNIT_TARGET_TEAM_")) {
            "ENEMY" -> add(language.pick("Affects: Enemies", "Действует на: врагов"))
            "FRIENDLY" -> add(language.pick("Affects: Allies", "Действует на: союзников"))
            "BOTH" -> add(language.pick("Affects: Allies and enemies", "Действует на: союзников и врагов"))
        }
        (data["cast_ranges"] as? JSONArray)?.formatPositive()?.let { add(language.pick("Cast range", "Дальность применения") + ": $it") }
        (data["channel_times"] as? JSONArray)?.formatPositive()?.let { add(language.pick("Channel time: $it s", "Время поддержки: $it сек.")) }
        (data["health_costs"] as? JSONArray)?.formatPositive()?.let { add(language.pick("Health cost", "Затраты здоровья") + ": $it") }
    }
    val yes = language.pick("Yes", "да")
    val no = language.pick("No", "нет")
    val piercesEnemies = language.pick("Pierces spell immunity", "Проходит сквозь невосприимчивость к магии")
    val immuneAllies = language.pick("Works on spell immune allies", "Действует на невосприимчивых к магии союзников")
    when (meta?.immunity?.removePrefix("SPELL_IMMUNITY_")) {
        "ENEMIES_YES" -> add("$piercesEnemies: $yes")
        "ENEMIES_NO" -> add("$piercesEnemies: $no")
        "ALLIES_YES" -> add("$immuneAllies: $yes")
        "ALLIES_NO" -> add("$immuneAllies: $no")
        "ALLIES_YES_ENEMIES_NO" -> {
            add("$immuneAllies: $yes")
            add("$piercesEnemies: $no")
        }
    }
    val dispellable = language.pick("Dispellable", "Развеивается")
    when (meta?.dispellable?.removePrefix("SPELL_DISPELLABLE_")) {
        "YES" -> add("$dispellable: $yes")
        "YES_STRONG" -> add("$dispellable: " + language.pick("Strong dispels only", "только сильным развеиванием"))
        "NO" -> add("$dispellable: $no")
    }
}

/** Shop and inventory facts of the item itself. */
private fun itemProperties(data: JSONObject, meta: ItemMeta?, language: Language): List<String> = buildList {
    val tier = (data["item_neutral_tier"] as? Number)?.toLong() ?: -1
    if (tier in 0..9) add(language.pick("Neutral item, tier ${tier + 1}", "Нейтральный предмет, уровень ${tier + 1}"))
    val charges = (data["item_initial_charges"] as? Number)?.toLong() ?: 0
    if (charges > 0) add(language.pick("Initial charges: $charges", "Начальные заряды: $charges"))
    val stock = (data["item_stock_max"] as? Number)?.toLong() ?: 0
    if (stock > 0) {
        val time = (data["item_stock_time"] as? Number)?.toDouble()?.takeIf { it > 0 }?.pretty()
        add(
            language.pick("Shop stock: $stock", "Запас в лавке: $stock") +
                (time?.let { language.pick(", restock every $it s", ", пополнение каждые $it сек.") } ?: ""),
        )
    }
    if (meta == null) return@buildList
    if (meta.stackable) add(language.pick("Stacks in one slot", "Складывается в один слот"))
    if (!meta.permanent) add(language.pick("Disappears when out of charges", "Исчезает, когда заканчиваются заряды"))
    when (meta.shareability) {
        "ITEM_FULLY_SHAREABLE", "ITEM_FULLY_SHAREABLE_STACKING", "ITEM_PARTIALLY_SHAREABLE" -> add(language.pick("Can be shared with allies", "Можно передать союзнику"))
        "ITEM_NOT_SHAREABLE" -> add(language.pick("Cannot be shared with allies", "Нельзя передать союзнику"))
    }
    if (!meta.sellable) add(language.pick("Cannot be sold", "Нельзя продать"))
    when (meta.disassemble) {
        "DOTA_ITEM_DISASSEMBLE_ALWAYS" -> add(language.pick("Can be disassembled", "Можно разобрать"))
        "DOTA_ITEM_DISASSEMBLE_NEVER" -> add(language.pick("Cannot be disassembled", "Нельзя разобрать"))
    }
}

private class ParsedAbility(val json: JSONObject, val isActive: Boolean) {
    val isPassive: Boolean get() = (json["abilityName"] as? String).orEmpty().let { name -> listOf("Passive", "Пассивн").any { name.startsWith(it, ignoreCase = true) } }
}

/** `desc_loc` is `<h1>Active: Name</h1> text <h1>Passive: Name</h1> text`; text before the first header belongs to the item itself. */
private fun parseAbilities(desc: String, values: Map<String, String>, itemName: String): List<ParsedAbility> {
    val header = Regex("<h1>(.*?)</h1>", RegexOption.DOT_MATCHES_ALL)
    val result = mutableListOf<ParsedAbility>()
    val intro = desc.substringBefore("<h1>").cleanHtml(values)
    if (intro.isNotEmpty()) result += ability(itemName, intro, isActive = false)
    val matches = header.findAll(desc).toList()
    matches.forEachIndexed { index, match ->
        val end = matches.getOrNull(index + 1)?.range?.first ?: desc.length
        val title = match.groupValues[1].cleanHtml(values)
        val text = desc.substring(match.range.last + 1, end).cleanHtml(values)
        val active = listOf("Active", "Use", "Активн", "Использование").any { title.startsWith(it, ignoreCase = true) }
        result += ability(title, text, active)
    }
    return result
}

private fun ability(name: String, description: String, isActive: Boolean) = ParsedAbility(
    JSONObject().apply {
        put("abilityName", name)
        put("description", description)
        put("effects", JSONArray())
        put("notes", JSONArray())
        put("itemBehaviour", JSONArray())
    },
    isActive,
)

private fun String.cleanHtml(values: Map<String, String>): String = substitute(values)
    .replace("\n", "<br>")
    .replace(Regex("\\s*<br\\s*/?>\\s*", RegexOption.IGNORE_CASE), "<br>")
    .replace(Regex("^(<br>)+|(<br>)+$"), "")
    .trim()

/** `%name%` → value, `%%` → `%` (Valve tooltip syntax). */
private fun String.substitute(values: Map<String, String>): String = replace(Regex("%([A-Za-z0-9_]*)%")) { match ->
    val key = match.groupValues[1]
    if (key.isEmpty()) "%" else values[key] ?: values.entries.firstOrNull { it.key.equals(key, ignoreCase = true) }?.value ?: match.value
}

private fun JSONObject.formatValues(): String {
    val numbers = (this["values_float"] as? JSONArray).orEmpty().map { (it as Number).toDouble() }
    return numbers.distinct().joinToString("/") { it.pretty() }
}

private fun JSONArray.formatPositive(): String? = map { (it as Number).toDouble() }.filter { it > 0 }.distinct().takeIf { it.isNotEmpty() }?.joinToString("/") { it.pretty() }

private fun Double.pretty(): String = if (this == Math.floor(this)) toLong().toString() else "%.2f".format(java.util.Locale.US, this).trimEnd('0').trimEnd('.')

/** `heading_loc` like `+$attack` or `+к скорости передвижения` → `+25 к скорости атаки`. */
private fun JSONObject.toBonus(language: Language): String? {
    val heading = (this["heading_loc"] as? String)?.trim().orEmpty()
    if (heading.isEmpty()) return null
    val value = formatValues().ifEmpty { return null } + if (this["is_percentage"] == true) "%" else ""
    val sign = heading.firstOrNull()?.takeIf { it == '+' || it == '-' }?.toString() ?: ""
    val label = heading.removePrefix(sign).trim().replace(Regex("\\$([a-z_]+)")) { tokenLabel(it.groupValues[1], language) }
    return "$sign$value $label"
}

private fun tokenLabel(token: String, language: Language): String {
    val labels = mapOf(
        "str" to ("Strength" to "к силе"),
        "agi" to ("Agility" to "к ловкости"),
        "int" to ("Intelligence" to "к интеллекту"),
        "all" to ("All Attributes" to "ко всем атрибутам"),
        "selected_attrib" to ("Selected Attribute" to "к выбранному атрибуту"),
        "primary_attribute" to ("Primary Attribute" to "к основному атрибуту"),
        "attack" to ("Attack Speed" to "к скорости атаки"),
        "damage" to ("Damage" to "к урону"),
        "armor" to ("Armor" to "к броне"),
        "hp" to ("Health" to "к здоровью"),
        "health" to ("Health" to "к здоровью"),
        "mana" to ("Mana" to "к мане"),
        "hp_regen" to ("Health Regeneration" to "к восстановлению здоровья"),
        "mana_regen" to ("Mana Regeneration" to "к восстановлению маны"),
        "move_speed" to ("Movement Speed" to "к скорости передвижения"),
        "evasion" to ("Evasion" to "к уклонению"),
        "lifesteal" to ("Lifesteal" to "к вампиризму"),
        "spell_lifesteal" to ("Spell Lifesteal" to "к вампиризму от заклинаний"),
        "spell_amp" to ("Spell Amplification" to "к усилению заклинаний"),
        "spell_resist" to ("Magic Resistance" to "к сопротивлению магии"),
        "magic_resist" to ("Magic Resistance" to "к сопротивлению магии"),
        "status_resist" to ("Status Resistance" to "к сопротивлению эффектам"),
        "cast_range" to ("Cast Range" to "к дальности применения"),
        "attack_range" to ("Attack Range" to "к дальности атаки"),
        "base_attack_time" to ("Base Attack Time" to "к базовому времени атаки"),
        "manacost_reduction" to ("Mana Cost Reduction" to "к снижению затрат маны"),
        "cooldown_reduction" to ("Cooldown Reduction" to "к снижению перезарядки"),
        "debuff_amp" to ("Debuff Duration" to "к длительности негативных эффектов"),
        "heal_amp" to ("Heal Amplification" to "к усилению лечения"),
        "night_vision" to ("Night Vision" to "к ночному обзору"),
        "projectile_speed" to ("Projectile Speed" to "к скорости снарядов"),
        "slow_resist" to ("Slow Resistance" to "к сопротивлению замедлению"),
    )
    val pair = labels[token] ?: run {
        unknownTokens += token
        val text = token.replace('_', ' ').replaceFirstChar(Char::uppercaseChar)
        text to text
    }
    return if (language == Language.EN) pair.first else pair.second
}

private val unknownTokens = sortedSetOf<String>().also { set ->
    Runtime.getRuntime().addShutdownHook(Thread { if (set.isNotEmpty()) println("\nUnmapped bonus tokens: ${set.joinToString()}") })
}

private object ValveItems {

    private const val BASE_URL = "https://www.dota2.com/datafeed"

    private val client = HttpClient.newBuilder()
        .connectTimeout(Duration.ofSeconds(15))
        .followRedirects(HttpClient.Redirect.NORMAL)
        .build()

    class ListItem(val id: Long, val name: String, val englishName: String, val components: List<Long>, val neutralTier: Long) {
        val isRecipe = name.startsWith("item_recipe_")
        val folderName: String = englishName.replace(" ", "_").lowercase()
    }

    fun itemList(language: Language): List<ListItem> {
        val list = getJson("$BASE_URL/itemlist?language=${language.valve}").path("result", "data", "itemabilities") as JSONArray
        return list.filterIsInstance<JSONObject>().map { item ->
            val components = (item["recipes"] as? JSONArray)?.filterIsInstance<JSONObject>()?.firstOrNull()
                ?.let { (it["items"] as JSONArray).map { id -> (id as Number).toLong() } }.orEmpty()
            ListItem((item["id"] as Number).toLong(), item["name"] as String, item["name_english_loc"] as String, components, (item["neutral_item_tier"] as Number).toLong())
        }
    }

    fun download(url: String): String = String(downloadBytes(url), Charsets.UTF_8)

    fun downloadBytes(url: String): ByteArray {
        val request = HttpRequest.newBuilder(URI(url)).timeout(Duration.ofSeconds(60)).build()
        val response = client.send(request, HttpResponse.BodyHandlers.ofByteArray())
        if (response.statusCode() != 200) error("HTTP ${response.statusCode()} for $url")
        return response.body()
    }

    fun itemData(id: Long, language: Language): JSONObject {
        val items = getJson("$BASE_URL/itemdata?language=${language.valve}&item_id=$id").path("result", "data", "items") as JSONArray
        return items.firstOrNull() as? JSONObject ?: error("no data for item $id")
    }

    private fun getJson(url: String): JSONObject {
        val request = HttpRequest.newBuilder(URI(url))
            .timeout(Duration.ofSeconds(30))
            .header("Accept", "application/json")
            .build()
        var lastError: Exception? = null
        repeat(3) { attempt ->
            try {
                val response = client.send(request, HttpResponse.BodyHandlers.ofString(Charsets.UTF_8))
                if (response.statusCode() == 200) return JSONParser().parse(response.body()) as JSONObject
                lastError = IllegalStateException("HTTP ${response.statusCode()} for $url")
            } catch (e: Exception) {
                lastError = e
            }
            Thread.sleep(1000L * (attempt + 1))
        }
        throw lastError ?: IllegalStateException("failed $url")
    }

    private fun JSONObject.path(vararg keys: String): Any? = keys.fold(this as Any?) { node, key -> (node as? JSONObject)?.get(key) }
}
