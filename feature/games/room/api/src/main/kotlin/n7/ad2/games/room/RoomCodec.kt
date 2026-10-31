package n7.ad2.games.room

import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

object RoomCodec {

    private val json = Json {
        ignoreUnknownKeys = true
        classDiscriminator = "type"
    }

    fun encode(message: RoomMessage): String = json.encodeToString(RoomMessage.serializer(), message)

    /** null для битого JSON и неизвестного типа (сообщение от более новой версии приложения) */
    fun decode(text: String): RoomMessage? = try {
        json.decodeFromString(RoomMessage.serializer(), text)
    } catch (e: SerializationException) {
        null
    } catch (e: IllegalArgumentException) {
        null
    }
}
