package n7.ad2.games.internal.games.skillmp.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.skillmp.BuildQuestion
import n7.ad2.games.internal.games.skillmp.BuildTile

private val Gold = Color(0xFFFFD166)
private val Selected = Color(0xFF448AFF)
private val Right = Color(0xFF43A047)
private val Wrong = Color(0xFFF44336)

/** «Собери предмет»: цель, слоты под выбранные компоненты и сетка вариантов */
@Composable
internal fun ColumnScope.BuildBoard(
    build: BuildQuestion,
    selected: Set<Int>,
    showAnswer: Boolean,
    onToggle: (Int) -> Unit,
    onBuild: () -> Unit,
) {
    Column(Modifier.weight(1f).fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
        AsyncImage(
            model = build.target.imageUrl,
            contentDescription = build.target.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(width = 112.dp, height = 82.dp).clip(RoundedCornerShape(10.dp)),
        )
        Text(build.target.title, color = AppTheme.color.textColor, fontWeight = FontWeight.SemiBold, fontSize = 18.sp, modifier = Modifier.padding(top = 8.dp))
        Text("🪙 ${build.target.cost}", color = Gold, style = AppTheme.style.info)
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 16.dp, bottom = 6.dp)) {
            val chosen = selected.toList()
            repeat(build.slots) { slot ->
                val tile = chosen.getOrNull(slot)?.let { build.tiles[it] }
                Box(
                    modifier = Modifier
                        .size(width = 56.dp, height = 42.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.5.dp, if (tile != null) Selected else Color(0xFF555555), RoundedCornerShape(8.dp))
                        .background(if (tile != null) AppTheme.color.surface else Color.Transparent),
                    contentAlignment = Alignment.Center,
                ) {
                    if (tile != null) TileImage(tile) else Text("?", color = AppTheme.color.textSecondaryColor)
                }
            }
        }
        Text(stringResource(R.string.build_pick, build.slots), color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info)
    }
    build.tiles.chunked(4).forEachIndexed { rowIndex, row ->
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)) {
            row.forEachIndexed { column, tile ->
                val index = rowIndex * 4 + column
                Tile(tile, isSelected = index in selected, showAnswer = showAnswer, modifier = Modifier.weight(1f)) { onToggle(index) }
            }
        }
    }
    val ready = selected.size == build.slots && !showAnswer
    Text(
        text = stringResource(R.string.build_button),
        color = Color.White,
        fontWeight = FontWeight.SemiBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp)
            .alpha(if (ready) 1f else 0.4f)
            .clip(RoundedCornerShape(12.dp))
            .background(Wrong)
            .clickable(enabled = ready, onClick = onBuild)
            .padding(14.dp),
    )
}

@Composable
private fun Tile(tile: BuildTile, isSelected: Boolean, showAnswer: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border = when {
        showAnswer && tile.isCorrect -> Right
        showAnswer && isSelected -> Wrong
        isSelected -> Selected
        else -> Color.Transparent
    }
    val shape = RoundedCornerShape(8.dp)
    // фон и рамка рисуются по форме без clip, прозрачность выбранной плитки - только у содержимого
    Column(
        modifier = modifier
            .aspectRatio(1.1f)
            .background(AppTheme.color.surface, shape)
            .border(2.dp, border, shape)
            .clickable(enabled = !showAnswer, onClick = onClick)
            .padding(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        val contentAlpha = if (isSelected && !showAnswer) 0.55f else 1f
        Box(Modifier.weight(1f).fillMaxWidth().alpha(contentAlpha), contentAlignment = Alignment.Center) { TileImage(tile) }
        Spacer(Modifier.height(2.dp))
        Text(
            text = tile.item?.cost?.toString() ?: stringResource(R.string.build_recipe),
            color = Gold,
            fontSize = 10.sp,
            maxLines = 1,
            modifier = Modifier.alpha(contentAlpha),
        )
    }
}

@Composable
private fun TileImage(tile: BuildTile) {
    val item = tile.item
    if (item == null) {
        Text("📜", fontSize = 20.sp)
    } else {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
        )
    }
}
