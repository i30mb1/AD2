package n7.ad2.ui.heroResponse.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponses
import javax.inject.Inject

class GetLocalHeroResponsesFromJsonUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val moshi: Moshi
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(json: String): LocalHeroResponses = withContext(ioDispatcher) {
        val result = moshi.adapter(LocalHeroResponses::class.java).fromJson(json)!!

        result
    }

}