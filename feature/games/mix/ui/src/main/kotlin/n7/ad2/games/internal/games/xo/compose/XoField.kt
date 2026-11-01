package n7.ad2.games.internal.games.xo.compose

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.role
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.xo.Mark
import n7.ad2.games.internal.games.xo.XoGame
import n7.ad2.games.internal.games.xo.XoMove
import n7.ad2.games.internal.games.xo.XoViewModel
import n7.ad2.games.room.GameMode

internal val XColor = Color(0xFF7FE08A)
internal val OColor = Color(0xFFFF8A80)
internal val XoGold = Color(0xFFD9A23A)
private val BoardColor = Color(0xFF2F3136)
private val ActiveBoard = Color(0xFF3A3326)
private val CellColor = Color(0xFF202225)
private val LastMove = Color(0xFF6B5522)
private val WonX = Color(0xFF1F4D2B)
private val WonO = Color(0xFF4D1F1F)

private val BoardGap = 6.dp
private val BoardPadding = 4.dp
private val CellGap = 2.dp

/** Smaller cells are hard to hit: a tap on a board zooms it in first */
private val MinCell = 44.dp

internal fun Mark.color(): Color = if (this == Mark.X) XColor else OColor

/**
 * The big field. Cells big enough are played right away; small ones (a phone) - a tap zooms the board in,
 * a tap on its cell makes the move. [zoom] is the board zoomed in, null - the whole field.
 */
@Composable
internal fun XoField(state: XoViewModel.State, zoom: Int?, onZoom: (Int?) -> Unit, onMove: (XoMove) -> Unit, modifier: Modifier = Modifier) {
    BoxWithConstraints(modifier.aspectRatio(1f)) {
        val board = (maxWidth - BoardGap * 2) / 3
        val cell = (board - BoardPadding * 2 - CellGap * 2) / 3
        val direct = cell >= MinCell
        AnimatedContent(
            targetState = zoom.takeUnless { direct },
            transitionSpec = { (fadeIn(tween(160)) + scaleIn(tween(220), initialScale = 0.9f)) togetherWith fadeOut(tween(120)) },
            label = "zoom",
        ) { zoomed ->
            if (zoomed == null) {
                Overview(state, board, cell, direct, onBoard = { onZoom(it) }, onMove = onMove)
            } else {
                Zoomed(state, zoomed, onMove)
            }
        }
    }
}

@Composable
private fun Overview(state: XoViewModel.State, boardSize: Dp, cellSize: Dp, direct: Boolean, onBoard: (Int) -> Unit, onMove: (XoMove) -> Unit) {
    Column(Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(BoardGap)) {
        repeat(3) { row ->
            Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(BoardGap)) {
                repeat(3) { column ->
                    SmallBoard(state, row * 3 + column, boardSize, cellSize, direct, onBoard, onMove, Modifier.weight(1f).fillMaxHeight())
                }
            }
        }
    }
}

@Composable
private fun SmallBoard(
    state: XoViewModel.State,
    board: Int,
    boardSize: Dp,
    cellSize: Dp,
    direct: Boolean,
    onBoard: (Int) -> Unit,
    onMove: (XoMove) -> Unit,
    modifier: Modifier,
) {
    val game = state.game
    val owner = game.boards[board]
    // a resign or a draw ends the game with boards still open: nothing to go to
    val open = state.result == null && game.isPlayable(board)
    val mine = state.myTurn && open
    // one board to go to is ringed, a free choice tints all the open ones
    val ringed = (open && game.target != null) || state.result?.line?.contains(board) == true
    val shape = RoundedCornerShape(12.dp)
    val density = LocalDensity.current
    val boardName = stringResource(BOARD_NAMES[board])
    Box(
        modifier
            .clip(shape)
            .background(
                when {
                    owner == Mark.X -> WonX
                    owner == Mark.O -> WonO
                    open -> ActiveBoard
                    else -> BoardColor
                },
            )
            .then(
                if (mine && !direct) {
                    Modifier
                        .semantics { contentDescription = boardName }
                        .clickable(role = Role.Button) { onBoard(board) }
                } else {
                    Modifier
                },
            ),
    ) {
        Cells(Modifier.fillMaxSize().padding(BoardPadding), CellGap) { cell, cellModifier ->
            val mark = game.cell(board, cell)
            val playable = mine && direct && mark == null
            Cell(
                mark = mark,
                last = game.last == XoMove(board, cell),
                fontSize = with(density) { (cellSize * 0.52f).toSp() },
                modifier = cellModifier,
                onClick = if (playable) ({ onMove(XoMove(board, cell)) }) else null,
            )
        }
        if (owner != null) {
            Box(Modifier.matchParentSize().background((if (owner == Mark.X) WonX else WonO).copy(alpha = 0.78f)), contentAlignment = Alignment.Center) {
                Text(owner.name, color = owner.color(), fontSize = with(density) { (boardSize * 0.62f).toSp() }, fontWeight = FontWeight.ExtraBold)
            }
        }
        if (ringed) Box(Modifier.matchParentSize().border(3.dp, XoGold, shape))
    }
}

