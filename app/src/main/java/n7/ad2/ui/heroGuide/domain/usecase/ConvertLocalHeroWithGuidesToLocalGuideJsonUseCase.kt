package n7.ad2.ui.heroGuide.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.LocalHeroWithGuides
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class ConvertLocalHeroWithGuidesToLocalGuideJsonUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val moshi: Moshi,
) {

    suspend operator fun invoke(
        localHeroWithGuides: LocalHeroWithGuides,
    ): List<LocalGuideJson> = withContext(dispatchers.IO) {
        localHeroWithGuides.guides.map {
            moshi.adapter(LocalGuideJson::class.java).fromJson(it.json)!!
        }
    }

}