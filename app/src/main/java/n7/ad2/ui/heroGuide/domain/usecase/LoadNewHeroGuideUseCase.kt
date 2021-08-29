package n7.ad2.ui.heroGuide.domain.usecase

import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import n7.ad2.ui.heroGuide.HeroGuideWorker
import javax.inject.Inject

class LoadNewHeroGuideUseCase @Inject constructor(
    private val workManager: WorkManager,
) {

    suspend operator fun invoke(heroName: String) {
        val request = HeroGuideWorker.getRequest(heroName)
        workManager.enqueue(request)

        val work = workManager.getWorkInfoByIdLiveData(request.id)
        work.observeForever(object : Observer<WorkInfo> {
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
        })
    }
}