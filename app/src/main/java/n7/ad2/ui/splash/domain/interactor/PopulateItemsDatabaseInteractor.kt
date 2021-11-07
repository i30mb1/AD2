package n7.ad2.ui.splash.domain.interactor

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AD2Logger
import n7.ad2.data.source.local.ItemRepository
import n7.ad2.data.source.local.model.LocalItem
import n7.ad2.ui.splash.domain.model.AssetsItem
import javax.inject.Inject

class PopulateItemsDatabaseInteractor @Inject constructor(
    private val moshi: Moshi,
    private val itemRepository: ItemRepository,
    private val logger: AD2Logger,
    private val ioDispatcher: CoroutineDispatcher,
) {

    class PopulateItemsDatabaseException(message: String) : Exception(message)

    suspend operator fun invoke(): Flow<Boolean> = flow {
        val json = itemRepository.getAssetsItems()
        if (json.isEmpty()) throw PopulateItemsDatabaseException("File with heroes empty or not exist")

        val type = Types.newParameterizedType(List::class.java, AssetsItem::class.java)
        val result: JsonAdapter<List<AssetsItem>> = moshi.adapter(type)
        val assetsItemsList = result.fromJson(json) ?: throw PopulateItemsDatabaseException("Could not parse assets heroes")

        val localItemsList = assetsItemsList.map { LocalItem(name = it.name, type = it.section, viewedByUser = false) }

        itemRepository.insertItems(localItemsList)
        logger.log("Items loaded in DB")
        emit(true)
    }.flowOn(ioDispatcher)

}