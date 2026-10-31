package n7.ad2.games.internal.games.skillmp

import android.graphics.Color
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.take
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.heroes.domain.model.HeroStats
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import javax.inject.Inject
import kotlin.math.roundToInt
import kotlin.random.Random

/** Характеристика для «Больше или меньше»: вопрос и как достать значение (1 уровень героя) */
internal enum class CompareStat(val question: Int, val value: (HeroStats) -> Double, val format: (Double) -> String = { it.roundToInt().toString() }) {
    HEALTH(R.string.compare_health, { it.health }),
    MANA(R.string.compare_mana, { it.mana }),
    ARMOR(R.string.compare_armor, { it.armor }, { "%.1f".format(it) }),
    DAMAGE(R.string.compare_damage, { (it.damageMin + it.damageMax) / 2 }),
    MOVE_SPEED(R.string.compare_move_speed, { it.moveSpeed }),
    ATTACK_RANGE(R.string.compare_attack_range, { it.attackRange }),
}

/**
 * «Больше или меньше»: два героя и характеристика, правильный - у кого значение больше.
 * Всё выбирается из [random], поэтому в сетевой игре вопрос у обоих одинаковый
 */
internal class GetCompareQuizUseCase @Inject constructor(
    private val getHeroesUseCase: GetHeroesUseCase,
    private val getHeroDescriptionUseCase: GetHeroDescriptionUseCase,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(random: Random = Random.Default): Flow<GetSkillsUseCase.Data> = getHeroesUseCase()
        .take(1)
        .map { list ->
            val heroes = list.sortedBy { it.name }
            val stat = CompareStat.entries.random(random)
            repeat(MAX_PAIRS) {
                val (left, right) = heroes.shuffled(random).take(2)
                val leftValue = stat.value(getHeroDescriptionUseCase(left.name).single().stats)
                val rightValue = stat.value(getHeroDescriptionUseCase(right.name).single().stats)
                // равные значения не загадываем: правильного ответа нет
                if (stat.format(leftValue) != stat.format(rightValue)) {
                    val leftWins = leftValue > rightValue
                    return@map GetSkillsUseCase.Data(
                        skillImage = "",
                        name = stat.name,
                        manaCost = "",
                        backgroundColor = Color.TRANSPARENT,
                        suggestsSpellList = listOf(GetSkillsUseCase.Spell(left.name, leftWins), GetSkillsUseCase.Spell(right.name, !leftWins)),
                        spellLVL = 0,
                        compare = GetSkillsUseCase.Compare(
                            question = stat.question,
                            left = GetSkillsUseCase.Side(left.name, left.avatarUrl, stat.format(leftValue)),
                            right = GetSkillsUseCase.Side(right.name, right.avatarUrl, stat.format(rightValue)),
                        ),
                    )
                }
            }
            error("no pair with different ${stat.name}")
        }
        .flowOn(dispatchers.IO)

    private companion object {
        const val MAX_PAIRS = 10
    }
}
