package n7.ad2.heroes.domain.usecase

import java.io.InputStream

interface GetHeroSpellInputStreamUseCase {

    suspend operator fun invoke(spellName: String): InputStream
}
