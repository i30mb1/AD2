package n7.ad2.drawer.internal.domain.vo

import n7.ad2.core.ui.performance.ResourceUsage

/** One meter of the device tile: the current [value] and the recent history as [points] from 0 to 1, oldest first. */
internal data class VOPerformance(val kind: Kind, val value: Int, val status: ResourceUsage.Status, val points: List<Float>) {
    enum class Kind { CPU, RAM, FPS }
}
