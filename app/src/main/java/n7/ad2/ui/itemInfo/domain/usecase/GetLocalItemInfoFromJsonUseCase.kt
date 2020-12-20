package n7.ad2.ui.itemInfo.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.itemInfo.domain.model.LocalItemInfo
import javax.inject.Inject

class GetLocalItemInfoFromJsonUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val moshi: Moshi,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(json: String): LocalItemInfo = withContext(ioDispatcher) {
        moshi.adapter(LocalItemInfo::class.java).fromJson(json)!!
    }

}