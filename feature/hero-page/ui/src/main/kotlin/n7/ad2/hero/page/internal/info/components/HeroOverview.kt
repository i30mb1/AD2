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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.heroes.domain.model.HeroAttribute
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroRole
import n7.ad2.heroes.domain.model.HeroStats

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun HeroHeader(imageUrl: String, info: HeroDescription) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .clip(AppTheme.shape.large)
            .background(AppTheme.color.surface),
    ) {
        Box(
            Modifier
                .fillMaxWidth()
                .aspectRatio(16f / 9f),
        ) {
            AsyncImage(
                model = imageUrl,
                contentDescription = info.name,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize(),
            )
            Box(
                Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(0.45f to Color.Transparent, 1f to AppTheme.color.surface)),
            )
            Column(
                Modifier
                    .align(Alignment.BottomStart)
                    .padding(horizontal = 16.dp, vertical = 8.dp),
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    AttributeIcon(info.primaryAttribute, Modifier.size(22.dp))
                    HSpace(8.dp)
                    Text(
                        text = info.name,
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.textColor,
                    )
                }
            }
        }
        Column(Modifier.padding(start = 16.dp, end = 16.dp, bottom = 16.dp)) {
            if (info.short.isNotEmpty()) {
                RichText(info.short, color = AppTheme.color.textSecondaryColor)
                VSpace(12.dp)
            }
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                InfoChip(
                    text = stringResource(info.primaryAttribute.label),
                    color = info.primaryAttribute.color,
                    leading = { AttributeIcon(info.primaryAttribute, Modifier.size(14.dp)) },
                )
                InfoChip(text = stringResource(info.attackType.label))
                ComplexityChip(info.complexity)
            }
            if (info.roles.isNotEmpty()) {
                VSpace(8.dp)
                Roles(info.roles)
            }
            VSpace(14.dp)
            Attributes(info)
        }
    }
}

@Composable
private fun ComplexityChip(complexity: Int) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(50))
            .background(InfoColors.chip)
            .padding(horizontal = 10.dp, vertical = 5.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = stringResource(R.string.complexity), style = AppTheme.style.body, color = AppTheme.color.textColor)
        HSpace(2.dp)
        repeat(3) { index ->
            val isActive = index < complexity
            Box(
                Modifier
                    .size(9.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(if (isActive) AppTheme.color.primary else Color.Transparent)
                    .border(1.dp, if (isActive) AppTheme.color.primary else AppTheme.color.textSecondaryColor, RoundedCornerShape(2.dp)),
            )
        }
    }
}

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

/** STR/AGI/INT with their gain per level, the primary one (all three for universal heroes) is highlighted. */
@Composable
private fun Attributes(info: HeroDescription) {
    val attributes = info.attributes
    val isUniversal = info.primaryAttribute == HeroAttribute.UNIVERSAL
    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AttributeTile(R.drawable.strength, HeroAttribute.STRENGTH, attributes.strength, attributes.strengthGain, isUniversal || info.primaryAttribute == HeroAttribute.STRENGTH, Modifier.weight(1f))
        AttributeTile(R.drawable.agility, HeroAttribute.AGILITY, attributes.agility, attributes.agilityGain, isUniversal || info.primaryAttribute == HeroAttribute.AGILITY, Modifier.weight(1f))
        AttributeTile(R.drawable.intelligence, HeroAttribute.INTELLIGENCE, attributes.intelligence, attributes.intelligenceGain, isUniversal || info.primaryAttribute == HeroAttribute.INTELLIGENCE, Modifier.weight(1f))
    }
}

@Composable
private fun AttributeTile(@DrawableRes icon: Int, attribute: HeroAttribute, base: Double, gain: Double, isPrimary: Boolean, modifier: Modifier) {
    val shape = AppTheme.shape.medium
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (isPrimary) attribute.color.copy(alpha = 0.14f) else InfoColors.chip)
            .border(1.dp, if (isPrimary) attribute.color.copy(alpha = 0.7f) else Color.Transparent, shape)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        Image(painterResource(icon), contentDescription = stringResource(attribute.label), modifier = Modifier.size(20.dp))
        HSpace(6.dp)
        Text(text = base.format(), fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
        HSpace(4.dp)
        Text(text = "+${gain.format()}", style = AppTheme.style.body, color = attribute.color)
    }
}

@Composable
internal fun StatsCard(stats: HeroStats) {
    val regen = R.string.stat_regen
    val rows = listOf(
        StatRow(R.string.stat_health, stats.health.format(), stringResource(regen, stats.healthRegen.format()), InfoColors.health),
        StatRow(R.string.stat_mana, stats.mana.format(), stringResource(regen, stats.manaRegen.format()), InfoColors.mana),
        StatRow(R.string.stat_damage, "${stats.damageMin.format()}–${stats.damageMax.format()}"),
        StatRow(R.string.stat_armor, stats.armor.format()),
        StatRow(R.string.stat_magic_resist, "${stats.magicResist.format()}%"),
        StatRow(R.string.stat_attack_range, stats.attackRange.format()),
        StatRow(R.string.stat_attack_rate, "${stats.attackRate.format()}s"),
        StatRow(R.string.stat_projectile_speed, stats.projectileSpeed.format()).takeIf { stats.projectileSpeed > 0 && stats.attackRange > 150 },
        StatRow(R.string.stat_move_speed, stats.moveSpeed.format()),
        StatRow(R.string.stat_turn_rate, stats.turnRate.format()),
        StatRow(R.string.stat_vision, "${stats.visionDay.format()} / ${stats.visionNight.format()}"),
    ).filterNotNull()
    // the values people compare heroes by
    val summary = rows.take(4).map { "${stringResource(it.label)} ${it.value}" }.joinToString(" · ")

    CollapsibleCard(title = stringResource(R.string.info_stats), summary = summary) {
        rows.forEachIndexed { index, row ->
            if (index > 0) InfoDivider(Modifier.padding(vertical = 8.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (row.accent != null) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(RoundedCornerShape(50))
                            .background(row.accent),
                    )
                    HSpace(8.dp)
                }
                Text(
                    text = stringResource(row.label),
                    style = AppTheme.style.H5,
                    color = AppTheme.color.textSecondaryColor,
                    modifier = Modifier.weight(1f),
                )
                Text(text = row.value, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                if (row.extra != null) {
                    HSpace(6.dp)
                    Text(text = row.extra, style = AppTheme.style.body, color = row.accent ?: AppTheme.color.textSecondaryColor)
                }
            }
        }
    }
}

private class StatRow(val label: Int, val value: String, val extra: String? = null, val accent: Color? = null)

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Roles(roles: List<HeroRole>) {
    FlowRow(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        roles.sortedByDescending { it.level }.forEach { role ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(50))
                    .background(InfoColors.chip)
                    .padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(role.role.label), style = AppTheme.style.body, color = AppTheme.color.textColor)
                HSpace(6.dp)
                repeat(3) { index ->
                    Box(
                        Modifier
                            .padding(start = if (index == 0) 0.dp else 2.dp)
                            .size(width = 8.dp, height = 3.dp)
                            .clip(RoundedCornerShape(2.dp))
                            .background(if (index < role.level) AppTheme.color.primary else AppTheme.color.textSecondaryColor.copy(alpha = 0.25f)),
                    )
                }
            }
        }
    }
}
