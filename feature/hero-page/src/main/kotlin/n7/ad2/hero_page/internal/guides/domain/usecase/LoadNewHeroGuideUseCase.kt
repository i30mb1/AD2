package n7.ad2.hero_page.internal.guides.domain.usecase

import androidx.lifecycle.Observer
import androidx.work.WorkInfo
import androidx.work.WorkManager
import kotlinx.coroutines.suspendCancellableCoroutine
import n7.ad2.hero_page.internal.guides.HeroGuideWorker
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class LoadNewHeroGuideUseCase @Inject constructor(
    private val workManager: WorkManager,
) {

    suspend operator fun invoke(heroName: String): Unit = suspendCancellableCoroutine { continuation ->
        val request = HeroGuideWorker.getRequest(heroName)
        workManager.enqueue(request)

        val work = workManager.getWorkInfoByIdLiveData(request.id)
        work.observeForever(object : Observer<WorkInfo> {
            override fun onChanged(info: WorkInfo) {
                when (info.state) {
                    WorkInfo.State.SUCCEEDED -> {
                        work.removeObserver(this)
                        continuation.resume(Unit)
                    }
                    WorkInfo.State.FAILED -> {
                        work.removeObserver(this)
                        val failureMessage = info.outputData.getString(HeroGuideWorker.RESULT)!!
                        continuation.resumeWithException(Exception(failureMessage))
                    }
                    else -> Unit
                }
            }
        })
    }
}