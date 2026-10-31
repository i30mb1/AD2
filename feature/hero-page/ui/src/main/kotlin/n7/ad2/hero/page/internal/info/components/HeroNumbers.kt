package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.clearAndSetSemantics
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.floor
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.STAT_LEVELS
import n7.ad2.hero.page.internal.info.domain.vo.statsAt
import n7.ad2.heroes.domain.model.AttackType
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.HeroRole
import n7.ad2.heroes.domain.model.HeroStats

private val healthBar = Color(0xFF2E7D32)
private val healthText = Color(0xFFE8F5E9)
private val manaBar = Color(0xFF1565C0)
private val manaText = Color(0xFFE3F2FD)
private val emptyDot = Color(0xFF55585F)
private const val MAX_ROLE_LEVEL = 3

/**
 * The hero in numbers, the way the game's hero page shows them: health and mana bars, the other stats as a table and
 * the roles. The numbers follow the picked [level], people compare how heroes grow, not only how they start.
 */
@Composable
internal fun HeroNumbersCard(description: HeroDescription, level: Int, onLevelChange: (Int) -> Unit) {
    val stats = remember(description, level) { description.statsAt(level) }
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.surface)
            .padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        if (description.short.isNotEmpty()) {
            Text(description.short, fontSize = 13.sp, lineHeight = 18.sp, color = AppTheme.color.textSecondaryColor)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            Caption(stringResource(R.string.info_hero_level), Modifier.weight(1f))
            LevelPicker(level, onLevelChange)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            ResourceBar(stringResource(R.string.stat_health), stats.health, stats.healthRegen, healthBar, healthText, Modifier.weight(1f))
            ResourceBar(stringResource(R.string.stat_mana), stats.mana, stats.manaRegen, manaBar, manaText, Modifier.weight(1f))
        }
        StatsTable(stats, description.attackType)
        if (description.roles.isNotEmpty()) {
            InfoDivider()
            Roles(description.roles)
        }
    }
}

@Composable
private fun LevelPicker(level: Int, onLevelChange: (Int) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.color.background)
            .padding(2.dp)
            .selectableGroup(),
    ) {
        STAT_LEVELS.forEach { option ->
            val isSelected = option == level
            Box(
                modifier = Modifier
                    .height(32.dp)
                    .widthIn(min = 44.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (isSelected) AppTheme.color.primary else Color.Transparent)
                    .selectable(selected = isSelected, role = Role.RadioButton) { onLevelChange(option) }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.toString(),
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else AppTheme.color.textSecondaryColor,
                )
            }
        }
    }
}

/** Health or mana as the game's bar: the amount on the left, the regeneration on the right. */
@Composable
private fun ResourceBar(label: String, value: Double, regen: Double, color: Color, textColor: Color, modifier: Modifier) {
    val amount = floor(value).toInt().toString()
    val regenText = stringResource(R.string.stat_regen, regen.format())
    Row(
        modifier = modifier
            .height(24.dp)
            .clip(RoundedCornerShape(6.dp))
            .background(color)
            .clearAndSetSemantics { contentDescription = "$label $amount, $regenText" }
            .padding(horizontal = 9.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(amount, fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Color.White, modifier = Modifier.weight(1f))
        Text(regenText, fontSize = 12.sp, color = textColor, maxLines = 1)
    }
}

@Composable
private fun StatsTable(stats: HeroStats, attackType: AttackType) {
    val rows = buildList {
        add(R.string.stat_damage to "${floor(stats.damageMin).toInt()}–${floor(stats.damageMax).toInt()}")
        add(R.string.stat_armor to stats.armor.format(1))
        add(R.string.stat_attack_rate to stats.attackRate.format())
        add(R.string.stat_magic_resist to "${stats.magicResist.format(1)}%")
        add(R.string.stat_attack_range to stats.attackRange.format())
        add(R.string.stat_move_speed to stats.moveSpeed.format())
        if (attackType == AttackType.RANGED && stats.projectileSpeed > 0) add(R.string.stat_projectile_speed to stats.projectileSpeed.format())
        add(R.string.stat_turn_rate to stats.turnRate.format())
        add(R.string.stat_vision to "${stats.visionDay.format()}/${stats.visionNight.format()}")
    }
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        rows.chunked(2).forEach { pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(22.dp)) {
                pair.forEach { (label, value) -> StatLine(stringResource(label), value, Modifier.weight(1f)) }
                if (pair.size == 1) Spacer(Modifier.weight(1f))
            }
        }
    }
}

@Composable
private fun StatLine(label: String, value: String, modifier: Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = label,
            fontSize = 13.sp,
            color = AppTheme.color.textSecondaryColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f),
        )
        HSpace(8.dp)
        Text(value, fontSize = 13.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor, maxLines = 1)
    }
}

/** Every role with its strength in dots, the strongest first. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Roles(roles: List<HeroRole>) {
    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Caption(stringResource(R.string.info_roles), Modifier.align(Alignment.CenterVertically).padding(end = 4.dp))
        roles.sortedByDescending { it.level }.forEach { role ->
            Row(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(InfoColors.chip)
                    .padding(horizontal = 9.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(stringResource(role.role.label), fontSize = 12.sp, color = AppTheme.color.textColor)
                HSpace(6.dp)
                repeat(MAX_ROLE_LEVEL) { index ->
                    Box(
                        Modifier
                            .padding(start = if (index > 0) 2.dp else 0.dp)
                            .size(5.dp)
                            .clip(CircleShape)
                            .background(if (index < role.level) Color.White else emptyDot),
                    )
                }
            }
        }
    }
}
