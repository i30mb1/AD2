package n7.ad2.games.internal.games.skillmp

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import javax.inject.Inject
import kotlin.math.abs
import kotlin.random.Random

/** Плитка компонента: [item] null - «Рецепт» */
internal data class BuildTile(val item: RecipeItem?, val isCorrect: Boolean)

/** «Собери предмет»: цель, [slots] = сколько плиток нужно выбрать, [tiles] - варианты (верные + ловушки) */
internal data class BuildQuestion(val target: RecipeItem, val slots: Int, val tiles: List<BuildTile>)

internal class GetBuildQuizUseCase @Inject constructor(
    private val itemRecipes: ItemRecipes,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(random: Random = Random.Default): BuildQuestion = withContext(dispatchers.IO) {
        createBuildQuestion(itemRecipes.all(), random)
    }
}

private const val TILES = 8
private const val DECOY_POOL = 20

/**
 * Вопрос строится только из [random], поэтому в сетевой игре он одинаковый у обоих.
 * Рецепт нужен, если компоненты дешевле предмета. Плитка «Рецепт» есть всегда - когда он не нужен, это ловушка.
 * Ловушки - предметы дешевле цели с ценой, близкой к средней цене компонентов.
 */
internal fun createBuildQuestion(items: List<RecipeItem>, random: Random): BuildQuestion {
    val byKey = items.associateBy { it.key }
    val targets = items.filter { item -> item.components.isNotEmpty() && item.components.all { it in byKey } }
    val target = targets.random(random)
    val components = target.components.map { byKey.getValue(it) }
    val needsRecipe = components.sumOf { it.cost } < target.cost
    val correct = components.map { BuildTile(it, isCorrect = true) } + listOfNotNull(BuildTile(null, isCorrect = true).takeIf { needsRecipe })
    val averageCost = components.map { it.cost }.average()
    val decoys = items
        .filter { it.key !in target.components && it.key != target.key && it.cost in 1 until target.cost }
        .sortedBy { abs(it.cost - averageCost) }
        .take(DECOY_POOL)
        .shuffled(random)
        .take((TILES - correct.size - if (needsRecipe) 0 else 1).coerceAtLeast(0))
        .map { BuildTile(it, isCorrect = false) }
    val recipeDecoy = listOfNotNull(BuildTile(null, isCorrect = false).takeIf { !needsRecipe })
    return BuildQuestion(target, slots = correct.size, tiles = (correct + decoys + recipeDecoy).shuffled(random))
}
