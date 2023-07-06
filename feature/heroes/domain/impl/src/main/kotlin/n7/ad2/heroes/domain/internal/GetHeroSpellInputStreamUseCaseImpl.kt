package n7.ad2.heroes.domain.internal

import n7.ad2.heroes.domain.GetHeroSpellInputStreamUseCase
import java.io.InputStream

internal class GetHeroSpellInputStreamUseCaseImpl(
    private val heroesRepository: HeroesRepository,
) : GetHeroSpellInputStreamUseCase {

    override suspend fun invoke(spellName: String): InputStream {
        return heroesRepository.getSpellInputStream(spellName)
    }
}
