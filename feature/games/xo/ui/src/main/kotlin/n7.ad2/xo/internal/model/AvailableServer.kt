package n7.ad2.xo.internal.model

internal data class AvailableServer(
    val serverIP: String,
    val port: Int = 0,
    val name: String = "No name",
)
