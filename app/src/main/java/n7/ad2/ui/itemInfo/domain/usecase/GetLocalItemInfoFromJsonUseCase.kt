package n7.ad2.ui.itemInfo.domain.usecase

import ad2.n7.coroutines.DispatchersProvider
import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import n7.ad2.ui.itemInfo.domain.model.LocalItemInfo
import javax.inject.Inject

class GetLocalItemInfoFromJsonUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val moshi: Moshi,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(json: String): LocalItemInfo = withContext(dispatchers.Default) {
        moshi.adapter(LocalItemInfo::class.java).fromJson(json)!!
    }

}