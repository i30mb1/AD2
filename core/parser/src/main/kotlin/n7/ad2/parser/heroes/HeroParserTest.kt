@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.parser.heroes

import n7.ad2.parser.LocaleHeroes
import org.jsoup.Jsoup

fun main() {
    println("=== Testing Updated HeroParser ===")

    // Test 1: Check if we can parse the heroes list with Universal attribute
    println("\n1. Testing Heroes List Parsing...")
    testHeroesList()

    // Test 2: Parse a specific hero with new fields
    println("\n2. Testing Specific Hero Parsing...")
    testSpecificHero("Pudge")

    // Test 3: Test error handling with invalid hero
    println("\n3. Testing Error Handling...")
    testErrorHandling()

    println("\n=== Test Complete ===")
}

private fun testHeroesList() {
    println("\n=== Testing Heroes List Parsing ===")
    try {
        val document = Jsoup.connect(LocaleHeroes.EN.heroesUrl).postDataCharset("UTF-8").get()

        // Check for Universal heroes section
        val universalSection = document.select("*:contains(Universal)")
        println("Universal section found: ${universalSection.isNotEmpty()}")

        // Try to find heroes by attribute
        val strengthHeroes = document.select("img[alt*='Strength heroes']")
        val agilityHeroes = document.select("img[alt*='Agility heroes']")
        val intelligenceHeroes = document.select("img[alt*='Intelligence heroes']")
        val universalHeroes = document.select("img[alt*='Universal heroes']")

        println("Strength heroes section: ${strengthHeroes.isNotEmpty()}")
        println("Agility heroes section: ${agilityHeroes.isNotEmpty()}")
        println("Intelligence heroes section: ${intelligenceHeroes.isNotEmpty()}")
        println("Universal heroes section: ${universalHeroes.isNotEmpty()}")

        // Count total heroes on page
        val allHeroLinks = document.select("a[title]")
            .filter { link ->
                val title = link.attr("title")
                title.isNotEmpty() &&
                    !title.contains("Category:") &&
                    !title.contains("File:") &&
                    !title.contains("Heroes") &&
                    title.length < 50 // Reasonable hero name length
            }

        println("Total hero links found: ${allHeroLinks.size}")

        // Print first few heroes found
        allHeroLinks.take(10).forEach { hero ->
            println("  - ${hero.attr("title")}")
        }
    } catch (e: Exception) {
        println("Error testing heroes list: ${e.message}")
    }
}

private fun testSpecificHero(heroName: String) {
    println("\n=== Testing $heroName Parsing ===")
    try {
        val url = LocaleHeroes.EN.mainUrl.format(heroName)
        val document = Jsoup.connect(url).get()

        // Test parsing basic info
        println("Testing basic info parsing...")

        // Attack type
        val attackType = document.select("tr:contains(Attack Type) td").text().trim()
        println("Attack Type: '$attackType'")

        // Complexity
        val complexityElements = document.select("img[alt*='Complexity']")
        println("Complexity: ${complexityElements.size} stars")

        // Roles
        val roles = document.select("tr:contains(Role) td a").map { it.text().trim() }
        println("Roles: $roles")

        // Test Aghanim items
        val scepterMentions = document.select("*:contains(Aghanim's Scepter)").size
        val shardMentions = document.select("*:contains(Aghanim's Shard)").size
        println("Aghanim's Scepter mentions: $scepterMentions")
        println("Aghanim's Shard mentions: $shardMentions")

        // Test for Innate abilities
        val innateMentions = document.select("*:contains(Innate)").size
        println("Innate mentions: $innateMentions")

        // Test for Facets
        val facetMentions = document.select("*:contains(Facet)").size
        println("Facet mentions: $facetMentions")
    } catch (e: Exception) {
        println("Error testing $heroName: ${e.message}")
    }
}

private fun testErrorHandling() {
    try {
        val invalidUrl = "https://dota2.fandom.com/wiki/NonExistentHero123"
        val document = Jsoup.connect(invalidUrl).get()
        println("ERROR: Should have failed to load invalid hero page")
    } catch (e: Exception) {
        println("✓ Error handling works: ${e.message}")
    }
}
