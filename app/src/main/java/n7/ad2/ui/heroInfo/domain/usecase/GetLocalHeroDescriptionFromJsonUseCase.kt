package n7.ad2.ui.heroInfo.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import n7.ad2.ui.splash.domain.usecase.GetJsonFromAssetsUseCase

class GetLocalHeroDescriptionFromJsonUseCase(
        private val getJsonFromAssetsUseCase: GetJsonFromAssetsUseCase,
        private val ioDispatcher: CoroutineDispatcher,
        private val moshi: Moshi
) {

    suspend operator fun invoke(path: String): LocalHeroDescription {


        return LocalHeroDescription()
    }
}