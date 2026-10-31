package n7.ad2.hero.page.internal.info.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOAbilityTalent
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.PatchChange
import n7.ad2.heroes.domain.model.PatchNote

private val footerBackground = Color(0xFF26282C)
private val talentColor = InfoColors.pure

/**
 * The picked ability as the game's tooltip draws it: what it is, what it does, its numbers and the talents that change
 * it, with the costs in the footer. Notes, wiki details and patch history are long, they open under the card.
 */
@Composable
internal fun AbilityPanel(ability: Ability, talents: List<VOAbilityTalent>, changes: List<Pair<PatchChange, List<PatchNote>>>) {
    Column {
        Column(
            Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .background(AppTheme.color.surface),
        ) {
            Row(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, top = 14.dp, bottom = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                AbilityImage(ability, 52)
                HSpace(12.dp)
                Column {
                    Text(ability.name, fontSize = 17.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                    VSpace(4.dp)
                    AbilityTypeLine(ability, showKind = true)
                }
            }
            Column(
                modifier = Modifier.padding(start = 14.dp, end = 14.dp, bottom = 14.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                AbilityBody(ability)
                talents.forEach { talent -> TalentLine(talent) }
            }
            AbilityCosts(ability, Modifier.background(footerBackground))
        }
        AbilityExtras(ability, changes)
    }
}

@Composable
internal fun AbilityImage(ability: Ability, size: Int) {
    AsyncImage(
        model = ability.imageUrl,
        contentDescription = null,
        placeholder = painterResource(n7.ad2.core.ui.R.drawable.square_placeholder),
        error = painterResource(n7.ad2.core.ui.R.drawable.square_error_placeholder),
        modifier = Modifier
            .size(size.dp)
            .clip(RoundedCornerShape((size / 5).dp)),
    )
}

/** "Ultimate · Unit target · physical damage": the kind only where it matters, the damage in its game color. */
@Composable
internal fun AbilityTypeLine(ability: Ability, showKind: Boolean) {
    val parts = buildList {
        if (showKind) ability.kind.label?.let { add(stringResource(it)) }
        ability.behavior.forEach { add(stringResource(it.label)) }
    }
    val damageType = ability.damageType
    val damage = damageType?.let { stringResource(R.string.ability_damage_of_type, stringResource(it.label).lowercase()) }
    if (parts.isEmpty() && damage == null) return
    Text(
        text = buildAnnotatedString {
            append(parts.joinToString(" · "))
            if (damageType != null && damage != null) {
                if (parts.isNotEmpty()) append(" · ")
                withStyle(SpanStyle(color = damageType.color)) { append(damage) }
            }
        },
        fontSize = 12.sp,
        color = AppTheme.color.textSecondaryColor,
    )
}

/** Description, properties and numbers of an ability, the same in its card and among Aghanim's upgrades. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun AbilityBody(ability: Ability) {
    if (ability.description.isNotEmpty()) {
        RichText(ability.description, style = AppTheme.style.H5.copy(fontSize = 13.5.sp), color = AppTheme.color.textColor)
    }
    val properties = buildList {
        ability.targetTeam?.let { add(stringResource(R.string.ability_affects) to stringResource(it.label)) }
        ability.spellImmunity?.let { add(stringResource(R.string.ability_spell_immunity) to stringResource(it.label)) }
        ability.dispellable?.let { add(stringResource(R.string.ability_dispellable) to stringResource(it.label)) }
    }
    if (properties.isNotEmpty()) {
        FlowRow(horizontalArrangement = Arrangement.spacedBy(6.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            properties.forEach { (label, value) ->
                Text(
                    text = buildAnnotatedString {
                        append("$label: ")
                        withStyle(SpanStyle(color = AppTheme.color.textColor, fontWeight = FontWeight.Bold)) { append(value) }
                    },
                    fontSize = 11.5.sp,
                    color = AppTheme.color.textSecondaryColor,
                    modifier = Modifier
                        .clip(RoundedCornerShape(6.dp))
                        .background(InfoColors.chip)
                        .padding(horizontal = 8.dp, vertical = 5.dp),
                )
            }
        }
    }
    // labels of the datafeed come in capitals, the app's own ones are written the same way
    val values = buildList {
        ability.params.forEach { param -> param.value?.let { add(param.label to it) } }
        ability.castRange?.let { add(stringResource(R.string.ability_cast_range).uppercase() to it) }
        ability.castPoint?.let { add(stringResource(R.string.ability_cast_point).uppercase() to it) }
        ability.channelTime?.let { add(stringResource(R.string.ability_channel_time).uppercase() to it) }
        ability.duration?.let { add(stringResource(R.string.ability_duration).uppercase() to it) }
    }
    if (values.isNotEmpty()) {
        InfoDivider()
        FlowRow(horizontalArrangement = Arrangement.spacedBy(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            values.forEach { (label, value) ->
                Column {
                    RichText(label, style = AppTheme.style.info.copy(fontSize = 10.sp, letterSpacing = 0.6.sp), color = AppTheme.color.textSecondaryColor)
                    VSpace(3.dp)
                    Text(value, fontSize = 14.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
                }
            }
        }
    }
}

@Composable
private fun TalentLine(talent: VOAbilityTalent) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            modifier = Modifier
                .size(22.dp)
                .border(1.5.dp, talentColor, CircleShape),
            contentAlignment = Alignment.Center,
        ) {
            Text(talent.level.toString(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = talentColor)
        }
        HSpace(8.dp)
        Text(talent.text, fontSize = 12.sp, color = talentColor)
    }
}

/** Cooldown, mana and the other costs, each with the game's color, in one line. */
@Composable
internal fun AbilityCosts(ability: Ability, modifier: Modifier = Modifier) {
    val costs = buildList {
        ability.mana?.let { add(Cost(R.string.ability_mana, it, InfoColors.mana)) }
        ability.healthCost?.let { add(Cost(R.string.ability_health_cost, it, InfoColors.health)) }
        ability.goldCost?.let { add(Cost(R.string.ability_gold_cost, it, InfoColors.pure)) }
    }
    if (ability.cooldown == null && costs.isEmpty()) return
    val cooldownLabel = stringResource(R.string.ability_cooldown)
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 11.dp),
        horizontalArrangement = Arrangement.spacedBy(20.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        ability.cooldown?.let { cooldown ->
            Row(
                modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "$cooldownLabel $cooldown" },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // the vector is a full-color game icon, a tint would flatten it
                Image(painterResource(n7.ad2.core.ui.R.drawable.cooldown), contentDescription = null, modifier = Modifier.size(13.dp))
                HSpace(7.dp)
                Text(cooldown, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppTheme.color.textColor)
            }
        }
        costs.forEach { cost ->
            val label = stringResource(cost.label)
            Row(
                modifier = Modifier.semantics(mergeDescendants = true) { contentDescription = "$label ${cost.value}" },
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Box(
                    Modifier
                        .size(11.dp)
                        .clip(RoundedCornerShape(2.dp))
                        .background(cost.color),
                )
                HSpace(7.dp)
                Text(cost.value, fontSize = 13.sp, fontWeight = FontWeight.Medium, color = AppTheme.color.textColor)
            }
        }
    }
}

