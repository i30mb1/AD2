package n7.ad2.hero.page.internal.info.components

import androidx.annotation.DrawableRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityGroup
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.AbilityKind
import n7.ad2.heroes.domain.model.AbilityParam
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.PatchNote

private const val SCEPTER_ICON = "file:///android_asset/images/aghanim's_scepter_symbol.webp"
private const val SHARD_ICON = "file:///android_asset/images/aghanim's_shard_symbol.webp"

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AbilitiesSection(
    groups: List<VOAbilityGroup>,
    selected: Ability,
    onSelect: (Ability) -> Unit,
    changes: List<PatchChange>,
) {
    SectionBody {
        // one row in game order, groups are split by a thin line: the kind of the picked ability is named in its details
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            groups.forEachIndexed { index, group ->
                if (index > 0) {
                    Box(
                        Modifier
                            .align(Alignment.CenterVertically)
                            .padding(horizontal = 2.dp)
                            .size(width = 1.dp, height = 32.dp)
                            .background(InfoColors.divider),
                    )
                }
                group.abilities.forEach { ability ->
                    AbilityIcon(ability, isSelected = ability == selected, onClick = { onSelect(ability) })
                }
            }
        }
        VSpace(16.dp)
        InfoDivider()
        VSpace(16.dp)
        AnimatedContent(
            targetState = selected,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            label = "ability",
        ) { ability ->
            val abilityChanges = changes.mapNotNull { change ->
                change.abilities.firstOrNull { it.abilityName == ability.name }?.let { change to it.notes }
            }
            AbilityDetails(ability, abilityChanges)
        }
    }
}

@Composable
private fun AbilityIcon(ability: Ability, isSelected: Boolean, onClick: () -> Unit) {
    val shape = AppTheme.shape.medium
    AsyncImage(
        model = ability.imageUrl,
        contentDescription = ability.name,
        placeholder = painterResource(n7.ad2.core.ui.R.drawable.square_placeholder),
        error = painterResource(n7.ad2.core.ui.R.drawable.square_error_placeholder),
        modifier = Modifier
            .size(48.dp)
            .alpha(if (isSelected) 1f else 0.55f)
            .clip(shape)
            .border(2.dp, if (isSelected) AppTheme.color.primary else Color.Transparent, shape)
            .clickable(onClick = onClick),
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun AbilityDetails(ability: Ability, changes: List<Pair<PatchChange, List<PatchNote>>>) {
    Column(Modifier.fillMaxWidth()) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            AsyncImage(
                model = ability.imageUrl,
                contentDescription = null,
                placeholder = painterResource(n7.ad2.core.ui.R.drawable.square_placeholder),
                error = painterResource(n7.ad2.core.ui.R.drawable.square_error_placeholder),
                modifier = Modifier
                    .size(56.dp)
                    .clip(AppTheme.shape.medium),
            )
            HSpace(12.dp)
            Column {
                Text(ability.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                ability.kind.label?.let { kind ->
                    VSpace(4.dp)
                    Text(
                        text = stringResource(kind),
                        style = AppTheme.style.body,
                        color = if (ability.kind == AbilityKind.SCEPTER || ability.kind == AbilityKind.SHARD) InfoColors.aghanim else AppTheme.color.primary,
                        fontWeight = FontWeight.Bold,
                    )
                }
                if (ability.behavior.isNotEmpty()) {
                    VSpace(2.dp)
                    Text(
                        text = ability.behavior.map { stringResource(it.label) }.joinToString(" · "),
                        style = AppTheme.style.body,
                        color = AppTheme.color.textSecondaryColor,
                    )
                }
            }
        }

        val properties = buildList {
            ability.targetTeam?.let { add(Property(R.string.ability_affects, it.label)) }
            ability.damageType?.let { add(Property(R.string.ability_damage_type, it.label, it.color)) }
            ability.spellImmunity?.let { add(Property(R.string.ability_spell_immunity, it.label)) }
            ability.dispellable?.let { add(Property(R.string.ability_dispellable, it.label)) }
        }
        if (properties.isNotEmpty()) {
            VSpace(12.dp)
            properties.forEach { property ->
                Row(Modifier.padding(vertical = 2.dp)) {
                    Text(stringResource(property.label), style = AppTheme.style.H5, color = AppTheme.color.textSecondaryColor, modifier = Modifier.weight(1f))
                    Text(stringResource(property.value), style = AppTheme.style.H5, color = property.color ?: AppTheme.color.textColor, fontWeight = FontWeight.Medium)
                }
            }
        }

        if (ability.description.isNotEmpty()) {
            VSpace(12.dp)
            RichText(ability.description)
        }

        val costs = buildList {
            ability.cooldown?.let { add(Cost(n7.ad2.core.ui.R.drawable.cooldown, R.string.ability_cooldown, it, InfoColors.cooldown)) }
            // core.ui mana drawable is a <shape>, painterResource supports only vectors and bitmaps
            ability.mana?.let { add(Cost(null, R.string.ability_mana, it, InfoColors.mana)) }
            ability.healthCost?.let { add(Cost(null, R.string.ability_health_cost, it, InfoColors.health)) }
            ability.goldCost?.let { add(Cost(null, R.string.ability_gold_cost, it, InfoColors.pure)) }
            ability.castRange?.let { add(Cost(null, R.string.ability_cast_range, it)) }
            ability.castPoint?.let { add(Cost(null, R.string.ability_cast_point, it)) }
            ability.channelTime?.let { add(Cost(null, R.string.ability_channel_time, it)) }
            ability.duration?.let { add(Cost(null, R.string.ability_duration, it)) }
        }
        if (costs.isNotEmpty()) {
            VSpace(12.dp)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                costs.forEach { cost -> CostPill(cost) }
            }
        }

        if (ability.params.isNotEmpty()) {
            VSpace(16.dp)
            Column(
                Modifier
                    .fillMaxWidth()
                    .clip(AppTheme.shape.medium)
                    .background(AppTheme.color.background.copy(alpha = 0.6f))
                    .padding(12.dp),
            ) {
                ability.params.forEachIndexed { index, param ->
                    if (index > 0) InfoDivider(Modifier.padding(vertical = 8.dp))
                    ParamRow(param)
                }
            }
        }

        ability.shard?.let { text ->
            VSpace(12.dp)
            UpgradeBlock(SHARD_ICON, stringResource(R.string.aghanim_shard), text)
        }
        ability.scepter?.let { text ->
            VSpace(12.dp)
            UpgradeBlock(SCEPTER_ICON, stringResource(R.string.aghanim_scepter), text)
        }

        // secondary parts are folded, the user opens only what is interesting
        if (ability.notes.isNotEmpty() || ability.wikiNotes.isNotEmpty() || changes.isNotEmpty()) {
            VSpace(12.dp)
            InfoDivider()
        }
        if (ability.notes.isNotEmpty()) {
            SubSection(stringResource(R.string.hero_fragment_notes), ability.notes.size) {
                ability.notes.forEach { note -> BulletText(note) }
            }
        }
        if (ability.wikiNotes.isNotEmpty()) {
            SubSection(
                title = stringResource(R.string.info_ability_details),
                count = ability.wikiNotes.size,
                trailing = { WikiSourceLabel() },
            ) { AbilityWikiNotes(ability.wikiNotes) }
        }
        if (changes.isNotEmpty()) {
            SubSection(stringResource(R.string.info_ability_changes), changes.size) {
                AbilityChanges(changes)
            }
        }
    }
}

