package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.TalentLevel
import org.junit.Assert.assertEquals
import org.junit.Test

class AbilityKeysTest {

    private fun ability(name: String, kind: AbilityKind = AbilityKind.BASIC) = Ability(name = name, kind = kind)

    @Test
    fun `the usual layout gets Q W E for basic abilities and R for the ultimate`() {
        val abilities = listOf(
            ability("Blade Fury"),
            ability("Healing Ward"),
            ability("Blade Dance"),
            ability("Swiftslash", AbilityKind.SCEPTER),
            ability("Bladeform", AbilityKind.INNATE),
            ability("Omnislash", AbilityKind.ULTIMATE),
        )
        assertEquals(
            mapOf("Blade Fury" to "Q", "Healing Ward" to "W", "Blade Dance" to "E", "Omnislash" to "R"),
            abilities.hotkeys(),
        )
    }

    @Test
    fun `heroes with more slots get no keys, theirs depend on the slot`() {
        val darkWillow = listOf(
            ability("Bramble Maze"),
            ability("Shadow Realm"),
            ability("Cursed Crown"),
            ability("Bedlam", AbilityKind.ULTIMATE),
            ability("Terrorize", AbilityKind.ULTIMATE),
        )
        assertEquals(emptyMap<String, String>(), darkWillow.hotkeys())
    }

    @Test
    fun `talents of an ability are the ones naming it, lowest level first`() {
        val talents = listOf(
            TalentLevel(25, left = "40% Blade Dance lifesteal", right = "+1s Omnislash duration"),
            TalentLevel(20, left = "+120 Blade Fury DPS", right = "+15% Blade Dance crit"),
            TalentLevel(10, left = "-1s Bladeform interval", right = "-12s Healing Ward cooldown"),
        )
        assertEquals(
            listOf(VOAbilityTalent(20, "+15% Blade Dance crit"), VOAbilityTalent(25, "40% Blade Dance lifesteal")),
            talents.about(ability("Blade Dance")),
        )
    }
}
