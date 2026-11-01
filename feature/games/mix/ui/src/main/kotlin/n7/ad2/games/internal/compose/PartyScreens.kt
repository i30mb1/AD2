package n7.ad2.games.internal.compose

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.paper.PaperEdge
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.PartyFailure
import n7.ad2.games.internal.party.PartyState
import n7.ad2.games.internal.party.Via
import n7.ad2.games.room.MAX_PLAYERS

internal enum class PartyBanner { ALONE, NEARBY, CONNECTING, FAILED, TOGETHER }

/** What the top of the games list shows: searching, someone to play with, the connection in progress or failed, or the pair picking together. */
internal fun partyBanner(party: PartyState): PartyBanner = when {
    party.session != null -> PartyBanner.TOGETHER
    party.connecting -> PartyBanner.CONNECTING
    party.failure != null -> PartyBanner.FAILED
    party.nearby.isNotEmpty() -> PartyBanner.NEARBY
    else -> PartyBanner.ALONE
}

/** Top of the games list while not paired: searching, players to play with, or the connection state. */
@Composable
internal fun NearbyCard(party: PartyState, onPlay: (NearbyPlayer) -> Unit, onHelp: () -> Unit, modifier: Modifier = Modifier) {
    when (partyBanner(party)) {
        PartyBanner.ALONE -> Row(
            modifier = modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Pulse()
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.party_alone), color = AppTheme.color.textColor, fontSize = 14.sp, fontWeight = FontWeight.Medium)
                Text(stringResource(R.string.party_alone_hint), color = DimColor, fontSize = 12.sp)
            }
            Text(stringResource(R.string.party_help), color = PeerColor, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onHelp).padding(4.dp))
        }
        PartyBanner.TOGETHER -> Unit
        else -> NearbyPaper(party, onPlay, onHelp, modifier)
    }
}

private val NearbyGreen = Color(0xFF3F7D45)
private val NearbyFailed = Color(0xFF8C3A33)
private val NearbyText = Color(0xFFDDEBD8)
private val NearbyButtonText = Color(0xFF2B5A31)

/** Players nearby on green paper: one - a button to play, several - a row for each; the connection on top */
@Composable
private fun NearbyPaper(party: PartyState, onPlay: (NearbyPlayer) -> Unit, onHelp: () -> Unit, modifier: Modifier) {
    val shape = RoundedCornerShape(14.dp)
    val failure = party.failure
    Column(
        modifier = modifier
            .fillMaxWidth()
            .rotate(-0.6f)
            .shadow(6.dp, shape)
            .clip(shape)
            .background(if (failure != null) NearbyFailed else NearbyGreen)
            .border(3.dp, PaperEdge, shape)
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        val first = party.nearby.firstOrNull()
        when {
            // ручное подключение по адресу идёт без имени
            party.connecting -> Text(
                text = first?.name?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.party_connecting, it) } ?: stringResource(R.string.party_connecting_plain),
                color = PaperEdge,
                fontSize = 14.sp,
            )
            failure != null -> Row(verticalAlignment = Alignment.CenterVertically) {
                Text(stringResource(failure.text()), color = PaperEdge, fontSize = 13.sp, modifier = Modifier.weight(1f))
                Text(stringResource(R.string.party_help), color = PaperEdge, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clickable(onClick = onHelp).padding(4.dp))
            }
        }
        if (first == null) return@Column
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            Row {
                party.nearby.take(3).forEachIndexed { index, player ->
                    Avatar(player.name, PeerColor, 34.dp, Modifier.offset(x = (-10).dp * index).border(2.dp, PaperEdge, CircleShape))
                }
            }
            Column(Modifier.weight(1f)) {
                Text(
                    stringResource(R.string.party_nearby_many, party.nearby.joinToString { it.name }),
                    color = PaperEdge,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                )
                Text(stringResource(R.string.party_room_subtitle, MAX_PLAYERS), color = NearbyText, fontSize = 12.sp)
            }
            if (party.nearby.size == 1) PlayButton(enabled = !party.connecting) { onPlay(first) }
        }
        if (party.nearby.size > 1) {
            party.nearby.forEach { player ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("${player.name} · ${player.via.label()}", color = NearbyText, fontSize = 13.sp, maxLines = 1, overflow = TextOverflow.Ellipsis, modifier = Modifier.weight(1f))
                    PlayButton(enabled = !party.connecting) { onPlay(player) }
                }
            }
        }
    }
}

