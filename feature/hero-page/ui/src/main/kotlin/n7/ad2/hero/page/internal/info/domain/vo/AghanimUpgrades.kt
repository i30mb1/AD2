package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.AbilityParam

/** What Aghanim's Scepter and Aghanim's Shard give the hero, each list is empty when the item changes nothing. */
data class VOAghanim(val scepter: List<VOUpgrade>, val shard: List<VOUpgrade>)

sealed interface VOUpgrade {
    val ability: Ability

    /** The item gives the hero one more ability. */
    data class NewAbility(override val ability: Ability) : VOUpgrade

    /** The item changes one of the hero's abilities, [values] are the numbers it changes. */
    data class Change(override val ability: Ability, val text: String, val values: List<VOUpgradeValue>) : VOUpgrade
}

/** [value] is the number without the upgrade, null when only the upgrade has it. */
data class VOUpgradeValue(val label: String, val value: String?, val change: String)

fun List<Ability>.aghanim(): VOAghanim = VOAghanim(
    scepter = upgrades(AbilityKind.SCEPTER, Ability::scepter, AbilityParam::scepter),
    shard = upgrades(AbilityKind.SHARD, Ability::shard, AbilityParam::shard),
)

private fun List<Ability>.upgrades(
    grantedKind: AbilityKind,
    text: (Ability) -> String?,
    upgradeValue: (AbilityParam) -> String?,
): List<VOUpgrade> = mapNotNull { ability ->
    val upgradeText = text(ability)
    when {
        ability.kind == grantedKind -> VOUpgrade.NewAbility(ability)
        upgradeText != null -> VOUpgrade.Change(
            ability = ability,
            text = upgradeText,
            values = ability.params.mapNotNull { param ->
                upgradeValue(param)?.let { change -> VOUpgradeValue(param.label, param.value, change.asChangeOf(param.value)) }
            },
        )
        else -> null
    }
}

/** With a value of its own the datafeed gives how much the upgrade adds, it reads better with a sign. */
private fun String.asChangeOf(value: String?): String = if (value != null && !startsWith("-") && !startsWith("+")) "+$this" else this
