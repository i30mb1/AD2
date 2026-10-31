package n7.ad2.hero.page.internal.info.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import javax.inject.Inject

class GetVOHeroDescriptionUseCase @Inject constructor(
    private val getHeroDescriptionUseCase: GetHeroDescriptionUseCase,
    private val getHeroByNameUseCase: GetHeroByNameUseCase,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(heroName: String): Flow<VOHeroInfo> = flow {
        val hero = getHeroByNameUseCase(heroName)
        val description = getHeroDescriptionUseCase(hero.name).single()
        // abilities Aghanim's Scepter or Shard give are shown with the other upgrades of these items
        val groups = description.abilities.groupBy { ability ->
            when (ability.kind) {
                AbilityKind.INNATE -> VOAbilityGroup.Group.INNATE
                AbilityKind.BASIC, AbilityKind.ULTIMATE -> VOAbilityGroup.Group.MAIN
                AbilityKind.EXTRA -> VOAbilityGroup.Group.EXTRA
                AbilityKind.SCEPTER, AbilityKind.SHARD -> null
            }
        }
        emit(
            VOHeroInfo(
                heroImageUrl = hero.avatarUrl,
                description = description,
                abilityGroups = VOAbilityGroup.Group.entries.mapNotNull { group ->
                    groups[group]?.let { abilities -> VOAbilityGroup(group, abilities) }
                },
            ),
        )
    }.flowOn(dispatchers.IO)
}