@Composable
private fun PlayButton(enabled: Boolean, onClick: () -> Unit) {
    Text(
        text = stringResource(R.string.party_play),
        color = NearbyButtonText,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .alpha(if (enabled) 1f else 0.5f)
            .clip(RoundedCornerShape(10.dp))
            .background(PaperEdge)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 10.dp),
    )
}

@Composable
private fun Pulse() {
    val scale by rememberInfiniteTransition().animateFloat(0.5f, 1.4f, infiniteRepeatable(tween(1_400), RepeatMode.Restart))
    Box(Modifier.size(32.dp), contentAlignment = Alignment.Center) {
        Box(Modifier.size(32.dp).scale(scale).alpha((1.4f - scale).coerceIn(0f, 1f)).border(2.dp, PeerColor, CircleShape))
        Box(Modifier.size(8.dp).clip(CircleShape).background(PeerColor))
    }
}

@Composable
private fun Via.label(): String = stringResource(if (this == Via.DIRECT) R.string.party_via_direct else R.string.party_via_wifi)

private fun PartyFailure.text(): Int = when (this) {
    PartyFailure.CONNECTION -> R.string.party_failed
    PartyFailure.FULL -> R.string.party_failed_full
    PartyFailure.BUSY -> R.string.party_failed_busy
    PartyFailure.VERSION -> R.string.party_failed_version
}

/** Nobody found: a checklist, my address to tell a friend and the friend's address to connect to. */
@Composable
internal fun PartyHelp(party: PartyState, onJoinManual: (String) -> Unit, modifier: Modifier = Modifier) {
    var address by rememberSaveable { mutableStateOf("") }
    Column(modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(stringResource(R.string.party_help_title), color = AppTheme.color.textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Text(stringResource(R.string.party_help_check), color = AppTheme.color.textSecondaryColor, fontSize = 13.sp)
        Check(1, stringResource(R.string.party_help_wifi), stringResource(R.string.party_help_wifi_hint))
        Check(2, stringResource(R.string.party_help_network), stringResource(R.string.party_help_network_hint))
        Check(3, stringResource(R.string.party_help_open), stringResource(R.string.party_help_open_hint))
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            val myAddress = party.myAddress
            if (myAddress != null) {
                Text(stringResource(R.string.party_my_address), color = DimColor, fontSize = 12.sp)
                Text(myAddress, color = AppTheme.color.textColor, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            } else {
                Text(stringResource(R.string.party_no_address), color = DimColor, fontSize = 12.sp)
            }
            Box(Modifier.fillMaxWidth().height(1.dp).background(LineColor))
            Text(stringResource(R.string.party_enter_address), color = DimColor, fontSize = 12.sp)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    singleLine = true,
                    placeholder = { Text("192.168.…:…") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Uri),
                    colors = TextFieldDefaults.outlinedTextFieldColors(textColor = AppTheme.color.textColor, unfocusedBorderColor = LineColor),
                    modifier = Modifier.weight(1f),
                )
                val ready = address.contains(":")
                Text(
                    text = stringResource(R.string.party_connect),
                    color = AppTheme.color.textColor,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.alpha(if (ready) 1f else 0.4f).clip(RoundedCornerShape(10.dp)).background(LineColor)
                        .clickable(enabled = ready) { onJoinManual(address.trim()) }.padding(horizontal = 14.dp, vertical = 12.dp),
                )
            }
        }
    }
}

@Composable
private fun Check(number: Int, title: String, hint: String) {
    Row(
        Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(AppTheme.color.surface).padding(12.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Box(Modifier.size(22.dp).clip(CircleShape).background(LineColor), contentAlignment = Alignment.Center) {
            Text("$number", color = AppTheme.color.textSecondaryColor, fontSize = 12.sp, fontWeight = FontWeight.Bold)
        }
        Column {
            Text(title, color = AppTheme.color.textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            Text(hint, color = DimColor, fontSize = 11.sp)
        }
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390)
@Composable
private fun NearbyCardPreview() {
    val players = listOf(
        NearbyPlayer("p", "Pixel 8", Via.DIRECT, ip = null, port = 0, deviceAddress = "aa"),
        NearbyPlayer("g", "Galaxy S24", Via.WIFI, ip = "192.168.1.5", port = 4000, deviceAddress = null),
    )
    AppTheme(isDarkTheme = true) {
        Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            NearbyCard(PartyState(), onPlay = {}, onHelp = {})
            NearbyCard(PartyState(nearby = players), onPlay = {}, onHelp = {})
        }
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun PartyHelpPreview() {
    AppTheme(isDarkTheme = true) { PartyHelp(PartyState(myAddress = "192.168.31.189:43905"), onJoinManual = {}) }
}
