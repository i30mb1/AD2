package n7.ad2.ui.heroInfo.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroInfo.domain.model.LocalHeroDescription
import javax.inject.Inject

class GetLocalHeroDescriptionFromJsonUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val moshi: Moshi
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(json: String): LocalHeroDescription = withContext(ioDispatcher) {
        val result = moshi.adapter(LocalHeroDescription::class.java).fromJson(json)!!

         result
    }
}