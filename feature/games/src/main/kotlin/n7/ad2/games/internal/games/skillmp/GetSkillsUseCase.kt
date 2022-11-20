package n7.ad2.games.internal.games.skillmp

import android.graphics.Color
import androidx.palette.graphics.Palette
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

internal class GetSkillsUseCase @Inject constructor(
    private val heroRepository: HeroRepository,
    private val dispatchers: DispatchersProvider,
) {

    data class Data(
        val skillImage: String,
        val name: String,
        val manacost: String,
        val backgroundColor: Int,
        val suggestsSpellList: List<Spell>,
    )

    data class Spell(
        val cost: String,
        val isRightAnswer: Boolean,
    )

    operator fun invoke(): Flow<Data> {
        return heroRepository.getAllHeroes()
            .transform { list -> emit(list.random()) }
            .map { hero ->
                val localHero = heroRepository.getHero(hero.name)
                val info = heroRepository.getHeroDescription(localHero.name, AppLocale.English).single()
                val spell: Ability = info.abilities.first { it.mana != null && it.mana != "" }
                val spellMana: String = spell.mana ?: error("spell ${spell.name} mana is ${spell.mana}")
                val spellCosts = buildSet {
                    while (size < 3) {
                        val wrongSpellCost = spellMana.toInt() + Random.nextInt(-4..4) * 10
                        if (wrongSpellCost > 0) add(wrongSpellCost.toString())
                    }
                }
                val spells = buildList {
                    add(Spell(spellMana, true))
                    addAll(spellCosts.map { Spell(it, false) })
                    shuffle()
                }
                val skillImage = heroRepository.getSpellBitmap(spell.name)
                val skillImageUrl = HeroRepository.getFullUrlHeroSpell(spell.name)
                val palette = Palette.from(skillImage).generate()
                val backgroundColor = palette.vibrantSwatch?.rgb ?: Color.TRANSPARENT
                Data(
                    skillImageUrl,
                    localHero.name,
                    spellMana,
                    backgroundColor,
                    spells,
                )
            }
            .flowOn(dispatchers.IO)
    }

}