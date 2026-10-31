package n7.ad2.games.internal.games.skillmp

import android.graphics.BitmapFactory
import android.graphics.Color
import androidx.palette.graphics.Palette
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import javax.inject.Inject
import kotlin.random.Random

/**
 * «Угадай героя»: картинка спелла и 4 героя на выбор. Вопрос строится только из [random],
 * поэтому в сетевой игре оба телефона получают один и тот же вопрос
 */
internal class GetHeroQuizUseCase @Inject constructor(
    private val getHeroesUseCase: GetHeroesUseCase,
    private val getHeroDescriptionUseCase: GetHeroDescriptionUseCase,
    private val getHeroSpellInputStreamUseCase: GetHeroSpellInputStreamUseCase,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(random: Random = Random.Default): Flow<GetSkillsUseCase.Data> = getHeroesUseCase()
        .take(1)
        .map { list ->
            val heroes = list.sortedBy { it.name }
            val hero = heroes.random(random)
            val abilities = getHeroDescriptionUseCase(hero.name).single().abilities
                .filter { it.imageUrl.isNotEmpty() && (it.kind == AbilityKind.BASIC || it.kind == AbilityKind.ULTIMATE) }
            val spell = abilities.random(random)
            val options = (heroes.filter { it.name != hero.name }.shuffled(random).take(OPTIONS - 1) + hero)
                .shuffled(random)
                .map { GetSkillsUseCase.Spell(it.name, it.name == hero.name) }
            val image = getHeroSpellInputStreamUseCase(spell.imageUrl).use { BitmapFactory.decodeStream(it) }
            val backgroundColor = Palette.from(image).generate().vibrantSwatch?.rgb ?: Color.TRANSPARENT
            GetSkillsUseCase.Data(spell.imageUrl, hero.name, manaCost = "", backgroundColor, options, spellLVL = 0)
        }
        .flowOn(dispatchers.IO)

    private companion object {
        const val OPTIONS = 4
    }
}
