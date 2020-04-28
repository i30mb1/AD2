package n7.ad2.ui.splash.domain.usecase

import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.data.source.local.model.AssetsHero
import n7.ad2.data.source.local.model.AssetsHeroList
import java.io.FileNotFoundException
import javax.inject.Inject

const val HEROES_DATA_FILENAME = "heroesNew.json"

class GetAssetsHeroesFromJsonUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val moshi: Moshi
) {
    suspend operator fun invoke(jsonString: String): List<AssetsHero> = withContext(ioDispatcher) {
        if (jsonString.isEmpty()) throw FileNotFoundException("File empty or not exist")

        val result = moshi.adapter(AssetsHeroList::class.java).fromJson(jsonString)!!

        result.heroes
    }
}