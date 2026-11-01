import com.google.common.truth.Truth
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import n7.ad2.games.internal.party.LanPresence
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.PartyCodec
import n7.ad2.games.internal.party.Lobby
import n7.ad2.games.internal.party.LobbyCodec
import n7.ad2.games.internal.party.Via
import n7.ad2.games.internal.party.isPreferredHost
import n7.ad2.games.internal.party.mergeNearby
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.Player
import org.junit.Test

internal class PartyTest {

    private val host = Player("h", "Host")
    private val anna = Player("a", "Anna")
    private val boris = Player("b", "Boris")
    private val three = listOf(host, anna, boris)

    @Test
    fun `a new pick of the host makes everyone pick ready again, the same pick keeps them`() {
        val lobby = Lobby().pick("show", GameMode.VERSUS).ready(anna.id, true)

        Truth.assertThat(lobby.pick("show", GameMode.VERSUS).ready).containsExactly(anna.id)
        Truth.assertThat(lobby.pick("show", GameMode.COOP)).isEqualTo(Lobby("show", GameMode.COOP))
        Truth.assertThat(lobby.pick("invoker", GameMode.VERSUS).ready).isEmpty()
        Truth.assertThat(lobby.ready(anna.id, false).ready).isEmpty()
    }

    @Test
    fun `the host counts as ready and starts once every guest is ready for a game that fits them all`() {
        val lobby = Lobby().pick("show", GameMode.VERSUS).ready(anna.id, true)

        Truth.assertThat(lobby.readyCount(three)).isEqualTo(2)
        Truth.assertThat(lobby.isReady(host.id, three)).isTrue()
        Truth.assertThat(lobby.canStart(three)).isFalse()
        Truth.assertThat(lobby.ready(boris.id, true).canStart(three)).isTrue()
    }

    @Test
    fun `no start without a game, alone, for too many players or in a mode the game has not`() {
        val everyone = setOf(anna.id, boris.id)

        Truth.assertThat(Lobby(ready = everyone).canStart(three)).isFalse()
        Truth.assertThat(Lobby("show", GameMode.VERSUS).canStart(listOf(host))).isFalse()
        Truth.assertThat(Lobby("mid", GameMode.VERSUS, everyone).canStart(three)).isFalse()
        Truth.assertThat(Lobby("mid", GameMode.VERSUS, everyone).canStart(listOf(host, anna))).isTrue()
        Truth.assertThat(Lobby("xo", GameMode.COOP, everyone).canStart(listOf(host, anna))).isFalse()
    }

    @Test
    fun `after a game nobody is ready, who left is dropped`() {
        val lobby = Lobby("show", GameMode.VERSUS, setOf(anna.id, boris.id))

        Truth.assertThat(lobby.reset()).isEqualTo(Lobby("show", GameMode.VERSUS))
        Truth.assertThat(lobby.forPlayers(listOf(host, boris)).ready).containsExactly(boris.id)
    }

    @Test
    fun `the lobby and a ready go over the wire and back`() {
        val lobby = Lobby("show", GameMode.COOP, setOf(anna.id, boris.id))

        Truth.assertThat(LobbyCodec.decode(LobbyCodec.encode(lobby))).isEqualTo(LobbyCodec.Message.State(lobby))
        Truth.assertThat(LobbyCodec.decode(LobbyCodec.encode(Lobby()))).isEqualTo(LobbyCodec.Message.State(Lobby()))
        Truth.assertThat(LobbyCodec.decode(LobbyCodec.encodeReady(true))).isEqualTo(LobbyCodec.Message.Ready(true))
        Truth.assertThat(LobbyCodec.decode(LobbyCodec.encodeReady(false))).isEqualTo(LobbyCodec.Message.Ready(false))
        Truth.assertThat(LobbyCodec.decode("l;show;NOPE;")).isNull()
        Truth.assertThat(LobbyCodec.decode("garbage")).isNull()
    }

    @Test
    fun `WHEN presence name is encoded THEN name and id come back, even with # in the name`() {
        val name = PartyCodec.presenceName("Pixel #8", "id-1")

        Truth.assertThat(PartyCodec.parsePresence(name)).isEqualTo("Pixel #8" to "id-1")
    }

    @Test
    fun `WHEN NSD renamed the service on a name conflict THEN the id is still recognized`() {
        // своя старая регистрация еще жива: Android переименовывает новую в "имя (2)", иначе находим сами себя
        val renamed = PartyCodec.presenceName("Pixel", "me") + " (2)"

        Truth.assertThat(PartyCodec.parsePresence(renamed)).isEqualTo("Pixel" to "me")
        Truth.assertThat(mergeNearby(listOf(LanPresence(renamed, "192.168.0.2", 4001)), emptyList(), myId = "me")).isEmpty()
    }

    @Test
    fun `WHEN service is not ours THEN it is not a player`() {
        Truth.assertThat(PartyCodec.parsePresence("XO server")).isNull()
    }

    @Test
    fun `WHEN the same player is found over Wi-Fi and Wi-Fi Direct THEN Wi-Fi wins and own entries are dropped`() {
        val lan = listOf(
            LanPresence(PartyCodec.presenceName("Pixel", "p1"), "192.168.0.5", 4000),
            LanPresence(PartyCodec.presenceName("Me", "me"), "192.168.0.2", 4001),
            LanPresence("XO server", "192.168.0.9", 4002),
        )
        val direct = listOf(WifiDirectRoom("Pixel", "aa:bb", 4000, "p1"), WifiDirectRoom("Galaxy", "cc:dd", 5000, "p2"))

        val nearby = mergeNearby(lan, direct, myId = "me")

        Truth.assertThat(nearby).containsExactly(
            NearbyPlayer("p1", "Pixel", Via.WIFI, ip = "192.168.0.5", port = 4000, deviceAddress = null),
            NearbyPlayer("p2", "Galaxy", Via.DIRECT, ip = null, port = 5000, deviceAddress = "cc:dd"),
        ).inOrder()
    }

    @Test
    fun `WHEN both phones joined each other THEN both keep the room hosted by the bigger id`() {
        // a и b одновременно нажали «Сыграть»: у каждого две комнаты, оставить надо одну и ту же
        Truth.assertThat(isPreferredHost(hostId = "b", myId = "a", peerId = "b")).isTrue()
        Truth.assertThat(isPreferredHost(hostId = "b", myId = "b", peerId = "a")).isTrue()
        Truth.assertThat(isPreferredHost(hostId = "a", myId = "a", peerId = "b")).isFalse()
        Truth.assertThat(isPreferredHost(hostId = "a", myId = "b", peerId = "a")).isFalse()
    }
}
