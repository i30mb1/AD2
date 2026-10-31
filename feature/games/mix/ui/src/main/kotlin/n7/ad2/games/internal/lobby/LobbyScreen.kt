package n7.ad2.games.internal.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.room.CloseReason
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.RoomState

internal val Accent = Color(0xFFF44336)
private val Blue = Color(0xFF448AFF)

@Composable
internal fun LobbyScreen(
    state: LobbyViewModel.State,
    mode: GameMode,
    onTab: (LobbyViewModel.Tab) -> Unit,
    onJoin: (LobbyViewModel.FoundGame) -> Unit,
    onJoinManual: (String) -> Unit,
    onStart: () -> Unit,
    onRecreate: () -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp),
    ) {
        Text(
            text = stringResource(if (mode == GameMode.COOP) R.string.games_mode_coop else R.string.games_mode_versus),
            color = AppTheme.color.textColor,
            fontSize = 22.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 14.dp),
        )
        Tabs(
            items = listOf(stringResource(R.string.lobby_create), stringResource(R.string.lobby_find)),
            selected = state.tab.ordinal,
            onSelect = { onTab(LobbyViewModel.Tab.entries[it]) },
        )
        Spacer(Modifier.height(16.dp))
        if (state.deviceIp.isEmpty() && !state.wifiDirect) {
            Hint(stringResource(R.string.lobby_no_network))
            return@Column
        }
        when (state.tab) {
            LobbyViewModel.Tab.CREATE -> CreateTab(state, onStart, onRecreate, Modifier.weight(1f))
            LobbyViewModel.Tab.FIND -> FindTab(state, onJoin, onJoinManual, Modifier.weight(1f))
        }
    }
}

@Composable
private fun CreateTab(state: LobbyViewModel.State, onStart: () -> Unit, onRecreate: () -> Unit, modifier: Modifier) {
    val roomState = state.roomState
    val guestName = (roomState as? RoomState.Ready)?.guest?.name
    Column(modifier) {
        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
            PlayerSlot("🙂", stringResource(R.string.lobby_you, state.me.name), active = true)
            Text("VS", color = AppTheme.color.textSecondaryColor, style = AppTheme.style.body)
            PlayerSlot(if (guestName != null) "😎" else "?", guestName ?: stringResource(R.string.lobby_waiting), active = guestName != null)
        }
        Spacer(Modifier.height(16.dp))
        when (roomState) {
            is RoomState.Waiting -> {
                if (state.deviceIp.isNotEmpty()) Hint(stringResource(R.string.lobby_visible_as, state.me.name, "${state.deviceIp}:${roomState.port}"))
                if (state.wifiDirect) Hint(stringResource(R.string.lobby_visible_direct, state.me.name))
            }
            is RoomState.Closed -> {
                Hint(stringResource(roomState.reason.message()))
                PrimaryButton(stringResource(R.string.lobby_create), enabled = true, color = AppTheme.color.surface, onClick = onRecreate)
            }
            else -> Unit
        }
        Spacer(Modifier.weight(1f))
        val ready = roomState is RoomState.Ready
        PrimaryButton(
            text = stringResource(if (ready) R.string.lobby_start else R.string.lobby_start_waiting),
            enabled = ready,
            color = Accent,
            onClick = onStart,
        )
    }
}

@Composable
private fun FindTab(state: LobbyViewModel.State, onJoin: (LobbyViewModel.FoundGame) -> Unit, onJoinManual: (String) -> Unit, modifier: Modifier) {
    var address by remember { mutableStateOf("") }
    Column(modifier) {
        when (state.direct) {
            LobbyViewModel.DirectStatus.CONNECTING -> Hint(stringResource(R.string.lobby_direct_connecting))
            LobbyViewModel.DirectStatus.FAILED -> Hint(stringResource(R.string.lobby_closed_failed))
            LobbyViewModel.DirectStatus.IDLE -> Unit
        }
        when (val roomState = state.roomState) {
            RoomState.Connecting -> Hint(stringResource(R.string.lobby_connecting))
            is RoomState.Ready -> Hint(stringResource(R.string.lobby_wait_host, roomState.host.name))
            is RoomState.Closed -> Hint(stringResource(roomState.reason.message()))
            else -> Unit
        }
        Text(
            text = stringResource(if (state.servers.isEmpty()) R.string.lobby_searching else R.string.lobby_found),
            color = AppTheme.color.textSecondaryColor,
            style = AppTheme.style.info,
            modifier = Modifier.padding(vertical = 8.dp),
        )
        LazyColumn(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            items(state.servers, key = { it.deviceAddress ?: "${it.ip}:${it.port}" }) { game ->
                val direct = game.deviceAddress != null
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(10.dp))
                        .background(AppTheme.color.surface)
                        .clickable { onJoin(game) }
                        .padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Box(Modifier.size(8.dp).clip(CircleShape).background(if (direct) Blue else Color(0xFF43A047)))
                    val via = stringResource(if (direct) R.string.lobby_via_direct else R.string.lobby_via_wifi)
                    Text("  ${game.name} · $via", color = AppTheme.color.textColor, style = AppTheme.style.body, modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.lobby_join), color = Blue, style = AppTheme.style.body)
                }
            }
        }
        OutlinedTextField(
            value = address,
            onValueChange = { address = it },
            singleLine = true,
            placeholder = { Text(stringResource(R.string.lobby_manual_hint)) },
            colors = TextFieldDefaults.outlinedTextFieldColors(textColor = AppTheme.color.textColor),
            modifier = Modifier.fillMaxWidth(),
        )
        PrimaryButton(stringResource(R.string.lobby_connect), enabled = address.contains(":"), color = AppTheme.color.surface) { onJoinManual(address) }
    }
}

@Composable
internal fun Tabs(items: List<String>, selected: Int, onSelect: (Int) -> Unit) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(AppTheme.color.surface)
            .padding(3.dp),
    ) {
        items.forEachIndexed { index, title ->
            Text(
                text = title,
                color = if (index == selected) Color.White else AppTheme.color.textSecondaryColor,
                style = AppTheme.style.body,
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(8.dp))
                    .background(if (index == selected) Accent else Color.Transparent)
                    .clickable { onSelect(index) }
                    .padding(vertical = 8.dp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center,
            )
        }
    }
}

@Composable
private fun PlayerSlot(emoji: String, name: String, active: Boolean) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.alpha(if (active) 1f else 0.5f)) {
        Box(Modifier.size(56.dp).clip(CircleShape).background(AppTheme.color.surface), contentAlignment = Alignment.Center) {
            Text(emoji, fontSize = 24.sp)
        }
        Text(name, color = AppTheme.color.textColor, style = AppTheme.style.info, modifier = Modifier.padding(top = 6.dp))
    }
}

@Composable
private fun Hint(text: String) {
    Text(text, color = AppTheme.color.textSecondaryColor, style = AppTheme.style.info, modifier = Modifier.padding(vertical = 4.dp))
}

@Composable
private fun PrimaryButton(text: String, enabled: Boolean, color: Color, onClick: () -> Unit) {
    Text(
        text = text,
        color = AppTheme.color.textColor,
        fontWeight = FontWeight.SemiBold,
        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .alpha(if (enabled) 1f else 0.4f)
            .clip(RoundedCornerShape(12.dp))
            .background(color)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(14.dp),
    )
}

private fun CloseReason.message(): Int = when (this) {
    CloseReason.CONNECTION_FAILED -> R.string.lobby_closed_failed
    CloseReason.VERSION_MISMATCH -> R.string.lobby_closed_version
    CloseReason.PEER_LEFT, CloseReason.LEFT -> R.string.lobby_closed_peer
}
