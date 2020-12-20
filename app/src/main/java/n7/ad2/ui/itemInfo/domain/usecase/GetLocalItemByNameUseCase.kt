package n7.ad2.ui.itemInfo.domain.usecase

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.data.source.local.model.LocalItem
import javax.inject.Inject

class GetLocalItemByNameUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val appDatabase: AppDatabase,
) {

    suspend operator fun invoke(itemName: String): LocalItem = withContext(ioDispatcher) {
        appDatabase.itemsDao.getItem(itemName)
    }

}