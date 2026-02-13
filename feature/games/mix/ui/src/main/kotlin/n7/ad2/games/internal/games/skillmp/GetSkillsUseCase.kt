package n7.ad2.games.internal.games.skillmp

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.transform
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import javax.inject.Inject
import kotlin.random.Random
import kotlin.random.nextInt

internal class GetSkillsUseCase @Inject constructor(
    private val getHeroesUseCase: GetHeroesUseCase,
    private val dispatchers: DispatchersProvider,
    private val getHeroByNameUseCase: GetHeroByNameUseCase,
    private val getHeroSpellInputStreamUseCase: GetHeroSpellInputStreamUseCase,
    private val getHeroDescriptionUseCase: GetHeroDescriptionUseCase,
) {

    data class Data(val skillImage: String, val name: String, val manaCost: String, val backgroundColor: Int, val suggestsSpellList: List<Spell>, val spellLVL: Int)

    data class Spell(val cost: String, val isRightAnswer: Boolean)

    operator fun invoke(): Flow<Data> = getHeroesUseCase()
        .transform { list -> emit(list.random()) }
        .map { hero ->
            val localHero = getHeroByNameUseCase(hero.name)
            val info = getHeroDescriptionUseCase(localHero.name).single()
            val spell: Ability = info.abilities.first { it.mana != null && it.mana != "" }
            val spellManaList = spell.mana?.split("/") ?: error("spell ${spell.name} mana is ${spell.mana}")
            val spellMana = spellManaList.random()
            val spellLVL = spellManaList.indexOf(spellMana) + 1
            val spellCosts = buildSet {
                add(spellMana)
                while (size < 4) {
                    val randomTail = Random.nextInt(-3..3) * 10
                    val wrongSpellCost = spellMana.toInt() + randomTail
                    if (wrongSpellCost <= 0 || wrongSpellCost == spellMana.toInt()) continue
                    add(wrongSpellCost.toString())
                }
            }
                .map { mana -> Spell(mana, spellMana == mana) }
                .shuffled()
            val skillImage = getHeroSpellInputStreamUseCase(spell.name).use { BitmapFactory.decodeStream(it) }
            val palette = Palette.from(skillImage).generate()
            val backgroundColor = palette.vibrantSwatch?.rgb ?: Color.TRANSPARENT
            Data(
                spell.imageUrl,
                localHero.name,
                spellMana,
                backgroundColor,
                spellCosts,
                spellLVL,
            )
        }
        .flowOn(dispatchers.IO)
}
