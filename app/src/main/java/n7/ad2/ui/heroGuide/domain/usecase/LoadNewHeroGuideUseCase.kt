package n7.ad2.ui.heroGuide.domain.usecase

import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import n7.ad2.ui.heroGuide.HeroGuideWorker
import n7.ad2.utils.ResultState
import javax.inject.Inject

class LoadNewHeroGuideUseCase @Inject constructor(
    private val ioDispatcher: CoroutineDispatcher,
    private val workManager: WorkManager,
) {

    suspend operator fun invoke(
        heroName: String,
        callback: (ResultState<Unit>) -> Unit,
    ): Unit = withContext(ioDispatcher) {
        val request = HeroGuideWorker.getRequest(heroName)
        workManager.enqueue(request)

        val work = workManager.getWorkInfoByIdLiveData(request.id)
        val observer = object : Observer<WorkInfo> {
            override fun onChanged(info: WorkInfo) {
                when (info.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        work.removeObserver(this)
                        callback.invoke(ResultState.success(Unit))
                    }
                    WorkInfo.State.FAILED -> {
                        work.removeObserver(this)
                        val failure = info.outputData.getString(HeroGuideWorker.RESULT)!!
                        callback.invoke(ResultState.failure(Exception(failure)))
                    }
                    else -> Unit
                }
            }
        }
        work.observeForever(observer)
    }
}