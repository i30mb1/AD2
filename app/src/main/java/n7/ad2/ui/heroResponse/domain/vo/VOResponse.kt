package n7.ad2.ui.heroResponse.domain.vo

import androidx.databinding.ObservableBoolean
import androidx.lifecycle.MutableLiveData
import n7.ad2.ui.heroPage.Playable

sealed class VOResponse
data class VOResponseTitle(val title: String) : VOResponse()
data class VOResponseBody(
    override val audioUrl: String?,
    val heroName: String,
    val title: String,
    val icons: List<String>,
    val titleForFile: String,
    val isSavedInMemory: Boolean,
) : VOResponse(), Playable {
    override val isPlaying = ObservableBoolean(false)
    val currentProgress = MutableLiveData(0)
    val maxProgress = MutableLiveData(0)
}