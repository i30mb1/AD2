package n7.ad2.hero.page.internal.info.components

import androidx.annotation.StringRes
import androidx.compose.ui.graphics.Color
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.heroes.domain.model.AbilityBehavior
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.AttackType
import n7.ad2.heroes.domain.model.DamageType
import n7.ad2.heroes.domain.model.Dispellable
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.Role
import n7.ad2.heroes.domain.model.SpellImmunity
import n7.ad2.heroes.domain.model.TargetTeam

@get:StringRes
internal val HeroAttribute.label: Int
    get() = when (this) {
        HeroAttribute.STRENGTH -> R.string.attr_strength
        HeroAttribute.AGILITY -> R.string.attr_agility
        HeroAttribute.INTELLIGENCE -> R.string.attr_intelligence
        HeroAttribute.UNIVERSAL -> R.string.attr_universal
    }

internal val HeroAttribute.color: Color
    get() = when (this) {
        HeroAttribute.STRENGTH -> InfoColors.strength
        HeroAttribute.AGILITY -> InfoColors.agility
        HeroAttribute.INTELLIGENCE -> InfoColors.intelligence
        HeroAttribute.UNIVERSAL -> InfoColors.universal
    }

@get:StringRes
internal val AttackType.label: Int
    get() = when (this) {
        AttackType.MELEE -> R.string.attack_melee
        AttackType.RANGED -> R.string.attack_ranged
    }

@get:StringRes
internal val Role.label: Int
    get() = when (this) {
        Role.CARRY -> R.string.role_carry
        Role.SUPPORT -> R.string.role_support
        Role.NUKER -> R.string.role_nuker
        Role.DISABLER -> R.string.role_disabler
        Role.JUNGLER -> R.string.role_jungler
        Role.DURABLE -> R.string.role_durable
        Role.ESCAPE -> R.string.role_escape
        Role.PUSHER -> R.string.role_pusher
        Role.INITIATOR -> R.string.role_initiator
    }

/** null for basic abilities, they need no badge. */
@get:StringRes
internal val AbilityKind.label: Int?
    get() = when (this) {
        AbilityKind.BASIC -> null
        AbilityKind.ULTIMATE -> R.string.ability_kind_ultimate
        AbilityKind.INNATE -> R.string.ability_kind_innate
        AbilityKind.EXTRA -> R.string.ability_kind_extra
        AbilityKind.SCEPTER -> R.string.ability_kind_scepter
        AbilityKind.SHARD -> R.string.ability_kind_shard
    }

@get:StringRes
internal val AbilityBehavior.label: Int
    get() = when (this) {
        AbilityBehavior.PASSIVE -> R.string.behavior_passive
        AbilityBehavior.NO_TARGET -> R.string.behavior_no_target
        AbilityBehavior.UNIT_TARGET -> R.string.behavior_unit_target
        AbilityBehavior.POINT_TARGET -> R.string.behavior_point_target
        AbilityBehavior.AOE -> R.string.behavior_aoe
        AbilityBehavior.CHANNELLED -> R.string.behavior_channelled
        AbilityBehavior.TOGGLE -> R.string.behavior_toggle
        AbilityBehavior.DIRECTIONAL -> R.string.behavior_directional
        AbilityBehavior.AUTOCAST -> R.string.behavior_autocast
    }

@get:StringRes
internal val TargetTeam.label: Int
    get() = when (this) {
        TargetTeam.ALLIES -> R.string.target_allies
        TargetTeam.ENEMIES -> R.string.target_enemies
        TargetTeam.BOTH -> R.string.target_both
    }

@get:StringRes
internal val DamageType.label: Int
    get() = when (this) {
        DamageType.PHYSICAL -> R.string.damage_physical
        DamageType.MAGICAL -> R.string.damage_magical
        DamageType.PURE -> R.string.damage_pure
    }

internal val DamageType.color: Color
    get() = when (this) {
        DamageType.PHYSICAL -> InfoColors.physical
        DamageType.MAGICAL -> InfoColors.magical
        DamageType.PURE -> InfoColors.pure
    }

@get:StringRes
internal val SpellImmunity.label: Int
    get() = when (this) {
        SpellImmunity.YES -> R.string.yes
        SpellImmunity.NO -> R.string.no
        SpellImmunity.ALLIES_YES_ENEMIES_NO -> R.string.immunity_allies_only
    }

@get:StringRes
internal val Dispellable.label: Int
    get() = when (this) {
        Dispellable.YES -> R.string.yes
        Dispellable.STRONG -> R.string.dispellable_strong
        Dispellable.NO -> R.string.no
    }
