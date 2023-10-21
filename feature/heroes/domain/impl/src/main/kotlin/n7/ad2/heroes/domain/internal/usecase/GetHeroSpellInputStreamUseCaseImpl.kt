package n7.ad2.heroes.domain.internal.usecase

import java.io.InputStream
import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase

internal class GetHeroSpellInputStreamUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroSpellInputStreamUseCase {

    override suspend fun invoke(spellName: String): InputStream {
        TODO()
    }
}
