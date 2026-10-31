package n7.ad2.games.internal.games.mid

import javax.inject.Inject
import kotlin.math.roundToInt
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase

/** Heroes of [MidRoster] with damage, attack time and projectile speed from the hero data of the current patch */
internal class GetMidHeroesUseCase @Inject constructor(
    private val getHeroByNameUseCase: GetHeroByNameUseCase,
    private val getHeroDescriptionUseCase: GetHeroDescriptionUseCase,
    private val dispatchers: DispatchersProvider,
) {

    suspend operator fun invoke(): List<MidHero> = withContext(dispatchers.IO) {
        MidRoster.attackPointsMs.map { (name, attackPointMs) ->
            val hero = getHeroByNameUseCase(name)
            val stats = getHeroDescriptionUseCase(hero.name).first().stats
            MidHero(
                key = name,
                name = name,
                imageUrl = hero.avatarUrl,
                damage = ((stats.damageMin + stats.damageMax) / 2).toInt(),
                attackPointMs = attackPointMs,
                attackRateMs = (stats.attackRate * 1000).roundToInt(),
                projectileSpeed = stats.projectileSpeed.roundToInt(),
            )
        }
    }
}
