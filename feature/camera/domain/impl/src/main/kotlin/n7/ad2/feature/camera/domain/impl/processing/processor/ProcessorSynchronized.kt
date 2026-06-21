package n7.ad2.feature.camera.domain.impl.processing.processor

import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import n7.ad2.feature.camera.domain.Processor
import n7.ad2.feature.camera.domain.model.Image
import n7.ad2.feature.camera.domain.model.ProcessorState

internal class ProcessorSynchronized(
    private val processor: Processor,
) : Processor by processor {

    private val mutex = Mutex()

    override suspend fun analyze(image: Image, isRecording: Boolean): ProcessorState = mutex.withLock {
        processor.analyze(image, isRecording)
    }

    override suspend fun stop() = mutex.withLock {
        processor.stop()
    }
}
