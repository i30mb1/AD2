package n7.ad2.ui.performance

data class ResourceUsage(val cpu: Info, val ram: Info, val fps: Info) {
    data class Info(val value: Int, val status: Status)

    enum class Status {
        VERY_BAD,
        POOR,
        FAIR,
        GOOD,
        VERY_GOOD,
        EXCELLENT,
    }
}
