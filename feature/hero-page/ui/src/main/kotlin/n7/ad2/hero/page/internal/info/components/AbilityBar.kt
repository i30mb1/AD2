package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.hero.page.internal.info.domain.vo.VOAghanim
import n7.ad2.hero.page.internal.info.domain.vo.VOUpgrade
import n7.ad2.heroes.domain.model.Ability

/** Key of the bar entry with both Aghanim's upgrades, ability names never start with a zero char. */
internal const val AGHANIM_TAB = "\u0000aghanim"

private val ICON_SIZE = 44.dp
private val aghanimTabBackground = Color(0xFF22303D)
private val aghanimTabBorder = Color(0xFF37587A)

/**
 * Abilities in the order the game shows them, the innate one apart, with their default keys under them, like on the
 * hero page of dota2.com. Abilities that Aghanim's Scepter or Shard changes wear its mark, and the last entry shows
 * what both items give, so nobody has to tap through every ability to find it.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AbilityBar(
    groups: List<VOAbilityGroup>,
    hotkeys: Map<String, String>,
    aghanim: VOAghanim,
    selected: String,
    onSelect: (String) -> Unit,
) {
    val changedByScepter = aghanim.scepter.filterIsInstance<VOUpgrade.Change>().mapTo(HashSet()) { it.ability.name }
    val changedByShard = aghanim.shard.filterIsInstance<VOUpgrade.Change>().mapTo(HashSet()) { it.ability.name }
    FlowRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
            .selectableGroup(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        groups.forEachIndexed { index, group ->
            if (index > 0) GroupDivider()
            val isInnate = group.group == VOAbilityGroup.Group.INNATE
            group.abilities.forEach { ability ->
                AbilityTab(
                    ability = ability,
                    label = if (isInnate) stringResource(R.string.ability_innate_short) else hotkeys[ability.name].orEmpty(),
                    badge = when (ability.name) {
                        in changedByScepter -> SCEPTER_SYMBOL
                        in changedByShard -> SHARD_SYMBOL
                        else -> null
                    },
                    isRound = isInnate,
                    isSelected = ability.name == selected,
                    onClick = { onSelect(ability.name) },
                )
            }
        }
        GroupDivider()
        AghanimTab(isSelected = selected == AGHANIM_TAB, onClick = { onSelect(AGHANIM_TAB) })
    }
}

@Composable
private fun GroupDivider() {
    Box(
        Modifier
            .padding(top = 6.dp)
            .size(width = 1.dp, height = 32.dp)
            .background(InfoColors.divider),
    )
}

@Composable
private fun AbilityTab(ability: Ability, label: String, badge: String?, isRound: Boolean, isSelected: Boolean, onClick: () -> Unit) {
    Column(
        modifier = Modifier
            .width(ICON_SIZE)
            .selectable(selected = isSelected, role = Role.Tab, onClick = onClick)
            .semantics { contentDescription = ability.name },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(Modifier.size(ICON_SIZE)) {
            AsyncImage(
                model = ability.imageUrl,
                contentDescription = null,
                placeholder = painterResource(n7.ad2.core.ui.R.drawable.square_placeholder),
                error = painterResource(n7.ad2.core.ui.R.drawable.square_error_placeholder),
                modifier = Modifier
                    .size(ICON_SIZE)
                    .selectionRing(isSelected, isRound, AppTheme.color.primary)
                    .clip(if (isRound) CircleShape else RoundedCornerShape(8.dp)),
            )
            if (badge != null) {
                AsyncImage(
                    model = badge,
                    contentDescription = null,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .offset(x = 6.dp, y = (-6).dp)
                        .size(18.dp)
                        .clip(CircleShape)
                        .background(AppTheme.color.background)
                        .padding(1.dp),
                )
            }
        }
        TabLabel(label, if (isSelected) AppTheme.color.textColor else AppTheme.color.textSecondaryColor)
    }
}

@Composable
private fun AghanimTab(isSelected: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(8.dp)
    Column(
        modifier = Modifier.selectable(selected = isSelected, role = Role.Tab, onClick = onClick),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .size(width = 88.dp, height = ICON_SIZE)
                .selectionRing(isSelected, isRound = false, color = AppTheme.color.primary)
                .clip(shape)
                .background(aghanimTabBackground)
                .border(1.dp, aghanimTabBorder, shape),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterHorizontally),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            AsyncImage(model = SCEPTER_SYMBOL, contentDescription = null, modifier = Modifier.size(22.dp))
            AsyncImage(model = SHARD_SYMBOL, contentDescription = null, modifier = Modifier.size(22.dp))
        }
        TabLabel(stringResource(R.string.aghanim_tab), if (isSelected) AppTheme.color.textColor else InfoColors.aghanim)
    }
}

@Composable
private fun TabLabel(text: String, color: Color) {
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        color = color,
        maxLines = 1,
        modifier = Modifier.padding(top = 6.dp),
    )
}

/** A ring a little outside the icon, so the picked one stands out without covering its art. */
private fun Modifier.selectionRing(isSelected: Boolean, isRound: Boolean, color: Color): Modifier = if (!isSelected) {
    this
} else {
    drawBehind {
        val gap = 3.dp.toPx()
        val stroke = 2.dp.toPx()
        val outset = gap + stroke / 2
        val radius = if (isRound) size.minDimension / 2 + outset else 8.dp.toPx() + outset
        drawRoundRect(
            color = color,
            topLeft = Offset(-outset, -outset),
            size = Size(size.width + outset * 2, size.height + outset * 2),
            cornerRadius = CornerRadius(radius),
            style = Stroke(stroke),
        )
    }
}
