package n7.ad2.feature.camera.domain.impl.processing.processor

import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

class ProcessorWithLogging(
    private val processor: Processor,
    private val fpsTimer: FPSTimer,
) : Processor by processor {

    override suspend fun analyze(image: Image, isRecording: Boolean): ProcessorState {
        val result = processor.analyze(image, isRecording)
        fpsTimer.counter.analyzer++
        return result
    }
}
