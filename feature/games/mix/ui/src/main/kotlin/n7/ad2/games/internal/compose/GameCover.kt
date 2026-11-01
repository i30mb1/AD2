package n7.ad2.games.internal.compose

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.games.mid.CreepType
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperCreep
import n7.ad2.games.internal.paper.PaperEdge
import n7.ad2.games.internal.paper.PaperLaneBackground

private const val ASSETS = "file:///android_asset/"

/**
 * Обложка игры из картинок Доты, которые уже лежат в ассетах приложения: каждая показывает, о чем игра.
 * Детали масштабируются от [size], обложка читается и на 48dp, и крупно.
 */
@Composable
internal fun GameCover(game: GameVO, size: Dp, modifier: Modifier = Modifier, shape: Shape = RoundedCornerShape(size * 0.18f)) {
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Color(0xFF111215)),
    ) {
        when (game) {
            is GameVO.Show -> ShowCover(size)
            is GameVO.Mid -> MidCover(size)
            is GameVO.Invoker -> InvokerCover(size)
            is GameVO.XO -> XoCover()
        }
    }
}

/** Карточки-ответы веером на столе и золотой вопрос: викторина */
@Composable
private fun BoxScope.ShowCover(size: Dp) {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF4A3B2C), Color(0xFF1E1915)))))
    val cards = listOf(Triple("heroes/pudge/full.webp", -16f, -0.2f), Triple("heroes/lion/full.webp", 16f, 0.2f), Triple("heroes/axe/full.webp", 0f, 0f))
    cards.forEach { (path, angle, shift) ->
        Box(
            Modifier
                .align(Alignment.Center)
                .offset(x = size * shift, y = if (shift == 0f) -size * 0.04f else size * 0.02f)
                .rotate(angle)
                .size(width = size * 0.4f, height = size * 0.54f)
                .background(PaperCard, RoundedCornerShape(size * 0.06f))
                .border(size * 0.015f, PaperEdge, RoundedCornerShape(size * 0.06f))
                .padding(size * 0.03f),
        ) {
            Asset(path, Modifier.fillMaxSize().clip(RoundedCornerShape(size * 0.04f)))
        }
    }
    Badge(
        text = "?",
        modifier = Modifier
            .align(Alignment.BottomEnd)
            .padding(size * 0.05f)
            .size(size * 0.3f)
            .background(Color(0xFFD9A23A), CircleShape)
            .border(size * 0.02f, PaperEdge, CircleShape),
        size = size * 0.17f,
        color = Ink,
    )
}

/** Бумажная линия у реки: крип Света и крип Тьмы друг напротив друга */
@Composable
private fun BoxScope.MidCover(size: Dp) {
    PaperLaneBackground(Modifier.fillMaxSize())
    PaperCreep(
        CreepType.MELEE,
        Side.RADIANT,
        killable = false,
        modifier = Modifier.align(Alignment.BottomStart).padding(start = size * 0.04f, bottom = size * 0.06f).size(size * 0.5f),
    )
    PaperCreep(
        CreepType.MELEE,
        Side.DIRE,
        killable = true,
        modifier = Modifier.align(Alignment.TopEnd).padding(end = size * 0.04f, top = size * 0.04f).size(size * 0.5f).graphicsLayer { scaleX = -1f },
    )
}

/** Санстрайк, а вокруг три сферы: собери заклинание */
@Composable
private fun BoxScope.InvokerCover(size: Dp) {
    Box(Modifier.fillMaxSize().background(Brush.radialGradient(listOf(Color(0xFF3B2A4A), Color(0xFF15111C)))))
    val spellShape = RoundedCornerShape(size * 0.08f)
    Asset("images/abilities/invoker_sun_strike.webp", Modifier.align(Alignment.Center).size(size * 0.46f).clip(spellShape).border(size * 0.015f, PaperEdge, spellShape))
    listOf("quas" to Alignment.TopStart, "wex" to Alignment.TopEnd, "exort" to Alignment.BottomCenter).forEach { (orb, place) ->
        Asset(
            "images/abilities/invoker_$orb.webp",
            Modifier.align(place).padding(size * 0.05f).size(size * 0.27f).clip(CircleShape).border(size * 0.015f, PaperEdge, CircleShape),
        )
    }
}

