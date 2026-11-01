import com.google.common.truth.Truth
import kotlin.random.Random
import n7.ad2.games.internal.games.xo.Mark
import n7.ad2.games.internal.games.xo.XoCodec
import n7.ad2.games.internal.games.xo.XoEvent
import n7.ad2.games.internal.games.xo.XoGame
import n7.ad2.games.internal.games.xo.XoMove
import n7.ad2.games.internal.games.xo.XoResult
import n7.ad2.games.internal.games.xo.XoStreak
import n7.ad2.games.internal.games.xo.botMove
import org.junit.Test

internal class XoRulesTest {

    /** The field from nine small boards written as in the canvas: X, O and . for an empty cell */
    private fun game(vararg boards: String, turn: Mark = Mark.X, target: Int? = null): XoGame {
        require(boards.size == 9 && boards.all { it.length == 9 })
        val cells = boards.joinToString("").map { char ->
            when (char) {
                'X' -> Mark.X
                'O' -> Mark.O
                else -> null
            }
        }
        return XoGame(cells = cells, turn = turn, target = target)
    }

    private val empty = "........."

    @Test
    fun `X starts, a move sends the opponent to the board of the same place`() {
        val next = XoGame().play(XoMove(board = 4, cell = 2))

        Truth.assertThat(next.turn).isEqualTo(Mark.O)
        Truth.assertThat(next.target).isEqualTo(2)
        Truth.assertThat(next.last).isEqualTo(XoMove(4, 2))
        Truth.assertThat(next.cell(4, 2)).isEqualTo(Mark.X)
        Truth.assertThat(next.canPlay(XoMove(2, 0))).isTrue()
        Truth.assertThat(next.canPlay(XoMove(4, 0))).isFalse()
        Truth.assertThat(next.moves).hasSize(9)
    }

    @Test
    fun `a look ahead at a move that cannot be played gives nothing instead of failing`() {
        val game = XoGame().play(XoMove(4, 2))

        Truth.assertThat(game.playOrNull(XoMove(2, 0))).isEqualTo(game.play(XoMove(2, 0)))
        Truth.assertThat(game.playOrNull(XoMove(4, 0))).isNull()
        Truth.assertThat(game.playOrNull(XoMove(4, 2))).isNull()
    }

    @Test
    fun `a taken cell cannot be played`() {
        val next = XoGame().play(XoMove(4, 4))

        Truth.assertThat(next.canPlay(XoMove(4, 4))).isFalse()
        Truth.assertThat(next.moves).doesNotContain(XoMove(4, 4))
        Truth.assertThat(next.moves).hasSize(8)
    }

    @Test
    fun `three in a row take the small board and close it`() {
        val before = game("XX.OO....", empty, empty, empty, empty, empty, empty, empty, empty, turn = Mark.X, target = 0)

        val after = before.play(XoMove(0, 2))

        Truth.assertThat(before.boards[0]).isNull()
        Truth.assertThat(after.boards[0]).isEqualTo(Mark.X)
        Truth.assertThat(after.taken(Mark.X)).isEqualTo(1)
        Truth.assertThat(after.isOpen(0)).isFalse()
        Truth.assertThat(after.canPlay(XoMove(0, 5))).isFalse()
    }

    @Test
    fun `sent to a taken or a full board the player picks any open board`() {
        val taken = game(empty, empty, empty, empty, "....X....", empty, empty, empty, "XXX......", turn = Mark.X, target = 4)
            .play(XoMove(4, 8))

        Truth.assertThat(taken.target).isNull()
        Truth.assertThat(taken.canPlay(XoMove(0, 0))).isTrue()
        Truth.assertThat(taken.canPlay(XoMove(8, 4))).isFalse()

        val full = game(empty, "XOXXOOOXX", empty, empty, empty, empty, empty, empty, empty, turn = Mark.O, target = 3)
            .play(XoMove(3, 1))

        Truth.assertThat(full.boards[1]).isNull()
        Truth.assertThat(full.isOpen(1)).isFalse()
        Truth.assertThat(full.target).isNull()
        Truth.assertThat(full.moves.map { it.board }.toSet()).containsExactly(0, 2, 3, 4, 5, 6, 7, 8)
    }

    @Test
    fun `three boards in a row win the game and nothing can be played after`() {
        val before = game("XXX......", "OOO......", empty, empty, "XXX......", "OO.......", empty, empty, "XX.......", turn = Mark.X, target = 8)

        val after = before.play(XoMove(8, 2))

        Truth.assertThat(after.result).isEqualTo(XoResult(Mark.X, XoResult.Reason.LINE, line = listOf(0, 4, 8)))
        Truth.assertThat(after.moves).isEmpty()
        Truth.assertThat(after.canPlay(XoMove(2, 0))).isFalse()
        Truth.assertThat(before.result).isNull()
    }

