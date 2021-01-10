package n7.ad2.ui.splash.domain.usecase

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.splash.domain.model.AssetsItem
import java.io.FileNotFoundException
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class ConvertJsonItemsToAssetsItemUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val moshi: Moshi,
) {

    suspend operator fun invoke(jsonString: String): List<AssetsItem> = withContext(ioDispatcher) {
        if (jsonString.isEmpty()) throw FileNotFoundException("File empty or not exist")

        val type = Types.newParameterizedType(List::class.java, AssetsItem::class.java)
        val result: JsonAdapter<List<AssetsItem>> = moshi.adapter(type)

        result.fromJson(jsonString)!!
    }

}