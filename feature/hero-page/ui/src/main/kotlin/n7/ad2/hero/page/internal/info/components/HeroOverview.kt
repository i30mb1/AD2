package n7.ad2.hero.page.internal.info.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroRole
import n7.ad2.heroes.domain.model.HeroStats

@Composable
internal fun AttributeIcon(attribute: HeroAttribute, modifier: Modifier = Modifier) {
    when (attribute) {
        HeroAttribute.STRENGTH -> Image(painterResource(R.drawable.strength), null, modifier)
        HeroAttribute.AGILITY -> Image(painterResource(R.drawable.agility), null, modifier)
        HeroAttribute.INTELLIGENCE -> Image(painterResource(R.drawable.intelligence), null, modifier)
        // there is no universal icon in the app, the three attributes together mean the same in game
        HeroAttribute.UNIVERSAL -> Box(modifier) {
            Image(painterResource(R.drawable.strength), null, Modifier.fillMaxSize(0.62f).align(Alignment.TopCenter))
            Image(painterResource(R.drawable.agility), null, Modifier.fillMaxSize(0.62f).align(Alignment.BottomStart))
            Image(painterResource(R.drawable.intelligence), null, Modifier.fillMaxSize(0.62f).align(Alignment.BottomEnd))
        }
    }
}

@Composable
internal fun StatsSection(stats: HeroStats) {
    val regen = R.string.stat_regen
    val rows = listOf(
        StatRow(R.string.stat_health, stats.health.format(), stringResource(regen, stats.healthRegen.format()), InfoColors.health),
        StatRow(R.string.stat_mana, stats.mana.format(), stringResource(regen, stats.manaRegen.format()), InfoColors.mana),
        StatRow(R.string.stat_damage, "${stats.damageMin.format()}–${stats.damageMax.format()}"),
        StatRow(R.string.stat_armor, stats.armor.format()),
        StatRow(R.string.stat_move_speed, stats.moveSpeed.format()),
        StatRow(R.string.stat_attack_range, stats.attackRange.format()),
        StatRow(R.string.stat_attack_rate, "${stats.attackRate.format()}s"),
        StatRow(R.string.stat_magic_resist, "${stats.magicResist.format()}%"),
        StatRow(R.string.stat_projectile_speed, stats.projectileSpeed.format()).takeIf { stats.projectileSpeed > 0 && stats.attackRange > 150 },
        StatRow(R.string.stat_turn_rate, stats.turnRate.format()),
        StatRow(R.string.stat_vision, "${stats.visionDay.format()} / ${stats.visionNight.format()}"),
    ).filterNotNull()

    // every stat is visible at once as a tile, people compare heroes by these numbers
    SectionBody {
        // plain rows instead of FlowRow: FlowRow measures weighted tiles of different heights wrong
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
            rows.chunked(STATS_IN_ROW).forEach { line ->
                Row(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    line.forEach { row ->
                        Column(
                            modifier = Modifier
                                .weight(1f)
                                .fillMaxHeight()
                                .clip(AppTheme.shape.small)
                                .background(InfoColors.chip)
                                .padding(horizontal = 4.dp, vertical = 8.dp),
                            horizontalAlignment = Alignment.CenterHorizontally,
                        ) {
                            Text(text = row.value, fontSize = 15.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor, maxLines = 1)
                            if (row.extra != null) Text(text = row.extra, style = AppTheme.style.info, color = row.accent ?: AppTheme.color.textSecondaryColor, maxLines = 1)
                            Text(
                                text = stringResource(row.label),
                                style = AppTheme.style.body,
                                color = AppTheme.color.textSecondaryColor,
                                maxLines = 2,
                                textAlign = TextAlign.Center,
                            )
                        }
                    }
                    repeat(STATS_IN_ROW - line.size) { Box(Modifier.weight(1f)) }
                }
            }
        }
    }
}

private const val STATS_IN_ROW = 4

private class StatRow(val label: Int, val value: String, val extra: String? = null, val accent: Color? = null)
