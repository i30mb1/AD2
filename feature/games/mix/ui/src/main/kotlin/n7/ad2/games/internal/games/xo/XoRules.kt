package n7.ad2.games.internal.games.xo

import androidx.compose.runtime.Immutable

/** X is Radiant and moves first, O is Dire */
internal enum class Mark {
    X, O;

    val other: Mark get() = if (this == X) O else X
}

/** A cell of the big field: [board] 0..8 and [cell] 0..8 inside it, both left to right and top to bottom */
internal data class XoMove(val board: Int, val cell: Int)

/**
 * How a game ended; [winner] null - a draw. [line] - the three boards in a row of a [Reason.LINE] win.
 * The rules end a game with [Reason.LINE], [Reason.BOARDS] or [Reason.FULL], the players with the rest.
 */
internal data class XoResult(val winner: Mark?, val reason: Reason, val line: List<Int>? = null) {
    enum class Reason {
        /** Three boards in a row */
        LINE,

        /** The field is full without a line: more boards win */
        BOARDS,

        /** The field is full, the boards are equal */
        FULL,
        RESIGN,
        AGREED,
        LEFT,
    }
}

/** The rows, columns and diagonals of a 3×3 board, the same for a small board and for the big field */
internal val LINES: List<List<Int>> = listOf(
    listOf(0, 1, 2), listOf(3, 4, 5), listOf(6, 7, 8),
    listOf(0, 3, 6), listOf(1, 4, 7), listOf(2, 5, 8),
    listOf(0, 4, 8), listOf(2, 4, 6),
)

/**
 * Tic-tac-toe 9×9: nine small boards on a big field. The cell of a move sends the opponent to the board in the same
 * place; a board that is taken or full lets them pick any open one. Three in a row take a small board,
 * three taken boards in a row win the game.
 *
 * @param cells 81 cells, board by board: the cell `c` of the board `b` is `cells[b * 9 + c]`
 * @param target the board for the next move, null - any open board
 */
@Immutable
internal data class XoGame(
    val cells: List<Mark?> = List(CELLS) { null },
    val turn: Mark = Mark.X,
    val target: Int? = null,
    val last: XoMove? = null,
) {

    /** Who took each small board, null - nobody yet or a full board without a winner */
    val boards: List<Mark?> = List(BOARDS) { board -> winner(LINES) { cells[board * BOARDS + it] } }

    val result: XoResult? = resultOf()

    /** The moves the player of [turn] can make now, none after the end */
    val moves: List<XoMove>
        get() = if (result != null) {
            emptyList()
        } else {
            (0 until BOARDS).filter(::isPlayable).flatMap { board -> (0 until BOARDS).filter { cell(board, it) == null }.map { XoMove(board, it) } }
        }

    val moveCount: Int get() = cells.count { it != null }

    fun cell(board: Int, cell: Int): Mark? = cells[board * BOARDS + cell]

    fun taken(mark: Mark): Int = boards.count { it == mark }

    /** Not taken and has an empty cell */
    fun isOpen(board: Int): Boolean = boards[board] == null && (0 until BOARDS).any { cell(board, it) == null }

    /** The next move may go to this board */
    fun isPlayable(board: Int): Boolean = result == null && isOpen(board) && (target == null || target == board)

    fun canPlay(move: XoMove): Boolean = move.board in 0 until BOARDS && move.cell in 0 until BOARDS && isPlayable(move.board) && cell(move.board, move.cell) == null

    /** A look ahead: the field after [move], null when it cannot be played */
    fun playOrNull(move: XoMove): XoGame? = if (canPlay(move)) play(move) else null

    fun play(move: XoMove): XoGame {
        require(canPlay(move)) { "Illegal move $move" }
        val next = copy(cells = cells.toMutableList().also { it[move.board * BOARDS + move.cell] = turn }, turn = turn.other, last = move)
        return next.copy(target = move.cell.takeIf(next::isOpen))
    }

    private fun resultOf(): XoResult? {
        LINES.firstOrNull { line -> boards[line[0]] != null && line.all { boards[it] == boards[line[0]] } }?.let { line ->
            return XoResult(boards[line[0]], XoResult.Reason.LINE, line)
        }
        if ((0 until BOARDS).any(::isOpen)) return null
        val x = taken(Mark.X)
        val o = taken(Mark.O)
        return when {
            x > o -> XoResult(Mark.X, XoResult.Reason.BOARDS)
            o > x -> XoResult(Mark.O, XoResult.Reason.BOARDS)
            else -> XoResult(null, XoResult.Reason.FULL)
        }
    }

    companion object {
        const val BOARDS = 9
        const val CELLS = BOARDS * BOARDS
    }
}

/** The mark that fills one of [lines] of a 3×3 board read by [at], null - none */
internal inline fun winner(lines: List<List<Int>>, at: (Int) -> Mark?): Mark? {
    for (line in lines) {
        val mark = at(line[0]) ?: continue
        if (at(line[1]) == mark && at(line[2]) == mark) return mark
    }
    return null
}

/** Wins against the bot or a friend in a row, a loss or a draw starts over: the record of the game */
internal data class XoStreak(val current: Int = 0) {
    fun won() = copy(current = current + 1)

    fun lost() = copy(current = 0)
}

/** What a phone tells the other one during a game */
internal sealed interface XoEvent {
    /**
     * The screen of the game is open. A phone opens the game a moment after the other one and an event sent before
     * that is lost, so moves wait until each phone has heard [Hello] or [HelloBack] from the other one.
     */
    data object Hello : XoEvent
    data object HelloBack : XoEvent

    /** [number] - how many moves were made before this one: a lost or repeated move is not played */
    data class Move(val move: XoMove, val number: Int) : XoEvent
    data object Resign : XoEvent
    data object OfferDraw : XoEvent
    data object AcceptDraw : XoEvent
    data object DeclineDraw : XoEvent
}

/** "h", "h!", "m;board;cell;number", "r", "d?", "d!", "d-" */
internal object XoCodec {

    fun encode(event: XoEvent): String = when (event) {
        XoEvent.Hello -> HELLO
        XoEvent.HelloBack -> HELLO_BACK
        is XoEvent.Move -> "$MOVE;${event.move.board};${event.move.cell};${event.number}"
        XoEvent.Resign -> RESIGN
        XoEvent.OfferDraw -> OFFER
        XoEvent.AcceptDraw -> ACCEPT
        XoEvent.DeclineDraw -> DECLINE
    }

    fun decode(payload: String): XoEvent? = when (payload) {
        HELLO -> XoEvent.Hello
        HELLO_BACK -> XoEvent.HelloBack
        RESIGN -> XoEvent.Resign
        OFFER -> XoEvent.OfferDraw
        ACCEPT -> XoEvent.AcceptDraw
        DECLINE -> XoEvent.DeclineDraw
        else -> decodeMove(payload)
    }

    private fun decodeMove(payload: String): XoEvent.Move? {
        val parts = payload.split(";")
        if (parts.size != 4 || parts[0] != MOVE) return null
        val board = parts[1].toIntOrNull()?.takeIf { it in 0 until XoGame.BOARDS } ?: return null
        val cell = parts[2].toIntOrNull()?.takeIf { it in 0 until XoGame.BOARDS } ?: return null
        val number = parts[3].toIntOrNull() ?: return null
        return XoEvent.Move(XoMove(board, cell), number)
    }

    private const val HELLO = "h"
    private const val HELLO_BACK = "h!"
    private const val MOVE = "m"
    private const val RESIGN = "r"
    private const val OFFER = "d?"
    private const val ACCEPT = "d!"
    private const val DECLINE = "d-"
}
