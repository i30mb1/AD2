package n7.ad2.drawer.internal.domain.vo

/** Live numbers of a section shown under its title instead of the static subtitle. */
internal sealed interface VOMenuInfo {
    data class Heroes(val count: Int) : VOMenuInfo
    data class Items(val count: Int) : VOMenuInfo
    data class News(val unread: Int) : VOMenuInfo
    data class LiveMatches(val count: Int) : VOMenuInfo
    data class Viewers(val count: Int) : VOMenuInfo
}
