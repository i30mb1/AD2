package n7.ad2.hero_page.internal.responses.domain.vo

import androidx.lifecycle.MutableLiveData
import n7.ad2.hero_page.internal.pager.Playable

sealed class VOResponse
data class VOResponseTitle(val title: String) : VOResponse()
data class VOResponseBody(
    override val audioUrl: String,
    val heroName: String,
    val title: String,
    val icons: List<VOResponseImage>,
    val titleForFile: String,
    val isSavedInMemory: Boolean,
) : VOResponse(), Playable {
    override val isPlaying = false
    val currentProgress = MutableLiveData(0)
    val maxProgress = MutableLiveData(0)
}