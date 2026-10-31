package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOAghanim
import n7.ad2.hero.page.internal.info.domain.vo.VOUpgrade

internal const val SCEPTER_SYMBOL = "file:///android_asset/images/aghanim's_scepter_symbol.webp"
internal const val SHARD_SYMBOL = "file:///android_asset/images/aghanim's_shard_symbol.webp"
private const val SCEPTER_ITEM = "file:///android_asset/items/aghanim's_scepter/full.webp"
private const val SHARD_ITEM = "file:///android_asset/items/aghanim's_shard/full.webp"

private val badgeText = Color(0xFF0E1A24)

/**
 * Both Aghanim's upgrades on one card, the way the game's tooltip of the two items lists them: a new ability or a
 * change of an existing one, with the numbers the item changes. [hotkeys] name the changed ability's key.
 */
@Composable
internal fun AghanimPanel(aghanim: VOAghanim, hotkeys: Map<String, String>) {
    Column(
        Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(AppTheme.color.surface),
    ) {
        UpgradeSection(SCEPTER_ITEM, stringResource(R.string.aghanim_scepter), aghanim.scepter, hotkeys)
        InfoDivider(Modifier.padding(horizontal = 14.dp))
        UpgradeSection(SHARD_ITEM, stringResource(R.string.aghanim_shard), aghanim.shard, hotkeys)
    }
}

@Composable
private fun UpgradeSection(itemImage: String, title: String, upgrades: List<VOUpgrade>, hotkeys: Map<String, String>) {
    Column(
        modifier = Modifier.padding(14.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = itemImage,
                contentDescription = null,
                modifier = Modifier
                    .size(width = 32.dp, height = 24.dp)
                    .clip(RoundedCornerShape(4.dp)),
            )
            HSpace(8.dp)
            Caption(title, color = InfoColors.aghanim)
        }
        if (upgrades.isEmpty()) {
            Text(stringResource(R.string.aghanim_none), fontSize = 13.sp, color = AppTheme.color.textSecondaryColor)
        }
        upgrades.forEach { upgrade -> Upgrade(upgrade, hotkeys[upgrade.ability.name]) }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun Upgrade(upgrade: VOUpgrade, hotkey: String?) {
    val ability = upgrade.ability
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AbilityImage(ability, 48)
            HSpace(12.dp)
            Column {
                // a long name pushes the badge to the next line instead of squeezing it
                FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Text(ability.name, fontSize = 16.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                    when (upgrade) {
                        is VOUpgrade.NewAbility -> Badge(stringResource(R.string.aghanim_new_ability), isFilled = true, Modifier.align(Alignment.CenterVertically))
                        is VOUpgrade.Change -> Badge(
                            text = listOfNotNull(stringResource(R.string.aghanim_change), hotkey).joinToString(" · "),
                            isFilled = false,
                            modifier = Modifier.align(Alignment.CenterVertically),
                        )
                    }
                }
                VSpace(4.dp)
                AbilityTypeLine(ability, showKind = false)
            }
        }
        when (upgrade) {
            is VOUpgrade.NewAbility -> {
                AbilityBody(ability)
                AbilityCosts(ability, Modifier.clip(RoundedCornerShape(8.dp)).background(InfoColors.chip.copy(alpha = 0.5f)))
            }
            is VOUpgrade.Change -> {
                RichText(upgrade.text, style = AppTheme.style.H5.copy(fontSize = 13.5.sp), color = AppTheme.color.textColor)
                upgrade.values.forEach { value ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RichText(value.label, style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor, modifier = Modifier.weight(1f))
                        HSpace(12.dp)
                        Text(
                            text = buildAnnotatedString {
                                value.value?.let { append("$it ") }
                                withStyle(SpanStyle(color = InfoColors.aghanim)) { append(value.change) }
                            },
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = AppTheme.color.textColor,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun Badge(text: String, isFilled: Boolean, modifier: Modifier) {
    val shape = RoundedCornerShape(4.dp)
    Text(
        text = text.uppercase(),
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 0.5.sp,
        color = if (isFilled) badgeText else InfoColors.aghanim,
        maxLines = 1,
        modifier = modifier
            .clip(shape)
            .then(if (isFilled) Modifier.background(InfoColors.aghanim) else Modifier.border(1.dp, InfoColors.aghanim.copy(alpha = 0.6f), shape))
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}
