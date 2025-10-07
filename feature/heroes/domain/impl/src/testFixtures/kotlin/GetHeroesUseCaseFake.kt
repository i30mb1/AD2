import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase

public class GetHeroesUseCaseFake : GetHeroesUseCase {

    override fun invoke(): Flow<List<Hero>> = flowOf(
        listOf(
            Hero(
                name = "Anti-Mage",
                avatarUrl = "https://cdn.dota2.com/apps/dota2/images/heroes/antimage_full.png",
                viewedByUser = false,
                mainAttr = "Agility"
            ),
            Hero(
                name = "Axe",
                avatarUrl = "https://cdn.dota2.com/apps/dota2/images/heroes/axe_full.png",
                viewedByUser = true,
                mainAttr = "Strength"
            ),
            Hero(
                name = "Crystal Maiden",
                avatarUrl = "https://cdn.dota2.com/apps/dota2/images/heroes/crystal_maiden_full.png",
                viewedByUser = false,
                mainAttr = "Intelligence"
            )
        )
    )
}
