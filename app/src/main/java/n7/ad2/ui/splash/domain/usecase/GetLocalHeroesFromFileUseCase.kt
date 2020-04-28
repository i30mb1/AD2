package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Inject

class GetLocalHeroesFromFileUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher
)