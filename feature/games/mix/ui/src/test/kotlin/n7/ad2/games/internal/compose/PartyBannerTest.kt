package n7.ad2.games.internal.compose

import com.google.common.truth.Truth.assertThat
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.PartyState
import n7.ad2.games.internal.party.Lobby
import n7.ad2.games.internal.party.Session
import n7.ad2.games.internal.party.Via
import n7.ad2.games.internal.party.PartyFailure
import n7.ad2.games.room.Player
import org.junit.Test

class PartyBannerTest {

    private val pixel = NearbyPlayer("p", "Pixel 8", Via.DIRECT, ip = null, port = 0, deviceAddress = "aa")

    @Test
    fun `nobody around shows the searching banner`() {
        assertThat(partyBanner(PartyState())).isEqualTo(PartyBanner.ALONE)
    }

    @Test
    fun `players around are offered, connecting and failure win over the list`() {
        assertThat(partyBanner(PartyState(nearby = listOf(pixel)))).isEqualTo(PartyBanner.NEARBY)
        assertThat(partyBanner(PartyState(nearby = listOf(pixel), connecting = true))).isEqualTo(PartyBanner.CONNECTING)
        assertThat(partyBanner(PartyState(nearby = listOf(pixel), failure = PartyFailure.FULL))).isEqualTo(PartyBanner.FAILED)
    }

    @Test
    fun `a session means the room`() {
        val session = Session(listOf(Player("p", "Pixel 8"), Player("me", "Me")), meId = "me", lobby = Lobby(), via = Via.DIRECT)
        assertThat(partyBanner(PartyState(nearby = listOf(pixel), session = session))).isEqualTo(PartyBanner.TOGETHER)
    }
}
