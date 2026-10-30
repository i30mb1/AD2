package n7.ad2.hero.page.internal.responses.domain.vo

import n7.ad2.core.ui.adapter.HeaderViewHolder

sealed class VOResponse {
    data class Title(val data: HeaderViewHolder.Data) : VOResponse()
    data class Body(val heroName: String, val title: String, val icons: List<VOResponseImage>, val titleForFile: String, val isSavedInMemory: Boolean, val audioUrl: String) : VOResponse() {
        var downloadID: Long? = null
    }
}

/**
 * The same audio file is reused in several categories, so a row is told apart by its text too.
 * The file name is used instead of [VOResponse.Body.audioUrl], the url turns into a local path after a download.
 */
val VOResponse.Body.key: String get() = "$title|$titleForFile"
