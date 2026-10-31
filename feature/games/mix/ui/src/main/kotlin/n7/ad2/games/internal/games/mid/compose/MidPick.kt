package n7.ad2.games.internal.games.mid.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.GoldColor
import n7.ad2.games.internal.compose.LineColor
import n7.ad2.games.internal.games.mid.BotLevel
import n7.ad2.games.internal.games.mid.MidHero
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.room.GameMode

private val PaperEdge = Color(0xFFFFF7E8)

/** Before the lane: a hero, and in solo a bot of some level or training; over the network both say they are ready */
@Composable
internal fun ColumnScope.MidPick(state: MidViewModel.State, actions: MidActions) {
    Column(
        Modifier.weight(1f).verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Text(stringResource(R.string.mid), color = AppTheme.color.textColor, fontSize = 24.sp, fontWeight = FontWeight.ExtraBold)
        Text(stringResource(R.string.mid_pick_hint), color = AppTheme.color.textSecondaryColor, fontSize = 13.sp, lineHeight = 18.sp)
        Column(Modifier.selectableGroup(), verticalArrangement = Arrangement.spacedBy(14.dp)) {
            state.heroes.chunked(2).forEach { row ->
                Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                    row.forEach { hero ->
                        HeroCard(hero, isSelected = hero == state.myHero, isEnabled = !state.myReady, onClick = { actions.onPickHero(hero) }, Modifier.weight(1f))
                    }
                    if (row.size == 1) Spacer(Modifier.weight(1f))
                }
            }
        }
        if (state.mode == GameMode.SOLO) SoloOptions(state, actions) else PeerStatus(state)
    }
    Spacer(Modifier.height(12.dp))
    GoButton(state, actions)
}

@Composable
private fun HeroCard(hero: MidHero, isSelected: Boolean, isEnabled: Boolean, onClick: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, if (isSelected) GoldColor else PaperEdge, shape)
            .selectable(selected = isSelected, enabled = isEnabled, role = Role.RadioButton, onClick = onClick)
            .padding(8.dp),
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        AsyncImage(
            model = hero.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxWidth().height(76.dp).clip(RoundedCornerShape(10.dp)),
        )
        Row(verticalAlignment = Alignment.Bottom) {
            Text(hero.name, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f), maxLines = 1)
            Text(stringResource(if (hero.projectileSpeed > 0) R.string.mid_ranged else R.string.mid_melee), color = InkDim, fontSize = 11.sp)
        }
        Stat(stringResource(R.string.mid_damage), hero.damage.toString())
        Stat(stringResource(R.string.mid_swing), stringResource(R.string.mid_seconds, seconds(hero.attackPointMs.toLong())))
        Stat(stringResource(R.string.mid_projectile), if (hero.projectileSpeed > 0) hero.projectileSpeed.toString() else stringResource(R.string.mid_no_projectile))
    }
}

@Composable
private fun Stat(label: String, value: String) {
    Row {
        Text(label, color = InkDim, fontSize = 11.sp, modifier = Modifier.weight(1f))
        Text(value, color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun SoloOptions(state: MidViewModel.State, actions: MidActions) {
    Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        Choice(stringResource(R.string.mid_vs_bot), isSelected = !state.training, Modifier.weight(1f)) { actions.onPickLevel(state.level) }
        Choice(stringResource(R.string.mid_training), isSelected = state.training, Modifier.weight(1f), onClick = actions.onPickTraining)
    }
    if (!state.training) {
        Row(Modifier.selectableGroup(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            BotLevel.entries.forEach { level ->
                Choice(stringResource(level.label()), isSelected = level == state.level, Modifier.weight(1f), small = true) { actions.onPickLevel(level) }
            }
        }
    }
    Text(stringResource(if (state.training) R.string.mid_training_hint else R.string.mid_rules_line), color = AppTheme.color.textSecondaryColor, fontSize = 12.sp)
}

@Composable
private fun PeerStatus(state: MidViewModel.State) {
    val peer = state.peerName.orEmpty()
    val peerHero = state.peerHero
    val text = if (state.peerReady && peerHero != null) stringResource(R.string.mid_peer_ready, peer, peerHero.name) else stringResource(R.string.mid_peer_picking, peer)
    Text(text, color = AppTheme.color.textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
    Text(
        stringResource(if (state.mode == GameMode.COOP) R.string.mid_coop_hint else R.string.mid_versus_hint),
        color = AppTheme.color.textSecondaryColor,
        fontSize = 12.sp,
    )
}

@Composable
private fun Choice(text: String, isSelected: Boolean, modifier: Modifier, small: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Text(
        text = text,
        color = if (isSelected) Ink else AppTheme.color.textColor,
        fontSize = if (small) 12.sp else 14.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        modifier = modifier
            .heightIn(min = if (small) 40.dp else 48.dp)
            .clip(shape)
            .background(if (isSelected) PaperCard else Color.Transparent)
            .border(if (isSelected) 0.dp else 1.dp, LineColor, shape)
            .selectable(selected = isSelected, role = Role.RadioButton, onClick = onClick)
            .padding(vertical = if (small) 11.dp else 14.dp),
    )
}

@Composable
private fun GoButton(state: MidViewModel.State, actions: MidActions) {
    val waiting = state.mode != GameMode.SOLO && state.myReady
    val text = when {
        state.mode == GameMode.SOLO -> stringResource(R.string.mid_go)
        waiting -> stringResource(R.string.mid_waiting, state.peerName.orEmpty())
        else -> stringResource(R.string.mid_ready)
    }
    val enabled = state.heroes.isNotEmpty() && !waiting
    Text(
        text = text,
        color = if (enabled) Ink else AppTheme.color.textSecondaryColor,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .background(if (enabled) GoldColor else LineColor)
            .clickable(enabled = enabled, role = Role.Button, onClick = actions.onReady)
            .padding(vertical = 16.dp),
    )
}

private fun BotLevel.label(): Int = when (this) {
    BotLevel.EASY -> R.string.mid_level_easy
    BotLevel.MEDIUM -> R.string.mid_level_medium
    BotLevel.PRO -> R.string.mid_level_pro
}
