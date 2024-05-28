package n7.ad2.feature.camera.domain.impl.streamer

import kotlinx.coroutines.flow.Flow
import n7.ad2.feature.camera.domain.Streamer
import n7.ad2.feature.camera.domain.model.StreamerState

class StreamerWithLogs(
    streamer: Streamer,
) : Streamer {

    override val stream: Flow<StreamerState> = streamer.stream
}
