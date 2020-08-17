package n7.ad2.ui.splash.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.splash.domain.model.AssetsHero
import n7.ad2.ui.splash.domain.model.AssetsHeroList
import n7.ad2.ui.splash.domain.model.AssetsItem
import n7.ad2.ui.splash.domain.model.AssetsItemList
import java.io.FileNotFoundException
import javax.inject.Inject

@Suppress("BlockingMethodInNonBlockingContext")
class ConvertJsonItemsToAssetsItemUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val moshi: Moshi
) {

    suspend operator fun invoke(jsonString: String): List<AssetsItem> = withContext(ioDispatcher) {
        if (jsonString.isEmpty()) throw FileNotFoundException("File empty or not exist")

        val result = moshi.adapter(AssetsItemList::class.java).fromJson(jsonString)!!

        result.items
    }

}