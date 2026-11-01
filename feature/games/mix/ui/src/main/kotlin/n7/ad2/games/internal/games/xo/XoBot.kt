package n7.ad2.games.internal.games.xo

import kotlin.random.Random

/**
 * The bot's move: it wins the game when it can, takes and blocks small boards, does not send the opponent where they
 * take a board or the game, keeps the free choice for itself and likes centers and corners. [random] varies equal moves.
 */
internal fun botMove(game: XoGame, random: Random): XoMove {
    val me = game.turn
    return game.moves.maxBy { move -> rate(game, move, me) + random.nextDouble() * NOISE }
}

private fun rate(game: XoGame, move: XoMove, me: Mark): Double {
    val next = game.play(move)
    next.result?.let { result ->
        return when (result.winner) {
            me -> WIN_GAME
            null -> 0.0
            else -> -WIN_GAME
        }
    }
    val opponent = me.other
    var score = CELL_WEIGHT[move.cell] + BOARD_WEIGHT[move.board]
    if (next.boards[move.board] == me) {
        score += TAKE_BOARD + LINE_BOARD * lineValue(game.boards, move.board, me)
    } else {
        if (completes(game, move.board, move.cell, opponent)) score += BLOCK + LINE_BOARD * lineValue(game.boards, move.board, opponent)
        score += THREAT * (threats(next, move.board, me) - threats(game, move.board, me))
    }
    // where the opponent goes: the boards they may play and what they take there
    val reachable = (0 until XoGame.BOARDS).filter(next::isPlayable)
    if (next.target == null) score -= FREE_CHOICE
    reachable.forEach { board ->
        if (!canTake(next, board, opponent)) return@forEach
        score -= if (winsGame(next.boards, board, opponent)) WIN_GAME / 10 else GIVE_BOARD + LINE_BOARD * lineValue(next.boards, board, opponent)
    }
    return score
}

/** [mark] at [cell] makes three in a row on [board] */
private fun completes(game: XoGame, board: Int, cell: Int, mark: Mark): Boolean {
    if (game.cell(board, cell) != null) return false
    return winner(LINES_THROUGH[cell]) { if (it == cell) mark else game.cell(board, it) } == mark
}

private fun canTake(game: XoGame, board: Int, mark: Mark): Boolean = game.isOpen(board) && (0 until XoGame.BOARDS).any { completes(game, board, it, mark) }

/** Lines of [board] with two marks of [mark] and an empty cell */
private fun threats(game: XoGame, board: Int, mark: Mark): Int = LINES.count { line ->
    line.count { game.cell(board, it) == mark } == 2 && line.any { game.cell(board, it) == null }
}

/** How much taking [board] helps [mark] on the big field: lines through it still free of the other mark, more for a line with another board of [mark] */
private fun lineValue(boards: List<Mark?>, board: Int, mark: Mark): Int = LINES_THROUGH[board].sumOf { line ->
    if (line.any { boards[it] == mark.other }) 0 else 1 + line.count { it != board && boards[it] == mark }
}

private fun winsGame(boards: List<Mark?>, board: Int, mark: Mark): Boolean =
    LINES_THROUGH[board].any { line -> line.all { it == board || boards[it] == mark } }

private const val WIN_GAME = 1_000_000.0
private const val TAKE_BOARD = 100.0
private const val BLOCK = 60.0
private const val GIVE_BOARD = 70.0
private const val LINE_BOARD = 12.0
private const val FREE_CHOICE = 30.0
private const val THREAT = 8.0
private const val NOISE = 4.0

private val LINES_THROUGH: List<List<List<Int>>> = List(XoGame.BOARDS) { cell -> LINES.filter { cell in it } }

// the center sits on 4 lines, a corner on 3, a side on 2
private val CELL_WEIGHT = doubleArrayOf(2.0, 1.0, 2.0, 1.0, 3.0, 1.0, 2.0, 1.0, 2.0)
private val BOARD_WEIGHT = doubleArrayOf(1.0, 0.5, 1.0, 0.5, 1.5, 0.5, 1.0, 0.5, 1.0)
