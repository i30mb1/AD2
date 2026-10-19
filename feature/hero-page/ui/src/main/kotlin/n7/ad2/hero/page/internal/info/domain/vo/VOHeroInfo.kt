package n7.ad2.hero.page.internal.info.domain.vo

import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.HeroDescription

/**
 * Everything the "Info" tab shows, blocks without data are simply not drawn.
 * Abilities are split into groups in the order the game shows them.
 */
data class VOHeroInfo(
    val heroImageUrl: String,
    val description: HeroDescription,
    val abilityGroups: List<VOAbilityGroup>,
)

data class VOAbilityGroup(val group: Group, val abilities: List<Ability>) {
    enum class Group { INNATE, MAIN, EXTRA }
}
