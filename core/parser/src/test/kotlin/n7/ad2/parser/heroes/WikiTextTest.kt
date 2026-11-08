package n7.ad2.parser.heroes

import java.io.File
import n7.ad2.parser.heroes.wiki.WikiHeroParser
import n7.ad2.parser.items.WikiItemParser
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

/**
 * Text of dota2.fandom.com as a reader sees it on the page: what the wiki failed to render or what only makes sense
 * with a mouse (formulas, tooltips, reference marks) must not reach the app as markup.
 */
class WikiTextTest {

    private fun lines(html: String): List<String> = WikiHeroParser.parseCounters(Jsoup.parse(html)).flatMap { it.blocks }.flatMap { it.lines }

    // wiki_formulas.html: list items with formulas from the Invoker (EN) and Juggernaut (RU) pages, MathML with a TeX annotation
    @Test
    fun `formulas read as one line instead of MathML text with TeX after it`() {
        val formulas = lines(File("src/test/resources/wiki_formulas.html").readText())
        assertEquals(
            listOf(
                "6 × ∑ₙ₌₁ⁱ(0.98ⁿ), where i is the remaining intervals.",
                "Knockback distance: (6 × ⌊Quas × 20/9⌋) + (6∑ₙ₌₁ⁱ(0.98ⁿ))",
                "Это можно определить как T = (100 × 1,4)/(∑СА × 1,5)",
            ),
            formulas,
        )
    }

    @Test
    fun `tooltips, reference marks and what the wiki failed to render are cut out`() {
        val html = """
            <h2><span class="mw-headline">Bad against...</span></h2>
            <ul>
            <li>Her low health makes her susceptible to {A|Brain Sap|Bane}} and Fiend's Grip.</li>
            <li>Skilled Luna can buy either {I|Gleipnir}} to prevent Earthshaker from blinking.</li>
            <li>Heroes who want to push lanes: {H|Luna}}, Pugna, Jakiro.</li>
            <li>Illusions, Boar, &amp;nbsp; Eidolon, &amp;nbsp; Forged Spirit and Warlock Golem.</li>
            <li>Weakens enemy magic resistance by 20%% for 3 seconds.</li>
            <li>Increases the total magic resistance by 0.6%. [<span class="tooltip" title="explanation">?</span>]</li>
            <li>From Latin lux<sup class="reference">[1]</sup> and fero[2].</li>
            <li>Блокируется как один барьер с [[Сопротивление магии}комбинированной плотностью]].</li>
            <li>Thirst's trigger response sm2&gt;vo_bloodseeker_blod_ability_thirst_03.mp3&lt;/sm2&gt; "Blood is in the air." is a reference.</li>
            <li>Increases its damage per second by 25 (<img alt="Shard">/<img alt="Talent">) for its duration.</li>
            </ul>
        """.trimIndent()
        assertEquals(
            listOf(
                "Her low health makes her susceptible to Brain Sap and Fiend's Grip.",
                "Skilled Luna can buy either Gleipnir to prevent Earthshaker from blinking.",
                "Heroes who want to push lanes: Luna, Pugna, Jakiro.",
                "Illusions, Boar, Eidolon, Forged Spirit and Warlock Golem.",
                "Weakens enemy magic resistance by 20% for 3 seconds.",
                "Increases the total magic resistance by 0.6%.",
                "From Latin lux and fero.",
                "Блокируется как один барьер с комбинированной плотностью.",
                "Thirst's trigger response \"Blood is in the air.\" is a reference.",
                "Increases its damage per second by 25 for its duration.",
            ),
            lines(html),
        )
    }

    // in 2026 the EN wiki renders some icons as their file names and breaks the paragraph around them (Doom/Guide)
    @Test
    fun `icons the wiki failed to draw leave no file names, a broken paragraph is one line again`() {
        val html = """
            <h2><span class="mw-headline">Doom</span></h2>
            <ul>
            <li>Batrider can push Arc Warden and File:ArcWardenUltPng.jpeg Tempest Double out of the Magnetic Field.</li>
            <li>The strongest counters to Doom are <span class="image-link">[[<p>File:Linken's Sphere icon.png</p></span></li></ul>
            <p class="mw-empty-elt"></p><p>|20px|link=Linken's Sphere|class=noprint]] <a href="/wiki/Linken%27s_Sphere" title="Linken's Sphere">Linken's Sphere</a> and Lotus Orb</p>
            <p>, which can cause the ultimate to be wasted.</p>
            <p>Break the Linken's Sphere first.</p>
        """.trimIndent()
        assertEquals(
            listOf(
                "Batrider can push Arc Warden and Tempest Double out of the Magnetic Field.",
                "The strongest counters to Doom are Linken's Sphere and Lotus Orb, which can cause the ultimate to be wasted.",
                "Break the Linken's Sphere first.",
            ),
            lines(html),
        )
    }

    @Test
    fun `trivia is cleaned the same way`() {
        val html = """
            <h2 id="Trivia">Trivia</h2>
            <ul>
            <li>Thirst's trigger response sm2&gt;vo_bloodseeker_blod_ability_thirst_03.mp3&lt;/sm2&gt; "Blood is in the air." is a reference.</li>
            <li>Her Spiderlings came from Anub'arak ( Nyx Assassin). Scream of Pain is based on this , while Sonic Wave is not. [<span class="tooltip" title="explanation">?</span>]</li>
            </ul>
        """.trimIndent()
        assertEquals(
            listOf(
                "Thirst's trigger response \"Blood is in the air.\" is a reference.",
                "Her Spiderlings came from Anub'arak (Nyx Assassin). Scream of Pain is based on this, while Sonic Wave is not.",
            ),
            parseTrivia(Jsoup.parse(html)),
        )
    }

    @Test
    fun `item pages are cleaned the same way`() {
        val html = """
            <h2><span class="mw-headline">Tips</span></h2>
            <ul><li>Weakens magic resistance by 20%% [<span class="tooltip" title="explanation">?</span>], from Latin lux[1].</li></ul>
        """.trimIndent()
        assertEquals(listOf("Weakens magic resistance by 20%, from Latin lux."), WikiItemParser.parse(Jsoup.parse(html)).tips)
    }
}
