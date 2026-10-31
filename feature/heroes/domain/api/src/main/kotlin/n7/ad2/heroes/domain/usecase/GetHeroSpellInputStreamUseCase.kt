package n7.ad2.heroes.domain.usecase

import java.io.InputStream

interface GetHeroSpellInputStreamUseCase {

    /** [spellImageUrl] is [n7.ad2.heroes.domain.model.Ability.imageUrl]. */
    suspend operator fun invoke(spellImageUrl: String): InputStream
}
