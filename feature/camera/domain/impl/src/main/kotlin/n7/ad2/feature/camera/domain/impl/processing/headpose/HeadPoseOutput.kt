package n7.ad2.feature.camera.domain.impl.processing.headpose

import n7.ad2.feature.camera.domain.Rotation

class HeadPoseOutput(
    val occlusionScore: Float,
    val rotation: Rotation,
)
