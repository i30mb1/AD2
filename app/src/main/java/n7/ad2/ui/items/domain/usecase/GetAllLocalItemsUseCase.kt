package n7.ad2.ui.items.domain.usecase;

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class GetAllLocalItemsUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val appDatabase: AppDatabase,
) {

    @Suppress("BlockingMethodInNonBlockingContext")
    suspend operator fun invoke(): Flow<List<LocalItem>> = withContext(ioDispatcher) {
        appDatabase.itemsDao.getAll()
    }
}