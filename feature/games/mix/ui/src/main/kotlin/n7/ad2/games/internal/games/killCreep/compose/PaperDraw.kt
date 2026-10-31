package n7.ad2.games.internal.games.killCreep.compose

import android.graphics.BlurMaskFilter
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.PaintingStyle
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import kotlin.math.max
import n7.ad2.games.internal.games.killCreep.CreepType

/** Деталь вместе с готовым контуром: контуры строятся один раз, а рисуются каждый кадр */
private class Piece(val part: PaperPart, val path: Path)

private fun List<PaperPart>.toPieces() = map { Piece(it, it.toPath()) }

private fun PaperPart.toPath(): Path = Path().apply {
    when (val part = this@toPath) {
        is PaperPath -> part.commands.forEach { (command, n) ->
            when (command) {
                'M' -> moveTo(n[0], n[1])
                'L' -> lineTo(n[0], n[1])
                'Q' -> quadraticTo(n[0], n[1], n[2], n[3])
                'Z' -> close()
            }
        }
        is PaperCircle -> addOval(Rect(Offset(part.cx, part.cy), part.r))
        is PaperOval -> addOval(Rect(part.cx - part.rx, part.cy - part.ry, part.cx + part.rx, part.cy + part.ry))
        is PaperRect -> addRoundRect(RoundRect(part.x, part.y, part.x + part.w, part.y + part.h, CornerRadius(part.radius)))
    }
}

/**
 * Слой бумаги: мягкая тень со смещением [lift], светлая кромка [edgeWidth] вокруг силуэта, затем сами детали.
 * Кромка рисуется под всеми деталями слоя сразу, поэтому видна только по внешнему краю.
 */
private fun DrawScope.paperLayer(pieces: List<Piece>, edge: Color, edgeWidth: Float, lift: Float, shadow: Paint) {
    if (lift > 0f) {
        translate(lift * 0.4f, lift) {
            drawIntoCanvas { canvas ->
                shadow.asFrameworkPaint().maskFilter = BlurMaskFilter(lift * 0.5f, BlurMaskFilter.Blur.NORMAL)
                pieces.forEach { piece ->
                    shadow.style = PaintingStyle.Stroke
                    shadow.strokeWidth = edgeWidth + piece.part.stroke
                    canvas.drawPath(piece.path, shadow)
                    if (piece.part.stroke == 0f) {
                        shadow.style = PaintingStyle.Fill
                        canvas.drawPath(piece.path, shadow)
                    }
                }
            }
        }
    }
    if (edgeWidth > 0f) {
        pieces.forEach { drawPath(it.path, edge, style = Stroke(edgeWidth + it.part.stroke, cap = StrokeCap.Round, join = StrokeJoin.Round)) }
    }
    pieces.forEach { piece ->
        if (piece.part.stroke > 0f) {
            drawPath(piece.path, piece.part.color, style = Stroke(piece.part.stroke, cap = StrokeCap.Round))
        } else {
            drawPath(piece.path, piece.part.color)
        }
    }
}

private fun shadowPaint() = Paint().apply { color = Color.Black.copy(alpha = 0.5f) }

/** Бумажный крип: когда его можно добить, кромка золотеет, а сам он приподнимается над полем и наклоняется */
@Composable
internal fun PaperCreep(type: CreepType, killable: Boolean, modifier: Modifier = Modifier) {
    val body = remember(type) { PaperCreeps.figure(type).body.toPieces() }
    val details = remember(type) { PaperCreeps.figure(type).details.toPieces() }
    val shadow = remember { shadowPaint() }
    val look = PaperLook.of(killable)
    val edge by animateColorAsState(look.edge)
    val lift by animateFloatAsState(look.lift)
    val tilt by animateFloatAsState(look.tilt)
    Canvas(
        modifier.graphicsLayer {
            rotationZ = tilt
            translationY = -(lift - PaperLook.of(killable = false).lift) / 100f * size.height
            transformOrigin = TransformOrigin(0.5f, 0.9f)
        },
    ) {
        val unit = size.minDimension / 100f
        scale(unit, unit, pivot = Offset.Zero) {
            paperLayer(body, edge, look.edgeWidth, lift, shadow)
            paperLayer(details, edge, edgeWidth = 0f, lift = 1.4f, shadow)
        }
    }
}

/** Фон поля из бумаги, растянут на всё поле с сохранением пропорций (лишнее обрезается) */
@Composable
internal fun PaperLaneBackground(modifier: Modifier = Modifier) {
    val groups = remember { PaperLane.groups.map { it to it.parts.toPieces() } }
    val shadow = remember { shadowPaint() }
    Canvas(modifier) {
        drawRect(PaperLane.Grass)
        val unit = max(size.width / PaperLane.WIDTH, size.height / PaperLane.HEIGHT)
        translate((size.width - PaperLane.WIDTH * unit) / 2, (size.height - PaperLane.HEIGHT * unit) / 2) {
            scale(unit, unit, pivot = Offset.Zero) {
                groups.forEach { (group, pieces) ->
                    paperLayer(pieces, PaperColors.Edge, group.edge, lift = if (group.edge > 0f) 5f else 0f, shadow)
                }
            }
        }
    }
}

@Preview(backgroundColor = 0xFF35532F, showBackground = true)
@Composable
private fun PaperCreepsPreview() {
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        CreepType.entries.forEach { PaperCreep(it, killable = it == CreepType.RANGED, Modifier.size(72.dp)) }
    }
}

@Preview(widthDp = 360, heightDp = 680)
@Composable
private fun PaperLanePreview() {
    PaperLaneBackground(Modifier.size(360.dp, 680.dp))
}
