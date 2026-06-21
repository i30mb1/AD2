package n7.ad2.feature.camera.domain

import kotlin.math.absoluteValue

data class Rotation(val x: Float, val y: Float, val z: Float) {

    fun areAbsValuesGreaterThan(limit: Float): Boolean =
        x.absoluteValue > limit || y.absoluteValue > limit || z.absoluteValue > limit

    companion object {
        val ZERO: Rotation get() = Rotation(0f, 0f, 0f)
    }
}
