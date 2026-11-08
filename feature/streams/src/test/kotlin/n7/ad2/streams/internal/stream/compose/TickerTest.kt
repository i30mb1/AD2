package n7.ad2.streams.internal.stream.compose

import kotlin.random.Random
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

/** The chat flying over the video: which row a message takes, when and how fast. Widths are in pixels, time in nanoseconds. */
class TickerTest {

    private val second = 1_000_000_000L
    private val screen = 2_000f
    private fun ticker() = Ticker<String>(screenWidth = screen, gap = 100f)

    @Test
    fun `a quiet chat spreads over the rows instead of crawling in the top one`() {
        val ticker = ticker()
        val lanes = listOf(0L, 5L, 10L).map { at ->
            ticker.offer("hi", at * second)
            ticker.start(at * second) { 400f }.single().lane
        }
        assertEquals(listOf(0, 1, 2), lanes)
    }

    @Test
    fun `a busy chat opens more rows and moves faster`() {
        val quiet = ticker().apply { offer("one", 0) }.start(0) { 400f }.single()
        val busy = ticker()
        repeat(8) { busy.offer("message $it", 0) }

        val runs = busy.start(0) { 400f }

        assertEquals(5, runs.size)
        assertTrue("${runs.first().speed} > ${quiet.speed}", runs.first().speed > quiet.speed)
    }

    @Test
    fun `a busy chat shows what was written last`() {
        val ticker = ticker()
        repeat(20) { ticker.offer("message $it", 0) }

        val runs = ticker.start(0) { 400f }

        assertEquals((12..16).map { "message $it" }, runs.map { it.message })
    }

    @Test
    fun `a message waits a few seconds at most`() {
        val ticker = ticker()
        ticker.offer("old", 0)
        ticker.offer("new", 3 * second)
        // the rows are busy until then
        repeat(5) { lane -> ticker.offer("filler $lane", 0) }
        ticker.start(0) { screen }

        val later = ticker.start(9 * second) { 400f }

        assertTrue(later.none { it.message == "old" })
    }

    @Test
    fun `nine of ten messages reach the screen when the chat writes three a second`() {
        val random = Random(1)
        val ticker = ticker()
        var offered = 0
        var shown = 0
        var next = 0L
        var now = 0L
        while (now < 120 * second) {
            while (next <= now) {
                ticker.offer("m$offered", next)
                offered++
                next += (random.nextDouble(0.0, 2 / 3.0) * second).toLong()
            }
            shown += ticker.start(now) { 100f + random.nextInt(0, 900) }.size
            now += second / 60
        }
        assertTrue("$shown of $offered", shown >= offered * 0.9)
    }

    @Test
    fun `a faster message never runs into the slower one ahead of it`() {
        val ticker = ticker()
        ticker.offer("slow", 0)
        val slow = ticker.start(0) { 1_500f }.single()
        // when the slow one has just entered the chat gets busy, the next messages want to move fast
        val now = ((1_500f + 100f) / slow.speed * second).toLong() + second / 1000
        repeat(10) { ticker.offer("busy $it", now) }

        val fast = ticker.start(now) { 300f }.single { it.lane == slow.lane }

        // the moment the tail of the slow one leaves the screen the fast one is still a gap behind
        val slowLeaves = ((screen + 1_500f) / slow.speed * second).toLong()
        assertTrue(fast.x(screen, slowLeaves) >= 100f - 1)
    }
}