private class Property(val label: Int, val value: Int, val color: Color? = null)

private class Cost(@DrawableRes val icon: Int?, val label: Int, val value: String, val color: Color? = null)

@Composable
private fun CostPill(cost: Cost) {
    Column(
        modifier = Modifier
            .clip(AppTheme.shape.medium)
            .background(InfoColors.chip)
            .padding(horizontal = 10.dp, vertical = 6.dp),
    ) {
        Caption(stringResource(cost.label))
        VSpace(2.dp)
        Row(verticalAlignment = Alignment.CenterVertically) {
            if (cost.icon != null) {
                Image(
                    painter = painterResource(cost.icon),
                    contentDescription = null,
                    // the vectors are full-color game icons, a tint would flatten them into a square
                    modifier = Modifier.size(12.dp),
                )
                HSpace(4.dp)
            } else if (cost.color != null) {
                Box(
                    Modifier
                        .size(8.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(cost.color),
                )
                HSpace(4.dp)
            }
            Text(cost.value, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
        }
    }
}

@Composable
private fun ParamRow(param: AbilityParam) {
    Column(Modifier.fillMaxWidth()) {
        RichText(
            text = param.label,
            style = AppTheme.style.info.copy(fontSize = 10.sp, letterSpacing = 0.8.sp),
            color = AppTheme.color.textSecondaryColor,
        )
        VSpace(2.dp)
        param.value?.let { Text(it, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor) }
        param.talent?.let { UpgradeValue(painterIcon = n7.ad2.core.ui.R.drawable.talent, url = null, value = it, color = InfoColors.talent) }
        param.shard?.let { UpgradeValue(painterIcon = null, url = SHARD_ICON, value = it, color = InfoColors.aghanim) }
        param.scepter?.let { UpgradeValue(painterIcon = null, url = SCEPTER_ICON, value = it, color = InfoColors.aghanim) }
    }
}

@Composable
private fun UpgradeValue(@DrawableRes painterIcon: Int?, url: String?, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 2.dp)) {
        if (painterIcon != null) {
            Image(painterResource(painterIcon), contentDescription = null, modifier = Modifier.size(14.dp))
        } else {
            AsyncImage(model = url, contentDescription = null, modifier = Modifier.size(14.dp))
        }
        HSpace(4.dp)
        Text(value, style = AppTheme.style.body, fontWeight = FontWeight.Bold, color = color)
    }
}

@Composable
private fun UpgradeBlock(iconUrl: String, title: String, text: String) {
    Row(
        Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .clip(AppTheme.shape.medium)
            .background(InfoColors.aghanim.copy(alpha = 0.08f)),
    ) {
        Box(
            Modifier
                .width(3.dp)
                .fillMaxHeight()
                .background(InfoColors.aghanim),
        )
        Column(Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                AsyncImage(model = iconUrl, contentDescription = null, modifier = Modifier.size(18.dp))
                HSpace(6.dp)
                Text(title, style = AppTheme.style.H5, fontWeight = FontWeight.Bold, color = InfoColors.aghanim)
            }
            VSpace(6.dp)
            RichText(text)
        }
    }
}

@Composable
internal fun BulletText(text: String, color: Color = AppTheme.color.textSecondaryColor) {
    Row(Modifier.padding(vertical = 3.dp)) {
        Box(
            Modifier
                .padding(top = 7.dp)
                .size(5.dp)
                .clip(RoundedCornerShape(50))
                .background(AppTheme.color.primary),
        )
        HSpace(10.dp)
        RichText(text, color = color)
    }
}
