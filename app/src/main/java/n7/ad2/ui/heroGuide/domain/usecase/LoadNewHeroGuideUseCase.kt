package n7.ad2.ui.heroGuide.domain.usecase

import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.HeroGuideWorker
import javax.inject.Inject

class LoadNewHeroGuideUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val workManager: WorkManager,
) {

    suspend operator fun invoke(heroName: String): Unit = withContext(ioDispatcher) {
        val request = HeroGuideWorker.getRequest(heroName)
        workManager.enqueue(request)

        val work = workManager.getWorkInfoByIdLiveData(request.id)
        val observer = object : Observer<WorkInfo> {
            override fun onChanged(info: WorkInfo) {
                when (info.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        work.removeObserver(this)
                    }
                    WorkInfo.State.FAILED -> {
                        work.removeObserver(this)
                        val failureMessage = info.outputData.getString(HeroGuideWorker.RESULT)!!
                        throw Exception(failureMessage)
                    }
                    else -> Unit
                }
            }
        }
        work.observeForever(observer)
    }
}