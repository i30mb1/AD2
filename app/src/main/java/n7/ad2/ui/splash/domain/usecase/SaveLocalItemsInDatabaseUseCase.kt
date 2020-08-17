package n7.ad2.ui.splash.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.Repository
import n7.ad2.data.source.local.model.LocalHero
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class SaveLocalItemsInDatabaseUseCase @Inject constructor(
    private val repository: Repository,
    private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(list: List<LocalItem>) = withContext(ioDispatcher) {
        repository.insertItems(list)
    }

}