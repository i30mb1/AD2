package n7.ad2.feature.camera.domain.model

import org.jetbrains.kotlinx.dl.api.inference.objectdetection.DetectedObject

class ProcessorState(
    val detectedObject: DetectedObject?,
)
