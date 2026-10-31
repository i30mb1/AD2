package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.AbilityParam
import org.junit.Assert.assertEquals
import org.junit.Test

class AghanimUpgradesTest {

    private val bladeFury = Ability(name = "Blade Fury")
    private val healingWard = Ability(
        name = "Healing Ward",
        shard = "Increases ward heal and health.",
        params = listOf(
            AbilityParam("HEAL PER SECOND", value = "2%/3%/4%/5%", shard = "1.5%"),
            AbilityParam("RADIUS", value = "400"),
            AbilityParam("BONUS ATTACKS", value = null, shard = "1"),
            AbilityParam("COOLDOWN CUT", value = "60", shard = "-20"),
        ),
    )
    private val swiftslash = Ability(name = "Swiftslash", kind = AbilityKind.SCEPTER)

    @Test
    fun `a granted ability is new, a changed one keeps its text and changed values`() {
        val aghanim = listOf(bladeFury, healingWard, swiftslash).aghanim()

        assertEquals(listOf(VOUpgrade.NewAbility(swiftslash)), aghanim.scepter)
        assertEquals(
            listOf(
                VOUpgrade.Change(
                    ability = healingWard,
                    text = "Increases ward heal and health.",
                    values = listOf(
                        // with a value of its own the number is how much the upgrade adds
                        VOUpgradeValue("HEAL PER SECOND", "2%/3%/4%/5%", "+1.5%"),
                        // without one it is the value the upgrade brings
                        VOUpgradeValue("BONUS ATTACKS", null, "1"),
                        VOUpgradeValue("COOLDOWN CUT", "60", "-20"),
                    ),
                ),
            ),
            aghanim.shard,
        )
    }

    @Test
    fun `a hero without upgrades gets empty lists`() {
        assertEquals(VOAghanim(emptyList(), emptyList()), listOf(bladeFury).aghanim())
    }
}