/** The zoomed board: every empty cell shows the board it sends the opponent to */
@Composable
private fun Zoomed(state: XoViewModel.State, board: Int, onMove: (XoMove) -> Unit) {
    val game = state.game
    val shape = RoundedCornerShape(16.dp)
    BoxWithConstraints(Modifier.fillMaxSize().clip(shape).background(ActiveBoard).border(3.dp, XoGold, shape)) {
        val cellSize = (maxWidth - 12.dp * 2 - 8.dp * 2) / 3
        val density = LocalDensity.current
        val anyBoard = stringResource(R.string.xo_any_board)
        Cells(Modifier.fillMaxSize().padding(12.dp), 8.dp) { cell, cellModifier ->
            val move = XoMove(board, cell)
            val mark = game.cell(board, cell)
            // the board fading out after my move is drawn with the field of the opponent's turn: no more look ahead there
            val next = game.playOrNull(move)
            if (mark != null || next == null) {
                Cell(mark, last = game.last == move, fontSize = with(density) { (cellSize * 0.5f).toSp() }, modifier = cellModifier, onClick = null, corner = 10.dp)
            } else {
                val sendsTo = next.target
                val label = if (sendsTo == null) anyBoard else stringResource(BOARD_NAMES[sendsTo])
                Box(
                    cellModifier
                        .clip(RoundedCornerShape(10.dp))
                        .background(CellColor)
                        .semantics { contentDescription = label }
                        .clickable(role = Role.Button) { onMove(move) },
                    contentAlignment = Alignment.Center,
                ) {
                    if (sendsTo == null) {
                        Text(anyBoard, color = XoGold, fontSize = with(density) { (cellSize * 0.13f).toSp() }, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
                    } else {
                        MiniBoard(next, sendsTo, Modifier.fillMaxSize(0.56f).alpha(0.55f))
                    }
                }
            }
        }
    }
}

/** A small picture of a board: where a move sends the opponent */
@Composable
private fun MiniBoard(game: XoGame, board: Int, modifier: Modifier) {
    BoxWithConstraints(modifier) {
        val density = LocalDensity.current
        val fontSize = with(density) { (maxWidth / 3 * 0.6f).toSp() }
        Cells(Modifier.fillMaxSize(), 2.dp) { cell, cellModifier ->
            Box(cellModifier.clip(RoundedCornerShape(3.dp)).background(BoardColor), contentAlignment = Alignment.Center) {
                val mark = game.cell(board, cell)
                if (mark != null) Text(mark.name, color = mark.color(), fontSize = fontSize, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
private fun Cell(mark: Mark?, last: Boolean, fontSize: TextUnit, modifier: Modifier, onClick: (() -> Unit)?, corner: Dp = 5.dp) {
    Box(
        modifier
            .clip(RoundedCornerShape(corner))
            .background(if (last) LastMove else CellColor)
            .then(if (onClick != null) Modifier.clickable(role = Role.Button, onClick = onClick) else Modifier),
        contentAlignment = Alignment.Center,
    ) {
        if (mark != null) Text(mark.name, color = mark.color(), fontSize = fontSize, fontWeight = FontWeight.ExtraBold)
    }
}

/** A 3×3 grid of [content] cells with [gap] between them */
@Composable
private fun Cells(modifier: Modifier, gap: Dp, content: @Composable (cell: Int, modifier: Modifier) -> Unit) {
    Column(modifier, verticalArrangement = Arrangement.spacedBy(gap)) {
        repeat(3) { row ->
            Row(Modifier.weight(1f).fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(gap)) {
                repeat(3) { column -> content(row * 3 + column, Modifier.weight(1f).fillMaxHeight()) }
            }
        }
    }
}

/** The places of the boards on the field, for the hints */
internal val BOARD_NAMES = listOf(
    R.string.xo_board_0, R.string.xo_board_1, R.string.xo_board_2,
    R.string.xo_board_3, R.string.xo_board_4, R.string.xo_board_5,
    R.string.xo_board_6, R.string.xo_board_7, R.string.xo_board_8,
)

/** The middle of a game as on the canvas: X took the boards 1 and 8, O the board 5, O just went to the 3rd cell of the 6th board */
internal fun previewGame(): XoGame {
    val raw = listOf("XXXO.O...", "O.X..X.O.", "..O.X....", "X..O.X..O", "O.XO.XO..", ".XO.O....", "..X.X.O..", "X.O.X.O.X", "O....X...")
    val cells = raw.joinToString("").map { char -> Mark.entries.firstOrNull { it.name.first() == char } }
    return XoGame(cells = cells, turn = Mark.X, target = 2, last = XoMove(5, 2))
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 420)
@Composable
private fun XoFieldZoomedPreview() {
    AppTheme(isDarkTheme = true) {
        XoField(
            XoViewModel.State(GameMode.VERSUS, previewGame(), Mark.X, peerName = "Pixel 8", peerReady = true),
            zoom = 2,
            onZoom = {},
            onMove = {},
            modifier = Modifier.fillMaxWidth().padding(12.dp),
        )
    }
}
