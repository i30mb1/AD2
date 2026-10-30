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
 * Rebuilds `items/<key>/<en|ru>/description.json` and `items/names.json` from the official
 * Valve datafeed (current patch, RU + EN) for every item folder that already exists in assets.
 *
 * Wiki-only fields (tips, trivia, lore, additional information, ability sounds) are kept from the
 * existing English file and are not copied into the Russian one, same as for heroes.
 */
fun main() {
    val existingFolders = File(assetsDatabaseItems).listFiles { file -> file.isDirectory }?.map { it.name }?.sorted().orEmpty()
    val only = System.getenv("ITEMS")?.split(",")?.map { it.trim().lowercase().replace(" ", "_") }?.filter { it.isNotEmpty() }.orEmpty()

    val english = ValveItems.itemList(Language.EN)
    val keyToItem = english.filterNot { it.isRecipe }.filter { it.englishName.isNotBlank() }.groupBy { it.folderName }.mapValues { (_, list) -> list.minBy { it.id } }
    val idToFolder = english.associate { it.id to it.folderName }
    val recipes = english.filter { it.isRecipe }

    // items the prepackaged items.db (2023) does not know: the app adds them from list.json
    val extras = readExtras()
    val meta = loadItemMeta()
    for (item in keyToItem.values) {
        if (item.folderName in existingFolders || item.folderName in extras) continue
        item.listType(meta[item.name])?.let { type -> extras[item.folderName] = type }
    }
    saveFile(assetsDatabaseItems, "list.json", JSONArray().apply { extras.toSortedMap().forEach { (name, type) -> add(JSONObject().apply { put("name", name); put("type", type) }) } }.toJSONString())
    println("Items added to the list (${extras.size}): ${extras.keys.joinToString()}")

    val folders = (existingFolders + extras.keys).distinct().sorted()
    val names = JSONObject()
    val missing = mutableListOf<String>()
    for (folder in folders) {
        val item = keyToItem[folder]
        val existing = readExisting(folder)
        if (item == null) {
            missing += folder
            (existing?.get("name") as? String)?.let { names[folder] = it }
            continue
        }
        names[folder] = item.englishName
        if (only.isNotEmpty() && folder !in only) continue
        saveIcon(folder, item.name)

        val recipe = recipes.firstOrNull { it.name == "item_recipe_" + item.name.removePrefix("item_") }
        val components = recipe?.components?.mapNotNull(idToFolder::get)?.takeIf { it.isNotEmpty() }

        for (language in Language.entries) {
            val data = try {
                ValveItems.itemData(item.id, language)
            } catch (e: Exception) {
                println("  [$folder] ${language.folder}: $e")
                continue
            }
            val json = buildItemJson(data, language, components, existing.takeIf { language == Language.EN }, existing)
            saveFile("$assetsDatabaseItems/$folder/${language.folder}", "description.json", json.toJSONString())
        }
        println("ok $folder")
    }
    saveFile(assetsDatabaseItems, "names.json", names.toJSONString())

    if (missing.isNotEmpty()) println("\nNo Valve item for ${missing.size} folders (English file left as is): ${missing.joinToString()}")
}

private fun readExtras(): MutableMap<String, String> {
    val file = File(assetsDatabaseItems, "list.json")
    if (!file.exists()) return mutableMapOf()
    val list = JSONParser().parse(file.readText(Charsets.UTF_8)) as JSONArray
    return list.filterIsInstance<JSONObject>().associateTo(mutableMapOf()) { (it["name"] as String) to (it["type"] as String) }
}

/** Section of an item that is not in the app yet, null when it is not a real item (not buyable, obsolete, event, ...). */
private fun ValveItems.ListItem.listType(meta: ItemMeta?): String? = when {
    meta == null || meta.obsolete -> null
    neutralTier >= 0 -> "Neutral Items Tier ${neutralTier + 1}"
    meta.purchasable && meta.cost > 0 -> "Miscellaneous"
    else -> null
}

private class ItemMeta(val purchasable: Boolean, val cost: Int, val obsolete: Boolean)

/** `scripts/npc/items.txt` of the game: tells buyable items from event/unused ones. */
private fun loadItemMeta(): Map<String, ItemMeta> {
    val text = ValveItems.download("https://raw.githubusercontent.com/SteamDatabase/GameTracking-Dota2/master/game/dota/pak01_dir/scripts/npc/items.txt")
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
    @Suppress("UNCHECKED_CAST")
    val items = readBlock().values.firstOrNull() as? Map<String, Any> ?: error("unexpected items.txt format")
    return items.mapNotNull { (key, value) ->
        val block = value as? Map<*, *> ?: return@mapNotNull null
        key to ItemMeta(
            purchasable = block["ItemPurchasable"] != "0",
            cost = (block["ItemCost"] as? String)?.toIntOrNull() ?: 0,
            obsolete = block["IsObsolete"] == "1",
        )
    }.toMap()
}

/** Items that are new for the app have no picture yet. Stored as webp when ffmpeg exists, png bytes otherwise (Android decodes by content). */
private fun saveIcon(folder: String, valveName: String) {
    val directory = File(assetsDatabaseItems, folder).apply { mkdirs() }
    val webp = File(directory, "full.webp")
    if (webp.exists() && webp.length() > 0) return
    try {
        val bytes = ValveItems.downloadBytes("https://cdn.cloudflare.steamstatic.com/apps/dota2/images/dota_react/items/${valveName.removePrefix("item_")}.png")
        val png = File(directory, "full.png").apply { writeBytes(bytes) }
        val converted = try {
            val process = ProcessBuilder("ffmpeg", "-y", "-loglevel", "error", "-i", png.path, "-c:v", "libwebp", "-quality", "90", webp.path).redirectErrorStream(true).start()
            process.inputStream.readAllBytes()
            process.waitFor() == 0 && webp.length() > 0
        } catch (e: Exception) {
            false
        }
        if (converted) png.delete() else png.renameTo(webp)
    } catch (e: Exception) {
        println("  [$folder] no icon: $e")
    }
}

internal enum class Language(val folder: String, val valve: String) {
    EN("en", "english"),
    RU("ru", "russian"),
}

private fun readExisting(folder: String): JSONObject? {
    val file = File("$assetsDatabaseItems/$folder/en/description.json")
    if (!file.exists()) return null
    return runCatching { JSONParser().parse(file.readText(Charsets.UTF_8)) as JSONObject }.getOrNull()
}

private fun buildItemJson(data: JSONObject, language: Language, components: List<String>?, wiki: JSONObject?, sounds: JSONObject?): JSONObject {
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

    val bonuses = values.mapNotNull { it.toBonus(language) }

    return JSONObject().apply {
        put("name", data["name_loc"])
        put("description", (data["lore_loc"] as? String)?.takeIf { it.isNotBlank() })
        put("cost", (data["item_cost"] as Number).toLong().takeIf { it > 0 }?.toString() ?: "0")
        put("consistFrom", components?.let { JSONArray().apply { addAll(it) } })
        put("bonuses", bonuses.takeIf { it.isNotEmpty() }?.let { JSONArray().apply { addAll(it) } })
        put("abilities", JSONArray().apply { abilities.forEach { add(it.json) } })
        if (abilities.isEmpty() && notes.isNotEmpty()) put("additionalInformation", JSONArray().apply { addAll(notes) })
        if (wiki != null) {
            for (key in listOf("tips", "trivia", "lore", "additionalInformation")) {
                if (this[key] == null) put(key, wiki[key])
            }
        }
    }
}

private class ParsedAbility(val json: JSONObject, val isActive: Boolean)

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
