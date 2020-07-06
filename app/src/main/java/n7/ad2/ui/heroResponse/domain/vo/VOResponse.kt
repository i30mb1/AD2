package n7.ad2.ui.heroResponse.domain.vo

import androidx.databinding.ObservableBoolean
import n7.ad2.ui.heroPage.Playable

sealed class VOResponse
data class VOResponseHeader(val title: String): VOResponse()
data class VOResponseBody(val title: String, override val audioUrl: String?, val icons: List<String>): VOResponse(), Playable { override val isPlaying: ObservableBoolean = ObservableBoolean(false) }