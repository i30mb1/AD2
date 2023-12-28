package n7.ad2.feature.camera.domain

import kotlinx.coroutines.flow.SharedFlow
import n7.ad2.feature.camera.domain.model.Image

interface Streamer {
    val stream: SharedFlow<Image>
    suspend fun start()
}
