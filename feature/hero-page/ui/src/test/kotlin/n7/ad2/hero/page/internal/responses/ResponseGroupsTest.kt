package n7.ad2.hero.page.internal.responses

import org.junit.Assert.assertEquals
import org.junit.Test

class ResponseGroupsTest {

    // the list is: header, 2 lines, header, 1 line, header, 3 lines
    private val sizes = listOf(2, 1, 3)

    @Test
    fun `a chip scrolls to the header of its category`() {
        assertEquals(listOf(0, 3, 5), sizes.indices.map { headerIndex(sizes, it) })
    }

    @Test
    fun `the highlighted chip is the category the top of the list is in`() {
        assertEquals(listOf(0, 0, 0, 1, 1, 2, 2, 2, 2), (0..8).map { groupAt(sizes, it) })
    }
}
