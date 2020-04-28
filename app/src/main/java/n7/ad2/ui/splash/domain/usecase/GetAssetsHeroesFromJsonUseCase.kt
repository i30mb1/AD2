package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import com.squareup.moshi.Moshi
import kotlinx.coroutines.CoroutineDispatcher
import n7.ad2.data.source.local.model.LocalHero
import java.io.FileNotFoundException
import javax.inject.Inject

const val HEROES_DATA_FILENAME = "heroesNew.json"

class GetAssetsHeroesFromJsonUseCase @Inject constructor(
        private val ioDispatcher: CoroutineDispatcher,
        private val moshi: Moshi
) {
    operator fun invoke(jsonString: String): List<LocalHero> {
        if(jsonString.isEmpty()) throw FileNotFoundException("File empty or not exist")

        moshi.adapter()

        return emptyList()
    }
}