package n7.ad2.ui.splash.domain.usecase

import android.app.Application
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject


@Suppress("BlockingMethodInNonBlockingContext")
class GetJsonFromAssetsUseCase @Inject constructor(
        private val application: Application,
        private val ioDispatcher: CoroutineDispatcher
) {

    suspend operator fun invoke(filePath: String): String = withContext(ioDispatcher) {
        application.assets.open(filePath).bufferedReader().use {
            it.readText()
        }
    }

}