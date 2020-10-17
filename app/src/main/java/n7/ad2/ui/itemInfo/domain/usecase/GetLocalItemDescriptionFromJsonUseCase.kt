package n7.ad2.ui.itemInfo.domain.usecase;

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.itemInfo.domain.model.LocalItemDescription
import javax.inject.Inject

class GetLocalItemDescriptionFromJsonUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val moshi: Moshi
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(json: String): LocalItemDescription = withContext(ioDispatcher) {
        moshi.adapter(LocalItemDescription::class.java).fromJson(json)!!
    }
}