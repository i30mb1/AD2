import com.google.common.truth.Truth
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import n7.ad2.games.room.RoomCodec
import n7.ad2.games.room.RoomMessage
import org.junit.Test

internal class RoomCodecTest {

    private val host = Player("h", "Host")
    private val guest = Player("g", "Гость")

    @Test
    fun `every message round-trips`() {
        val messages = listOf(
            RoomMessage.Hello(guest, 1),
            RoomMessage.Welcome(host, guest),
            RoomMessage.Reject("version"),
            RoomMessage.StartGame("spell", GameMode.VERSUS, 42L),
            RoomMessage.GameEvent("g", """{"answer":3}"""),
            RoomMessage.Leave("h"),
            RoomMessage.Sync("records", "[]"),
        )

        messages.forEach { message -> Truth.assertThat(RoomCodec.decode(RoomCodec.encode(message))).isEqualTo(message) }
    }

    @Test
    fun `type discriminator is on the wire`() {
        Truth.assertThat(RoomCodec.encode(RoomMessage.Leave("p1"))).contains(""""type":"leave"""")
    }

    @Test
    fun `unknown fields are ignored`() {
        Truth.assertThat(RoomCodec.decode("""{"type":"leave","playerId":"p1","extra":1}""")).isEqualTo(RoomMessage.Leave("p1"))
    }

    @Test
    fun `unknown type and garbage decode to null`() {
        Truth.assertThat(RoomCodec.decode("""{"type":"chat","text":"x"}""")).isNull()
        Truth.assertThat(RoomCodec.decode("not json")).isNull()
    }
}
