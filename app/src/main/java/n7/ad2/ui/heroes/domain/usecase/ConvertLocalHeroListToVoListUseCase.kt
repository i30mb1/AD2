package n7.ad2.ui.heroes.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.ui.heroes.domain.adapter.toVo
import n7.ad2.ui.heroes.domain.vo.VOHero

class ConvertLocalHeroListToVoListUseCase constructor(
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(list: List<LocalHero>): List<VOHero> = withContext(ioDispatcher) {
        list.map {
            it.toVo()
        }
    }

}