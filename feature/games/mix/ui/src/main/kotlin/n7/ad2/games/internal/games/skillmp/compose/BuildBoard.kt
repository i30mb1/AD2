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
import n7.ad2.games.internal.compose.DimColor
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.LineColor
import n7.ad2.games.internal.compose.MeColor
import n7.ad2.games.internal.compose.RightColor
import n7.ad2.games.internal.games.skillmp.BuildQuestion
import n7.ad2.games.internal.games.skillmp.BuildTile

/** «Собери предмет»: карточка с целью и слотами, под ней сетка вариантов и кнопка */
@Composable
internal fun ColumnScope.BuildBoard(
    build: BuildQuestion,
    selected: Set<Int>,
    showAnswer: Boolean,
    onToggle: (Int) -> Unit,
    onBuild: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(AppTheme.color.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = build.target.imageUrl,
                contentDescription = build.target.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)),
            )
            Column {
                Text(stringResource(R.string.build_pick, build.slots), color = DimColor, fontSize = 12.sp)
                Text(build.target.title, color = AppTheme.color.textColor, fontWeight = FontWeight.Bold, fontSize = 17.sp)
                Text(build.target.cost.toString(), color = GoldColor, fontSize = 13.sp)
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val chosen = selected.toList()
            repeat(build.slots) { slot ->
                val tile = chosen.getOrNull(slot)?.let { build.tiles[it] }
                val shape = RoundedCornerShape(10.dp)
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(56.dp)
                        .clip(shape)
                        .background(if (tile != null) LineColor else Color.Transparent)
                        .border(1.dp, if (tile != null) MeColor else DimColor.copy(alpha = 0.5f), shape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (tile != null) TileImage(tile) else Text("+", color = DimColor, fontSize = 20.sp)
                }
            }
        }
    }
    Spacer(Modifier.height(12.dp))
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        build.tiles.chunked(4).forEachIndexed { rowIndex, row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.fillMaxWidth()) {
                row.forEachIndexed { column, tile ->
                    val index = rowIndex * 4 + column
                    Tile(tile, isSelected = index in selected, showAnswer = showAnswer, modifier = Modifier.weight(1f)) { onToggle(index) }
                }
            }
        }
    }
    val ready = selected.size == build.slots && !showAnswer
    Text(
        text = stringResource(R.string.build_button),
        color = Color.White,
        fontSize = 15.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (ready) 1f else 0.4f)
            .clip(RoundedCornerShape(12.dp))
            .background(MeColor)
            .clickable(enabled = ready, onClick = onBuild)
            .padding(15.dp),
    )
}

@Composable
private fun Tile(tile: BuildTile, isSelected: Boolean, showAnswer: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val border = when {
        showAnswer && tile.isCorrect -> RightColor
        showAnswer && isSelected -> MeColor
        isSelected -> MeColor
        else -> Color.Transparent
    }
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .background(AppTheme.color.surface, shape)
            .border(1.dp, border, shape)
            .clickable(enabled = !showAnswer, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // выбранная плитка уже лежит в слоте, здесь она бледнеет
        val contentAlpha = if (isSelected && !showAnswer) 0.45f else 1f
        Box(Modifier.size(48.dp, 36.dp).clip(RoundedCornerShape(6.dp)).background(LineColor).alpha(contentAlpha), contentAlignment = Alignment.Center) { TileImage(tile) }
        Text(
            text = tile.item?.cost?.toString() ?: stringResource(R.string.build_recipe),
            color = GoldColor,
            fontSize = 11.sp,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp).alpha(contentAlpha),
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