    @Test
    fun `the full field without a line goes to whoever took more boards, equal boards are a draw`() {
        // X took 0, 5, 7, O took 1, 3, 8, the boards 2 and 6 are full without a winner, board 4 has the last empty cell
        val draw = "XOXXOOOXX"
        val boards = arrayOf("XXX......", "OOO......", draw, "OOO......", "XOOOXXXX.", "XXX......", draw, "XXX......", "OOO......")

        val xMore = game(*boards, turn = Mark.X, target = 4).play(XoMove(4, 8))
        Truth.assertThat(xMore.boards[4]).isEqualTo(Mark.X)
        Truth.assertThat(xMore.result).isEqualTo(XoResult(Mark.X, XoResult.Reason.BOARDS))

        val equal = game(*boards, turn = Mark.O, target = 4).play(XoMove(4, 8))
        Truth.assertThat(equal.boards[4]).isNull()
        Truth.assertThat(equal.result).isEqualTo(XoResult(null, XoResult.Reason.FULL))
    }

    @Test
    fun `the codec passes moves and the draw and resign events`() {
        val events = listOf(
            XoEvent.Hello,
            XoEvent.HelloBack,
            XoEvent.Move(XoMove(3, 7), number = 12),
            XoEvent.Resign,
            XoEvent.OfferDraw,
            XoEvent.AcceptDraw,
            XoEvent.DeclineDraw,
        )

        events.forEach { event -> Truth.assertThat(XoCodec.decode(XoCodec.encode(event))).isEqualTo(event) }
        Truth.assertThat(XoCodec.decode("m;9;0;1")).isNull()
        Truth.assertThat(XoCodec.decode("m;1;x;1")).isNull()
        Truth.assertThat(XoCodec.decode("s;10;3")).isNull()
    }

    @Test
    fun `the bot takes the game when it can`() {
        val position = game("OOO......", "OO.......", "OOO......", empty, empty, empty, empty, empty, empty, turn = Mark.O, target = 1)

        Truth.assertThat(botMove(position, Random(1))).isEqualTo(XoMove(1, 2))
    }

    @Test
    fun `the bot takes a small board and blocks the opponent's one`() {
        val take = game("OO.......", empty, empty, empty, empty, empty, empty, empty, empty, turn = Mark.O, target = 0)
        Truth.assertThat(botMove(take, Random(2))).isEqualTo(XoMove(0, 2))

        val block = game("XX.......", empty, empty, empty, empty, empty, empty, empty, empty, turn = Mark.O, target = 0)
        Truth.assertThat(botMove(block, Random(3))).isEqualTo(XoMove(0, 2))
    }

    @Test
    fun `the bot does not send the opponent where the opponent wins the game`() {
        // X has the boards 0 and 4 and two in a row on the board 8
        val position = game("XXX......", empty, empty, empty, "XXX......", empty, empty, empty, "XX.......", turn = Mark.O, target = 2)

        repeat(20) { seed ->
            val move = botMove(position, Random(seed))
            Truth.assertThat(move.board).isEqualTo(2)
            // the cell 8 sends X to the board 8, the cells 0 and 4 to the taken boards: X would pick the board 8 itself
            Truth.assertThat(move.cell).isNotIn(listOf(0, 4, 8))
        }
    }

    @Test
    fun `bots play whole games to the end with legal moves only`() {
        repeat(30) { seed ->
            val random = Random(seed)
            var game = XoGame()
            while (game.result == null) {
                val move = botMove(game, random)
                Truth.assertThat(game.canPlay(move)).isTrue()
                game = game.play(move)
            }
            Truth.assertThat(game.moves).isEmpty()
        }
    }

    @Test
    fun `the bot beats a player that moves at random`() {
        val wins = (0 until 20).count { seed ->
            val random = Random(seed)
            var game = XoGame()
            while (game.result == null) {
                game = game.play(if (game.turn == Mark.O) botMove(game, random) else game.moves.random(random))
            }
            game.result?.winner == Mark.O
        }

        Truth.assertThat(wins).isAtLeast(18)
    }

    @Test
    fun `the number of moves counts the marks on the field`() {
        val game = XoGame().play(XoMove(4, 4)).play(XoMove(4, 0)).play(XoMove(0, 8))

        Truth.assertThat(game.moveCount).isEqualTo(3)
    }

    @Test
    fun `wins in a row grow with a win and start over after a loss or a draw`() {
        val streak = XoStreak().won().won()

        Truth.assertThat(streak.current).isEqualTo(2)
        Truth.assertThat(streak.lost().current).isEqualTo(0)
        Truth.assertThat(streak.lost().won().current).isEqualTo(1)
    }
}