private class Cost(val label: Int, val value: String, val color: Color)

/** New content fades in while the old one fades out, and the height follows smoothly: nothing jumps on the screen. */
internal fun <S> AnimatedContentTransitionScope<S>.revealTransition(): ContentTransform =
    fadeIn(tween(durationMillis = 220, delayMillis = 60)) togetherWith fadeOut(tween(durationMillis = 120)) using
        SizeTransform(clip = true) { _, _ -> tween(durationMillis = 280, easing = FastOutSlowInEasing) }

private enum class Extra { NOTES, WIKI, CHANGES }

/** Long reference parts of an ability: each button opens its part under the buttons, a second tap closes it. */
@Composable
private fun AbilityExtras(ability: Ability, changes: List<Pair<PatchChange, List<PatchNote>>>) {
    val available = buildList {
        if (ability.notes.isNotEmpty()) add(Triple(Extra.NOTES, R.string.hero_fragment_notes, ability.notes.size))
        if (ability.wikiNotes.isNotEmpty()) add(Triple(Extra.WIKI, R.string.info_wiki_short, ability.wikiNotes.size))
        if (changes.isNotEmpty()) add(Triple(Extra.CHANGES, R.string.info_patches_short, changes.size))
    }
    if (available.isEmpty()) return
    var opened by rememberSaveable(ability.name) { mutableStateOf<Extra?>(null) }
    Row(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 10.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        available.forEach { (extra, title, count) ->
            val isOpen = opened == extra
            val shape = RoundedCornerShape(10.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(44.dp)
                    .clip(shape)
                    .background(if (isOpen) InfoColors.chip else Color.Transparent)
                    .border(1.dp, if (isOpen) AppTheme.color.primary else InfoColors.chip, shape)
                    .clickable(role = Role.Button) { opened = if (isOpen) null else extra },
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = buildAnnotatedString {
                        append(stringResource(title))
                        append("  ")
                        withStyle(SpanStyle(color = AppTheme.color.textSecondaryColor)) { append(count.toString()) }
                    },
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    color = AppTheme.color.textColor,
                )
            }
        }
    }
    AnimatedContent(
        targetState = opened,
        transitionSpec = { revealTransition() },
        label = "ability extras",
    ) { extra ->
        Column(
            Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = if (extra == null) 0.dp else 8.dp),
        ) {
            when (extra) {
                Extra.NOTES -> ability.notes.forEach { BulletText(it) }
                Extra.WIKI -> AbilityWikiNotes(ability.wikiNotes)
                Extra.CHANGES -> AbilityChanges(changes)
                null -> Unit
            }
        }
    }
}
