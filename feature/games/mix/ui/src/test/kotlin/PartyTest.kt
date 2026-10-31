import com.google.common.truth.Truth
import n7.ad2.feature.games.xo.domain.model.WifiDirectRoom
import n7.ad2.games.internal.party.LanPresence
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.PartyCodec
import n7.ad2.games.internal.party.PartyPicks
import n7.ad2.games.internal.party.Pick
import n7.ad2.games.internal.party.Via
import n7.ad2.games.internal.party.isPreferredHost
import n7.ad2.games.internal.party.mergeNearby
import n7.ad2.games.room.GameMode
import org.junit.Test

internal class PartyTest {

    private val spellVersus = Pick("spell", GameMode.VERSUS)

    @Test
    fun `WHEN both picked the same game and mode THEN it is a match`() {
        val picks = PartyPicks().pick(spellVersus).peerPicked(spellVersus)

        Truth.assertThat(picks.match).isEqualTo(spellVersus)
    }

    @Test
    fun `WHEN modes differ or one changed the mind THEN no match`() {
        val picks = PartyPicks().pick(spellVersus).peerPicked(Pick("spell", GameMode.COOP))

        Truth.assertThat(picks.match).isNull()
        Truth.assertThat(PartyPicks().pick(spellVersus).peerPicked(spellVersus).cancel().match).isNull()
    }

    @Test
    fun `WHEN pick goes over the wire THEN it comes back the same, cancel included`() {
        Truth.assertThat(PartyCodec.decodePick(PartyCodec.encodePick(spellVersus))).isEqualTo(PartyCodec.Picked(spellVersus))
        Truth.assertThat(PartyCodec.decodePick(PartyCodec.encodePick(null))).isEqualTo(PartyCodec.Picked(null))
        Truth.assertThat(PartyCodec.decodePick("spell;NOPE")).isNull()
        Truth.assertThat(PartyCodec.decodePick("garbage")).isNull()
    }

    @Test
    fun `WHEN presence name is encoded THEN name and id come back, even with # in the name`() {
        val name = PartyCodec.presenceName("Pixel #8", "id-1")

        Truth.assertThat(PartyCodec.parsePresence(name)).isEqualTo("Pixel #8" to "id-1")
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
