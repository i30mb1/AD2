import com.google.common.truth.Truth
import n7.ad2.games.internal.games.killCreep.KillCreepMatch
import n7.ad2.games.room.GameMode
import org.junit.Test

internal class KillCreepMatchTest {

    @Test
    fun `solo kill gives gold, fifth early tap ends`() {
        var match = KillCreepMatch.start(GameMode.SOLO, isHost = true)

        match = match.myKill(id = 1, gold = 38, timeMs = 1_000)
        Truth.assertThat(match.myGold).isEqualTo(38)
        Truth.assertThat(match.score).isEqualTo(38)

        repeat(4) { match = match.myEarly() }
        Truth.assertThat(match.misses).isEqualTo(4)
        Truth.assertThat(match.isOver).isFalse()

        match = match.myEarly()
        Truth.assertThat(match.isOver).isTrue()
    }

    @Test
    fun `creeps finished by allies are counted as lost, not as misses`() {
        var match = KillCreepMatch.start(GameMode.SOLO, isHost = true).myKill(1, 38, 1_000)
        repeat(10) { match = match.allyKill(100 + it) }
        match = match.allyKill(1)

        Truth.assertThat(match.lost).isEqualTo(10)
        Truth.assertThat(match.misses).isEqualTo(0)
        Truth.assertThat(match.isOver).isFalse()
    }

    @Test
    fun `versus peer kill goes to the peer and the creep is gone for me`() {
        val match = KillCreepMatch.start(GameMode.VERSUS, isHost = true).peerKill(id = 1, gold = 45, timeMs = 2_000)

        Truth.assertThat(match.peerGold).isEqualTo(45)
        Truth.assertThat(match.isTaken(1)).isTrue()
        Truth.assertThat(match.myKill(1, 45, 2_500).myGold).isEqualTo(0)
    }

    @Test
    fun `versus conflict - the earlier tap wins on both phones`() {
        val host = KillCreepMatch.start(GameMode.VERSUS, isHost = true).myKill(1, 45, 2_000).peerKill(1, 45, 1_900)
        val guest = KillCreepMatch.start(GameMode.VERSUS, isHost = false).myKill(1, 45, 1_900).peerKill(1, 45, 2_000)

        Truth.assertThat(host.myGold to host.peerGold).isEqualTo(0 to 45)
        Truth.assertThat(guest.myGold to guest.peerGold).isEqualTo(45 to 0)
    }

    @Test
    fun `versus tie goes to the host`() {
        val host = KillCreepMatch.start(GameMode.VERSUS, isHost = true).myKill(1, 45, 2_000).peerKill(1, 45, 2_000)
        val guest = KillCreepMatch.start(GameMode.VERSUS, isHost = false).peerKill(1, 45, 2_000).myKill(1, 45, 2_000)

        Truth.assertThat(host.myGold).isEqualTo(45)
        Truth.assertThat(guest.myGold).isEqualTo(0)
        Truth.assertThat(guest.peerGold).isEqualTo(45)
    }

    @Test
    fun `versus has no miss limit and ends by time`() {
        var match = KillCreepMatch.start(GameMode.VERSUS, isHost = true)
        repeat(10) { match = match.myEarly() }
        Truth.assertThat(match.isOver).isFalse()

        Truth.assertThat(match.timeUp().isOver).isTrue()
    }

    @Test
    fun `coop shares gold and misses, eighth miss ends`() {
        var match = KillCreepMatch.start(GameMode.COOP, isHost = true).myKill(1, 38, 1_000).peerKill(2, 45, 1_200)
        Truth.assertThat(match.score).isEqualTo(83)

        repeat(4) { match = match.peerEarly() }
        repeat(3) { match = match.myEarly() }
        repeat(5) { match = match.allyKill(100 + it) }
        Truth.assertThat(match.isOver).isFalse()
        match = match.myEarly()
        Truth.assertThat(match.misses).isEqualTo(8)
        Truth.assertThat(match.isOver).isTrue()
    }

    @Test
    fun `peer leaving ends the game`() {
        val match = KillCreepMatch.start(GameMode.VERSUS, isHost = true).peerLeft()

        Truth.assertThat(match.isOver).isTrue()
        Truth.assertThat(match.peerLeft).isTrue()
    }
}
