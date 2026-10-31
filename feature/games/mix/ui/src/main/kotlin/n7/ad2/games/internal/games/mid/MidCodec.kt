package n7.ad2.games.internal.games.mid

/**
 * Mid messages over the network: "r;hero" ready with this hero, "g" both are ready - go, "c;creep;landAt" my hit took
 * the creep, "s;creep;startAt" I swung at the creep (only to draw it). Each phone decides about its own hits.
 */
internal object MidCodec {

    sealed interface Message {
        data class Ready(val heroKey: String) : Message
        data object Go : Message
        data class Claim(val creepId: Int, val landAt: Long) : Message
        data class Swing(val creepId: Int, val startAt: Long) : Message
    }

    fun ready(heroKey: String) = "r;$heroKey"

    fun go() = "g"

    fun claim(creepId: Int, landAt: Long) = "c;$creepId;$landAt"

    fun swing(creepId: Int, startAt: Long) = "s;$creepId;$startAt"

    fun decode(payload: String): Message? {
        val parts = payload.split(";")
        return when (parts.first()) {
            "r" -> parts.getOrNull(1)?.takeIf { parts.size == 2 && it.isNotEmpty() }?.let { Message.Ready(it) }
            "g" -> Message.Go.takeIf { parts.size == 1 }
            "c" -> numbers(parts)?.let { (creep, time) -> Message.Claim(creep, time) }
            "s" -> numbers(parts)?.let { (creep, time) -> Message.Swing(creep, time) }
            else -> null
        }
    }

    private fun numbers(parts: List<String>): Pair<Int, Long>? {
        if (parts.size != 3) return null
        return (parts[1].toIntOrNull() ?: return null) to (parts[2].toLongOrNull() ?: return null)
    }
}
