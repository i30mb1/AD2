package n7.ad2.hero_page.internal.responses.domain.vo

import androidx.lifecycle.MutableLiveData

sealed class VOResponse
data class VOResponseTitle(val title: String) : VOResponse()
data class VOResponseBody(
    val heroName: String,
    val title: String,
    val icons: List<VOResponseImage>,
    val titleForFile: String,
    val isSavedInMemory: Boolean,
) : VOResponse() {
    val currentProgress = MutableLiveData(0)
    val maxProgress = MutableLiveData(0)
}