package n7.ad2.ui.heroGuide.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.data.source.local.model.LocalGuide
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import javax.inject.Inject

class ConvertLocalGuideJsonToLocalGuide @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val moshi: Moshi,
) {

    suspend operator fun invoke(
        localGuideJsonList: List<LocalGuideJson>,
        heroName: String,
    ): List<LocalGuide> = withContext(dispatchers.Default) {
        localGuideJsonList.map { localGuideJson ->
            val json = moshi.adapter(LocalGuideJson::class.java).toJson(localGuideJson)
            LocalGuide(name = heroName, json = json)
        }
    }
}