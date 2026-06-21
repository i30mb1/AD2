package n7.ad2.heroes.domain.internal.usecase

import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroByNameUseCase

internal class GetHeroByNameUseCaseImpl : GetHeroByNameUseCase {

    override suspend fun invoke(name: String): Hero {
        val key = name.lowercase().replace(" ", "_")
        return Hero(
            name = name,
            avatarUrl = "file:///android_asset/heroes/$key/full.webp",
            viewedByUser = false,
            mainAttr = "",
        )
    }
}
