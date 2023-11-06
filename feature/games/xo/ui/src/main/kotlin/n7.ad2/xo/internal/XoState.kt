package n7.ad2.xo.internal

import androidx.compose.runtime.Immutable

@Immutable
internal data class XoState(
    val deviceIP: String,
    val servers: List<Server>,
) {

    companion object {
        fun init() = XoState(
            "",
            emptyList(),
        )
    }
}

internal data class Server(
    val serverIP: String,
)


