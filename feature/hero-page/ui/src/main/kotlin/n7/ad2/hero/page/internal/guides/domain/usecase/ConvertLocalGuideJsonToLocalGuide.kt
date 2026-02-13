package n7.ad2.hero.page.internal.guides.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.internal.model.LocalGuide
import javax.inject.Inject

class ConvertLocalGuideJsonToLocalGuide @Inject constructor(private val dispatchers: DispatchersProvider) {

    suspend operator fun invoke(localGuideJsonList: List<LocalGuide>, heroName: String): List<LocalGuide> = withContext(dispatchers.Default) {
        localGuideJsonList.map { localGuideJson ->
            LocalGuide(name = heroName, json = "json")
        }
    }
}
