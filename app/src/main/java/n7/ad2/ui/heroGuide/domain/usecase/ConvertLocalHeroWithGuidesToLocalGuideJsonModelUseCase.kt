package n7.ad2.ui.heroGuide.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJsonModel
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class ConvertLocalHeroWithGuidesToLocalGuideJsonModelUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val  moshi: Moshi
) {

    suspend operator fun invoke(json: String) = withContext(ioDispatcher) {
       moshi.adapter(LocalGuideJsonModel::class.java).fromJson(json)!!
    }

}