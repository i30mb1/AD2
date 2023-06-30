package n7.ad2.database_guides.internal.domain.usecase

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.Resources
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.database_guides.api.dao.ItemsDao
import n7.ad2.database_guides.internal.domain.model.AssetsItem
import n7.ad2.database_guides.internal.model.LocalItem
import n7.ad2.app.logger.Logger
import javax.inject.Inject

class PopulateItemsDatabaseUseCase @Inject constructor(
    private val res: Resources,
    private val itemsDao: ItemsDao,
    private val moshi: Moshi,
    private val logger: Logger,
    private val dispatcher: DispatchersProvider,
) {

    class PopulateItemsDatabaseException(message: String) : Exception(message)

    suspend operator fun invoke(): Flow<Boolean> = flow {
        val json = res.getAssets("items.json").bufferedReader().use {
            it.readText()
        }

        if (json.isEmpty()) throw PopulateItemsDatabaseException("File with heroes empty or not exist")

        val typeAssetsItem = Types.newParameterizedType(List::class.java, AssetsItem::class.java)
        val adapter: JsonAdapter<List<AssetsItem>> = moshi.adapter(typeAssetsItem)
        val listAssetsItem = adapter.fromJson(json) ?: throw PopulateItemsDatabaseException("Could not parse assets items")

        val result = listAssetsItem.map {
            LocalItem(name = it.name, type = it.section, viewedByUser = false)
        }

        itemsDao.insert(result)
        logger.log("db filled with items")
        emit(true)
    }.flowOn(dispatcher.IO)

}