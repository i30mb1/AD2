package n7.ad2.feature.camera.domain

import kotlinx.coroutines.flow.Flow
import n7.ad2.feature.camera.domain.model.Image

interface Streamer {
    val stream: Flow<Image>
}
