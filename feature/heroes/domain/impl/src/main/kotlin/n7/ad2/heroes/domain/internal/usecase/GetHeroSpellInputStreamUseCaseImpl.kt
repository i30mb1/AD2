package n7.ad2.heroes.domain.internal.usecase

import android.content.Context
import n7.ad2.heroes.domain.usecase.GetHeroSpellInputStreamUseCase
import java.io.InputStream

private const val ASSET_URL_PREFIX = "file:///android_asset/"

internal class GetHeroSpellInputStreamUseCaseImpl(private val context: Context) : GetHeroSpellInputStreamUseCase {

    override suspend fun invoke(spellImageUrl: String): InputStream = context.assets.open(spellImageUrl.removePrefix(ASSET_URL_PREFIX))
}
