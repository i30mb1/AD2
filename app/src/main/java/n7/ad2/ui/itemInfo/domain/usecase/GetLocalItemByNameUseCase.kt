package n7.ad2.ui.itemInfo.domain.usecase

import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.database_guides.internal.model.LocalItem
import javax.inject.Inject

class GetLocalItemByNameUseCase @Inject constructor(
    private val dispatchers: DispatchersProvider,
    private val appDatabase: AppDatabase,
) {

    suspend operator fun invoke(itemName: String): LocalItem = withContext(dispatchers.IO) {
        appDatabase.itemsDao.getItem(itemName)
    }

}