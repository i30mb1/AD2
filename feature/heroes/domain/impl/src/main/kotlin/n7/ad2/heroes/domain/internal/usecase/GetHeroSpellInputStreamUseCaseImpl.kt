package n7.ad2.heroes.domain.internal.usecase

import n7.ad2.heroes.domain.internal.HeroesRepository
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import java.io.InputStream

internal class GetHeroSpellInputStreamUseCaseImpl(private val heroesRepository: HeroesRepository) : GetHeroSpellInputStreamUseCase {

    override suspend fun invoke(spellName: String): InputStream {
        TODO("GetHeroSpellInputStream not implemented")
    }
}
