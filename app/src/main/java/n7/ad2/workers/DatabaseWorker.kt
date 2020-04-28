package n7.ad2.workers

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import n7.ad2.data.source.local.db.AppDatabase
import n7.ad2.ui.MyApplication
import n7.ad2.ui.splash.domain.usecase.ConvertAssetsHeroListToLocalHeroListUseCase
import n7.ad2.ui.splash.domain.usecase.GetAssetsHeroesFromJsonUseCase
import n7.ad2.ui.splash.domain.usecase.GetJsonFromAssetsUseCase
import javax.inject.Inject

class DatabaseWorker(
        val context: Context,
        workerParameters: WorkerParameters
) : CoroutineWorker(context, workerParameters) {

    @Inject
    lateinit var appDatabase: AppDatabase

    @Inject
    lateinit var getJsonFromAssetsUseCase: GetJsonFromAssetsUseCase

    @Inject
    lateinit var getAssetsHeroesFromJsonUseCase: GetAssetsHeroesFromJsonUseCase

    @Inject
    lateinit var convertAssetsHeroListToLocalHeroListUseCase: ConvertAssetsHeroListToLocalHeroListUseCase

    override suspend fun doWork(): Result = coroutineScope {
        (context as MyApplication).component.inject(this@DatabaseWorker)

        val file: String = getJsonFromAssetsUseCase(GetJsonFromAssetsUseCase.HEROES_DATA_FILENAME)
        val assetsHeroesList = getAssetsHeroesFromJsonUseCase(file)
        val localHeroesList = convertAssetsHeroListToLocalHeroListUseCase(assetsHeroesList)

        appDatabase.heroesDao.insert(localHeroesList)

        Result.success()
    }

}