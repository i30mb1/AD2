package n7.ad2.games.internal.compose

import com.google.common.truth.Truth.assertThat
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.PartyState
import n7.ad2.games.internal.party.Pick
import n7.ad2.games.internal.party.Session
import n7.ad2.games.internal.party.Via
import n7.ad2.games.room.GameMode
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
        assertThat(partyBanner(PartyState(nearby = listOf(pixel), failed = true))).isEqualTo(PartyBanner.FAILED)
    }

    @Test
    fun `a session means picking together`() {
        val session = Session(Player("p", "Pixel 8"), Via.DIRECT)
        assertThat(partyBanner(PartyState(nearby = listOf(pixel), session = session))).isEqualTo(PartyBanner.TOGETHER)
    }

    @Test
    fun `game tile marks show who picked it in the chosen mode`() {
        val party = PartyState(myPick = Pick("spell", GameMode.VERSUS), peerPick = Pick("build", GameMode.COOP))
        assertThat(tileMarks("spell", party)).isEqualTo(true to false)
        assertThat(tileMarks("build", party)).isEqualTo(false to true)
        assertThat(tileMarks("hero", party)).isEqualTo(false to false)
    }
}
