package n7.ad2.feature.camera.domain.impl

import kotlin.time.TimeSource
import n7.ad2.app.logger.Logger
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

class ProcessorMetrics(
    private val processor: Processor,
    private val logger: Logger,
) : Processor {

    override fun analyze(image: Image): ProcessorState {
        val mark = TimeSource.Monotonic.markNow()
        val result: ProcessorState = processor.analyze(image)
        val time = mark.elapsedNow()
        logger.log("processed bitmap ${time.inWholeMilliseconds}")
        return result
    }
}
