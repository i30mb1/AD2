package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroSummary

private val prosColor = Color(0xFF81C784)
private val consColor = Color(0xFFFF8A80)

/** What to know before the game: strong and weak points, who to fear and what the hero is built around. */
@Composable
internal fun SummaryCard(summary: VOHeroSummary) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Caption(stringResource(R.string.info_summary))
        if (summary.pros.isNotEmpty() || summary.cons.isNotEmpty()) {
            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                summary.pros.forEach { Point("+", it, prosColor) }
                summary.cons.forEach { Point("−", it, consColor) }
            }
        }
        if (summary.threats.isNotEmpty() || summary.keyItems.isNotEmpty()) {
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                if (summary.threats.isNotEmpty()) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Caption(stringResource(R.string.info_threats), color = consColor)
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            summary.threats.forEach { hero ->
                                AsyncImage(
                                    model = hero.imageUrl,
                                    contentDescription = hero.heroName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(28.dp)
                                        .clip(CircleShape)
                                        .background(InfoColors.chip),
                                )
                            }
                        }
                    }
                }
                if (summary.keyItems.isNotEmpty()) {
                    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
                        Caption(stringResource(R.string.info_key_items))
                        Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            summary.keyItems.forEach { item ->
                                AsyncImage(
                                    model = item.imageUrl,
                                    contentDescription = item.itemName,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .size(width = 36.dp, height = 27.dp)
                                        .clip(RoundedCornerShape(4.dp))
                                        .background(InfoColors.chip),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun Point(sign: String, text: String, color: Color) {
    Row {
        Text(sign, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = color, modifier = Modifier.padding(end = 6.dp))
        Text(text, fontSize = 13.sp, lineHeight = 18.sp, color = AppTheme.color.textColor)
    }
}
