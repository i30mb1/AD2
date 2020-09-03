package n7.ad2.ui.heroGuide.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.domain.adapter.toVO
import n7.ad2.ui.heroGuide.domain.model.LocalGuideJson
import n7.ad2.ui.heroGuide.domain.vo.VOHeroGuide
import javax.inject.Inject

class ConvertLocalGuideJsonToVOHeroGuide @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(list: List<LocalGuideJson>, application: Application): List<VOHeroGuide> = withContext(ioDispatcher) {
        list.map {
            it.toVO(application)
        }
    }

}