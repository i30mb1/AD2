package n7.ad2.feature.camera.domain.impl.processor

import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.impl.FPSTimer
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

class ProcessorMetrics(
    private val processor: Processor,
    private val fpsTimer: FPSTimer,
) : Processor {

    override fun analyze(image: Image): ProcessorState {
        val result: ProcessorState = processor.analyze(image)
        fpsTimer.counter.analyzer++
        return result
    }
}
