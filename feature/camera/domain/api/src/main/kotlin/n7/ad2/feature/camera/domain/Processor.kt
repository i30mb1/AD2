package n7.ad2.feature.camera.domain

import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

interface Processor {
    suspend fun init()
    suspend fun analyze(image: Image, isRecording: Boolean): ProcessorState
    suspend fun stop()
}
