package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.TalentLevel

private val basicKeys = listOf("Q", "W", "E")
private const val ULTIMATE_KEY = "R"

/**
 * Default keys by ability name, only for the usual layout of 3 basic abilities and an ultimate: the few heroes with
 * more slots have keys that depend on the slot, which the data does not have, so they get none.
 */
fun List<Ability>.hotkeys(): Map<String, String> {
    val basic = filter { it.kind == AbilityKind.BASIC }
    val ultimate = filter { it.kind == AbilityKind.ULTIMATE }
    if (basic.size != basicKeys.size || ultimate.size != 1) return emptyMap()
    return basic.map { it.name }.zip(basicKeys).toMap() + (ultimate.single().name to ULTIMATE_KEY)
}

data class VOAbilityTalent(val level: Int, val text: String)

/** Talents that change [ability]: the game names the ability in the talent text. */
fun List<TalentLevel>.about(ability: Ability): List<VOAbilityTalent> = sortedBy { it.level }.flatMap { talent ->
    listOf(talent.left, talent.right)
        .filter { text -> ability.name.isNotEmpty() && text.contains(ability.name) }
        .map { text -> VOAbilityTalent(talent.level, text) }
}
