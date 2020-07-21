package n7.ad2.ui.heroResponse.domain.vo

import androidx.databinding.ObservableBoolean
import androidx.databinding.ObservableInt
import n7.ad2.ui.heroPage.Playable

sealed class VOResponse
data class VOResponseHeader(val title: String) : VOResponse()
data class VOResponseBody(
    override val audioUrl: String?,
    val heroName: String,
    val title: String,
    val icons: List<String>,
    val titleForFile: String,
    val savedInMemory: Boolean
) : VOResponse(), Playable {
    override val isPlaying: ObservableBoolean = ObservableBoolean(false)

    val currentProgress = ObservableInt(0)

    val maxProgress = ObservableInt(0)

}