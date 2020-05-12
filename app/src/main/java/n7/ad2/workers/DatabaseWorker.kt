package n7.ad2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import n7.ad2.ui.MyApplication
import n7.ad2.ui.splash.domain.usecase.ConvertAssetsHeroListToLocalHeroListUseCase
import n7.ad2.ui.splash.domain.usecase.ConvertJsonHeroesToAssetsHeroesUseCase
import n7.ad2.ui.splash.domain.usecase.GetJsonHeroesFromAssetsUseCase
import n7.ad2.ui.splash.domain.usecase.SaveLocalHeroesInDatabaseUseCase
import javax.inject.Inject

class DatabaseWorker(
        val context: Context,
        workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var saveLocalHeroesInDatabaseUseCase: SaveLocalHeroesInDatabaseUseCase

    @Inject
    lateinit var getJsonHeroesFromAssetsUseCase: GetJsonHeroesFromAssetsUseCase

    @Inject
    lateinit var convertJsonHeroesToAssetsHeroesUseCase: ConvertJsonHeroesToAssetsHeroesUseCase

    @Inject
    lateinit var convertAssetsHeroListToLocalHeroListUseCase: ConvertAssetsHeroListToLocalHeroListUseCase

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@DatabaseWorker)

        val json: String = getJsonHeroesFromAssetsUseCase()
        val assetsHeroesList = convertJsonHeroesToAssetsHeroesUseCase(json)
        val localHeroesList = convertAssetsHeroListToLocalHeroListUseCase(assetsHeroesList)

        saveLocalHeroesInDatabaseUseCase(localHeroesList)

        Result.success()
    }

}