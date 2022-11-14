package n7.ad2.games.internal.games.skillmp

import androidx.compose.runtime.Immutable
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.transform
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.repositories.HeroRepository
import n7.ad2.repositories.model.Ability
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

class GetRandomSkillUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    @Immutable
    data class Data(val iconUrl: String, val name: String, val manacost: String, val suggestsList: List<String>)

    operator fun invoke(): Flow<Data> {
        return heroRepository.getAllHeroes()
            .transform { list -> emit(list.random()) }
            .map { hero ->
                val localHero = heroRepository.getHero(hero.name)
                val info = heroRepository.getHeroDescription(localHero.name, AppLocale.English).single()
                val spell: Ability = info.abilities.first { it.mana != null && it.mana != "" }
                val spellMana: String = spell.mana ?: error("spell ${spell.name} mana is ${spell.mana}")
                val suggestsList = buildSet {
                    add(spellMana)
                    while (size < 4) {
                        val wrongSpellCost = spellMana.toInt() + Random.nextInt(-4..4) * 10
                        if (wrongSpellCost > 0) add(wrongSpellCost.toString())
                    }
                }
                Data(
                    HeroRepository.getFullUrlHeroSpell(spell.name),
                    localHero.name,
                    spellMana,
                    suggestsList.toList()
                )
            }
            .flowOn(dispatchers.IO)
    }

}