package n7.ad2.ui.heroInfo.domain.vo

import androidx.databinding.BaseObservable
import androidx.databinding.ObservableBoolean

sealed class VOResponse
data class VOResponseHeader(val title: String): VOResponse()
data class VOResponseBody(val title: String, val audioUrl: String, val icons: List<String>): VOResponse() { val isPlaying: ObservableBoolean = ObservableBoolean(false) }