/** Крестики 9×9: девять досок на поле Света и Тьмы, две уже взяты - большой X и большой O */
@Composable
private fun XoCover() {
    // доска: ее клетки (x, o, .) или взявший ее (X, O)
    val boards = listOf("X", "x.o.x.o..", "o..x....x", ".o.x.o...", "O", "..x.o..x.", "x....o...", ".x..o.x..", "o.x.x....")
    Canvas(Modifier.fillMaxSize()) {
        drawRect(Color(0xFF1F4D2B))
        drawPath(Path().apply { moveTo(this@Canvas.size.width, 0f); lineTo(this@Canvas.size.width, this@Canvas.size.height); lineTo(0f, this@Canvas.size.height); close() }, Color(0xFF4D1F1F))
        val pad = this.size.minDimension * 0.08f
        val gap = this.size.minDimension * 0.04f
        val board = (this.size.minDimension - pad * 2 - gap * 2) / 3
        boards.forEachIndexed { index, cells ->
            val origin = Offset(pad + index % 3 * (board + gap), pad + index / 3 * (board + gap))
            drawRoundRect(Color(0x66000000), origin, Size(board, board), CornerRadius(board * 0.16f))
            when (cells) {
                "X", "O" -> drawMark(cells == "X", origin + Offset(board * 0.2f, board * 0.2f), board * 0.6f, board * 0.13f)
                else -> cells.forEachIndexed { cell, mark ->
                    if (mark == '.') return@forEachIndexed
                    val step = board / 3
                    drawMark(mark == 'x', origin + Offset(cell % 3 * step + step * 0.25f, cell / 3 * step + step * 0.25f), step * 0.5f, step * 0.16f)
                }
            }
        }
    }
}

/** Крестик или нолик в квадрате [side] от [topLeft] */
private fun DrawScope.drawMark(cross: Boolean, topLeft: Offset, side: Float, stroke: Float) {
    if (cross) {
        val color = Color(0xFF7FE08A)
        drawLine(color, topLeft, topLeft + Offset(side, side), stroke, StrokeCap.Round)
        drawLine(color, topLeft + Offset(side, 0f), topLeft + Offset(0f, side), stroke, StrokeCap.Round)
    } else {
        drawCircle(Color(0xFFFF8A80), side / 2, topLeft + Offset(side / 2, side / 2), style = Stroke(stroke))
    }
}

@Composable
private fun Asset(path: String, modifier: Modifier, alignment: Alignment = Alignment.Center) {
    AsyncImage(
        model = ASSETS + path,
        contentDescription = null,
        contentScale = ContentScale.Crop,
        alignment = alignment,
        modifier = modifier,
    )
}

@Composable
private fun Badge(text: String, modifier: Modifier, size: Dp, color: Color = Color.White) {
    Box(modifier, contentAlignment = Alignment.Center) {
        Text(text, color = color, fontSize = (size.value).sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 420)
@Composable
private fun GameCoverPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val res = object : n7.ad2.Resources {
        override fun getString(resourceID: Int): String = context.getString(resourceID)
        override fun getString(resourceID: Int, formatArgs: Any): String = context.getString(resourceID, formatArgs)
        override fun getAssets(path: String) = throw UnsupportedOperationException()
        override fun getExternalFilesDir(path: String) = null
        override fun getFilesDir() = null
        override fun getConfiguration() = null
    }
    Row(Modifier.padding(8.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        listOf(GameVO.Show(res), GameVO.Mid(res), GameVO.Invoker(res), GameVO.XO(res)).forEach { GameCover(it, 96.dp) }
    }
}
