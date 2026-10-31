package n7.ad2.hero.page.internal.guides.domain.adapter

import n7.ad2.hero.page.internal.guides.domain.vo.VOSkillRow
import n7.ad2.heroes.domain.model.SkillUpgrade
import org.junit.Test
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull

class SkillBuildAdapterTest {

    private fun upgrade(key: String) = SkillUpgrade(
        key,
        "url/$key.png",
        when {
            key == "attributes" -> SkillUpgrade.Kind.ATTRIBUTES
            key.startsWith("special_bonus") -> SkillUpgrade.Kind.TALENT
            else -> SkillUpgrade.Kind.ABILITY
        },
    )

    private val build = listOf("fury", "dance", "fury", "special_bonus_1", "ward", "fury", "attributes").map(::upgrade)

    @Test
    fun `one row per ability in order of the first point, levels are one based`() {
        val grid = build.toVOSkillBuild()!!
        assertEquals(listOf("url/fury.png", "url/dance.png", "url/ward.png"), grid.rows.filter { it.kind == VOSkillRow.Kind.ABILITY }.map { it.imageUrl })
        assertEquals(setOf(1, 3, 6), grid.rows.first().levels)
        assertEquals(7, grid.levels)
    }

    @Test
    fun `talents and attribute points get their own rows after the abilities`() {
        val grid = build.toVOSkillBuild()!!
        assertEquals(listOf(VOSkillRow.Kind.TALENT, VOSkillRow.Kind.ATTRIBUTES), grid.rows.takeLast(2).map { it.kind })
        assertEquals(setOf(4), grid.rows[grid.rows.size - 2].levels)
        assertEquals(setOf(7), grid.rows.last().levels)
    }

    @Test
    fun `no build without upgrades`() {
        assertNull(emptyList<SkillUpgrade>().toVOSkillBuild())
    }
}
