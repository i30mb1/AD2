package n7.ad2.parser.heroes

import org.json.simple.JSONObject
import org.jsoup.Jsoup
import org.junit.Assert.assertEquals
import org.junit.Test

/** Voice lines of a "<Hero>/Responses" ("/Реплики") wiki page, list items as the pages have them in 2026. */
class ResponsesParserTest {

    private val page = """
        <div class="mw-parser-output">
        <h2><span class="mw-headline">Loadout</span><span class="mw-editsection">[edit]</span></h2>
        <ul>
        <li><a class="ext-audiobutton" data-state="error" title="File not found"></a> The spectacular, the unbelievable...Ringmaster.</li>
        <li><span><audio hidden="" class="ext-audiobutton"><source src="https://wiki/vo_ringmaster_01.mp3" type="audio/mpeg">
        <a href="https://wiki/vo_ringmaster_01.mp3">Link</a></audio><a class="ext-audiobutton" data-state="play" title="Play/Pause">▶️</a></span>
        Gaze and know true wonder.</li>
        <li><span><audio hidden=""><a href="https://wiki/vo_ringmaster_02.mp3">Link</a></audio><a title="Play/Pause">▶️</a></span> Gaze and know true wonder.</li>
        <li><span><audio hidden=""><a href="https://wiki/vo_juggernaut_01.mp3">Link</a></audio><a title="Play/Pause">▶️</a></span>
        <span><audio hidden=""><a href="https://wiki/vo_juggernaut_arcana_01.mp3">Link</a></audio><a title="Play/Pause">▶️</a></span> Yurnero here.</li>
        </ul>
        <h2><span class="mw-headline">Drafting</span></h2>
        <p><i>Picked</i></p>
        <h2><span class="mw-headline">Taunt</span></h2>
        <ul><li><a class="ext-audiobutton" data-state="error" title="File not found"></a> Step right up.</li></ul>
        </div>
    """.trimIndent()

    @Test
    fun `only lines with a sound, the arcana sound under the same key, no empty categories`() {
        // the same text twice is two takes of a line, the arcana voice is the second sound of one line
        val categories = parseResponses(Jsoup.parse(page)).map { it as JSONObject }

        assertEquals(listOf("Loadout"), categories.map { it["category"] })
        val lines = (categories.single()["responses"] as List<*>).map { it as JSONObject }
        assertEquals(
            listOf(
                mapOf("audio_url" to "https://wiki/vo_ringmaster_01.mp3", "title" to "Gaze and know true wonder.", "isArcane" to null),
                mapOf("audio_url" to "https://wiki/vo_ringmaster_02.mp3", "title" to "Gaze and know true wonder.", "isArcane" to null),
                mapOf("audio_url" to "https://wiki/vo_juggernaut_01.mp3", "title" to "Yurnero here.", "isArcane" to null),
                mapOf("audio_url" to "https://wiki/vo_juggernaut_arcana_01.mp3", "title" to "Yurnero here.", "isArcane" to true),
            ),
            lines.map { line -> mapOf("audio_url" to line["audio_url"], "title" to line["title"], "isArcane" to line["isArcane"]) },
        )
    }

    // "It's not the size of the sword, Sven" has Sven's minimap icon in the line
    @Test
    fun `the hero a line is about keeps the icon of the app`() {
        val assets = java.nio.file.Files.createTempDirectory("assets").toFile().apply {
            java.io.File(this, "heroes/sven").mkdirs()
            java.io.File(this, "heroes/sven/minimap.webp").writeText("webp")
        }
        val html = """
            <div class="mw-parser-output">
            <h2><span class="mw-headline">Rivals</span></h2>
            <ul><li><span><audio hidden=""><a href="https://wiki/vo_juggernaut_rival_01.mp3">Link</a></audio><a title="Play/Pause">▶️</a></span>
            <span class="pixelart" typeof="mw:File"><a href="/wiki/Sven" title="Sven"><img alt="Sven minimap icon"></a></span>
            <span class="pixelart" typeof="mw:File"><a href="/wiki/Kez" title="Kez"><img alt="Kez minimap icon"></a></span> It's not the size of the sword, Sven.</li></ul>
            </div>
        """.trimIndent()

        val line = ((parseResponses(Jsoup.parse(html), assets).single() as JSONObject)["responses"] as List<*>).single() as JSONObject

        assertEquals("It's not the size of the sword, Sven.", line["title"])
        assertEquals(listOf("heroes/sven/minimap.webp"), line["icons"])
    }

    // e.g. the RU page of Kez is the template only, its headings have no lines
    @Test
    fun `a page without lines gives nothing to save`() {
        val template = """
            <div class="mw-parser-output">
            <h2><span class="mw-headline">Снаряжение</span></h2>
            <h2><span class="mw-headline">Стадия планирования</span></h2>
            <p><i>Выбран</i></p>
            </div>
        """.trimIndent()
        assertEquals(0, parseResponses(Jsoup.parse(template)).size)
    }
}
