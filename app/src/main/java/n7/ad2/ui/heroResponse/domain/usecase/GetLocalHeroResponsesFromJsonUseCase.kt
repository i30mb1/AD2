package n7.ad2.ui.heroResponse.domain.usecase

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroResponse.domain.model.LocalHeroResponsesItem
import javax.inject.Inject

class GetLocalHeroResponsesFromJsonUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val moshi: Moshi
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(json: String): List<LocalHeroResponsesItem> = withContext(ioDispatcher) {
        val type = Types.newParameterizedType(List::class.java, LocalHeroResponsesItem::class.java)
        val result : JsonAdapter<List<LocalHeroResponsesItem>> = moshi.adapter(type)

        result.fromJson(json)!!
    }

}