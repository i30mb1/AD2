package n7.ad2.hero.page.internal.responses.domain.vo

import n7.ad2.ui.adapter.HeaderViewHolder

sealed class VOResponse {
    data class Title(val data: HeaderViewHolder.Data) : VOResponse()
    data class Body(
        val heroName: String,
        val title: String,
        val icons: List<VOResponseImage>,
        val titleForFile: String,
        val isSavedInMemory: Boolean,
        val audioUrl: String,
    ) : VOResponse() {
        var downloadID: Long? = null
    }
}
