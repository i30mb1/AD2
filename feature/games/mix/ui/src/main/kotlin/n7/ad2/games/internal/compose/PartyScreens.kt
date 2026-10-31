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
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.party.NearbyPlayer
import n7.ad2.games.internal.party.PartyState
import n7.ad2.games.internal.party.Pick
import n7.ad2.games.internal.party.Via
import n7.ad2.games.room.GameMode

internal enum class PartyBanner { ALONE, NEARBY, CONNECTING, FAILED, TOGETHER }

/** What the top of the games list shows: searching, someone to play with, the connection in progress or failed, or the pair picking together. */
internal fun partyBanner(party: PartyState): PartyBanner = when {
    party.session != null -> PartyBanner.TOGETHER
    party.connecting -> PartyBanner.CONNECTING
    party.failed -> PartyBanner.FAILED
    party.nearby.isNotEmpty() -> PartyBanner.NEARBY
    else -> PartyBanner.ALONE
}

/** Whether I and the peer picked the game [gameId], for the A / P marks on its tile. */
internal fun tileMarks(gameId: String, party: PartyState): Pair<Boolean, Boolean> = (party.myPick?.gameId == gameId) to (party.peerPick?.gameId == gameId)

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
        else -> Column(
            modifier = modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .background(AppTheme.color.surface)
                .border(1.dp, if (party.failed) MeColor else PeerColor, RoundedCornerShape(12.dp))
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            val first = party.nearby.firstOrNull()
            when {
                // ручное подключение по адресу идёт без имени
                party.connecting -> Text(
                    text = first?.name?.takeIf { it.isNotBlank() }?.let { stringResource(R.string.party_connecting, it) } ?: stringResource(R.string.party_connecting_plain),
                    color = AppTheme.color.textColor,
                    fontSize = 14.sp,
                )
                party.failed -> Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(stringResource(R.string.party_failed), color = AppTheme.color.textColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
                    Text(stringResource(R.string.party_help), color = PeerColor, fontSize = 12.sp, modifier = Modifier.clickable(onClick = onHelp).padding(4.dp))
                }
            }
            party.nearby.forEachIndexed { index, player ->
                if (index == 0) MainNearby(player, enabled = !party.connecting) { onPlay(player) } else OtherNearby(player, enabled = !party.connecting) { onPlay(player) }
            }
        }
    }
}

@Composable
private fun MainNearby(player: NearbyPlayer, enabled: Boolean, onPlay: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        Box {
            Avatar(player.name, PeerColor, 40.dp)
            Box(Modifier.align(Alignment.BottomEnd).size(12.dp).clip(CircleShape).background(RightColor).border(2.dp, AppTheme.color.surface, CircleShape))
        }
        Column(Modifier.weight(1f)) {
            Text(stringResource(R.string.party_nearby, player.name), color = AppTheme.color.textColor, fontSize = 15.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text("${player.via.label()} · ${stringResource(R.string.party_in_games)}", color = DimColor, fontSize = 12.sp)
        }
        Text(
            text = stringResource(R.string.party_play),
            color = Color.White,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.alpha(if (enabled) 1f else 0.4f).clip(RoundedCornerShape(18.dp)).background(MeColor).clickable(enabled = enabled, onClick = onPlay)
                .padding(horizontal = 16.dp, vertical = 9.dp),
        )
    }
}

@Composable
private fun OtherNearby(player: NearbyPlayer, enabled: Boolean, onPlay: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        Avatar(player.name, RightColor, 24.dp)
        Text("${player.name} · ${player.via.label()}", color = AppTheme.color.textSecondaryColor, fontSize = 13.sp, modifier = Modifier.weight(1f))
        Text(stringResource(R.string.party_play), color = PeerColor, fontSize = 13.sp, fontWeight = FontWeight.Medium, modifier = Modifier.clickable(enabled = enabled, onClick = onPlay).padding(4.dp))
    }
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

/**
 * Picking a game together: my pick on the left, the peer's on the right, a mode switch and the games grid.
 * The game starts by itself once both picks are the same.
 */
@Composable
internal fun PartySelect(
    party: PartyState,
    peerName: String,
    games: List<GameVO>,
    onPick: (gameId: String, mode: GameMode) -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val partyGames = games.filter { it.quizId != null }
    var mode by rememberSaveable { mutableStateOf(party.myPick?.mode ?: GameMode.VERSUS) }
    Column(modifier.fillMaxSize().padding(horizontal = 12.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Column(Modifier.weight(1f)) {
                Text(stringResource(R.string.party_with, peerName), color = AppTheme.color.textColor, fontSize = 18.sp, fontWeight = FontWeight.Bold)
                Text("● " + stringResource(R.string.party_online), color = RightColor, fontSize = 12.sp)
            }
            Text(stringResource(R.string.party_leave), color = DimColor, fontSize = 13.sp, modifier = Modifier.clickable(onClick = onLeave).padding(8.dp))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            PickCard(stringResource(R.string.game_me_letter), stringResource(R.string.party_you), MeColor, party.myPick, partyGames, mirrored = false, Modifier.weight(1f))
            PickCard(peerName, peerName, PeerColor, party.peerPick, partyGames, mirrored = true, Modifier.weight(1f))
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            listOf(GameMode.VERSUS, GameMode.COOP).forEach { option ->
                ModeChip(option, selected = option == mode, peerHere = party.peerPick?.mode == option, Modifier.weight(1f)) {
                    mode = option
                    party.myPick?.let { onPick(it.gameId, option) }
                }
            }
        }
        partyGames.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                row.forEach { game ->
                    val id = requireNotNull(game.quizId)
                    val (mine, peers) = tileMarks(id, party)
                    GameTile(game, mine, peers, peerName, Modifier.weight(1f)) { onPick(id, mode) }
                }
                repeat(3 - row.size) { Spacer(Modifier.weight(1f)) }
            }
        }
        Spacer(Modifier.weight(1f))
        Text(stringResource(R.string.party_pick_hint), color = DimColor, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp))
    }
}

@Composable
private fun PickCard(avatarName: String, label: String, color: Color, pick: Pick?, games: List<GameVO>, mirrored: Boolean, modifier: Modifier) {
    val game = games.firstOrNull { it.quizId == pick?.gameId }
    val align = if (mirrored) Alignment.End else Alignment.Start
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.surface)
            .border(1.dp, color, RoundedCornerShape(12.dp))
            .padding(10.dp),
        horizontalAlignment = align,
        verticalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            if (!mirrored) Avatar(avatarName, color, 24.dp)
            Text(label, color = AppTheme.color.textSecondaryColor, fontSize = 12.sp, maxLines = 1, overflow = TextOverflow.Ellipsis)
            if (mirrored) Avatar(avatarName, color, 24.dp)
        }
        Box(Modifier.size(64.dp).clip(RoundedCornerShape(12.dp)).background(LineColor), contentAlignment = Alignment.Center) {
            if (game != null) {
                GameCover(game, 64.dp, shape = RoundedCornerShape(12.dp))
            } else {
                Text("?", color = DimColor, fontSize = 24.sp)
            }
        }
        Text(game?.title ?: "—", color = AppTheme.color.textColor, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 2, textAlign = if (mirrored) TextAlign.End else TextAlign.Start)
        if (pick != null) Text(stringResource(if (pick.mode == GameMode.COOP) R.string.party_coop else R.string.party_versus), color = DimColor, fontSize = 12.sp)
        val picked = pick != null
        Text(
            text = stringResource(if (picked) R.string.party_picked else R.string.party_picking),
            color = if (picked) RightColor else AppTheme.color.textSecondaryColor,
            fontSize = 11.sp,
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(if (picked) RightColor.copy(alpha = 0.2f) else LineColor).padding(horizontal = 8.dp, vertical = 2.dp),
        )
    }
}

@Composable
private fun ModeChip(mode: GameMode, selected: Boolean, peerHere: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(18.dp)
    Row(
        modifier = modifier
            .height(36.dp)
            .clip(shape)
            .background(if (selected) MeColor.copy(alpha = 0.15f) else Color.Transparent)
            .border(1.dp, if (selected) MeColor else LineColor, shape)
            .clickable(onClick = onClick),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterHorizontally),
    ) {
        Text(stringResource(if (mode == GameMode.COOP) R.string.party_coop else R.string.party_versus), color = AppTheme.color.textColor, fontSize = 13.sp, fontWeight = FontWeight.Medium)
        if (peerHere) Box(Modifier.size(8.dp).clip(CircleShape).background(PeerColor))
    }
}

@Composable
private fun GameTile(game: GameVO, mine: Boolean, peers: Boolean, peerName: String, modifier: Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    val border = when {
        mine -> MeColor
        peers -> PeerColor
        else -> Color.Transparent
    }
    Box(modifier) {
        Column(
            modifier = Modifier
                .padding(top = 7.dp)
                .fillMaxWidth()
                .clip(shape)
                .background(AppTheme.color.surface)
                .border(1.dp, border, shape)
                .clickable(onClick = onClick)
                .padding(horizontal = 6.dp, vertical = 10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            GameCover(game, 48.dp, shape = RoundedCornerShape(10.dp))
            Text(game.title, color = AppTheme.color.textSecondaryColor, fontSize = 11.sp, lineHeight = 13.sp, textAlign = TextAlign.Center, maxLines = 2, minLines = 2)
        }
        if (mine) Avatar(stringResource(R.string.game_me_letter), MeColor, 18.dp, Modifier.offset(x = 6.dp).border(2.dp, AppTheme.color.background, CircleShape))
        if (peers) Avatar(peerName, PeerColor, 18.dp, Modifier.align(Alignment.TopEnd).offset(x = (-6).dp).border(2.dp, AppTheme.color.background, CircleShape))
    }
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

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartySelectPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val res = object : n7.ad2.Resources {
        override fun getString(resourceID: Int): String = context.getString(resourceID)
        override fun getString(resourceID: Int, formatArgs: Any): String = context.getString(resourceID, formatArgs)
        override fun getAssets(path: String) = throw UnsupportedOperationException()
        override fun getExternalFilesDir(path: String) = null
        override fun getFilesDir() = null
        override fun getConfiguration() = null
    }
    val games = listOf(GameVO.GuessSkillMana(res), GameVO.GuessHero(res), GameVO.HigherLower(res), GameVO.BuildItem(res), GameVO.Apm(res), GameVO.XO(res))
    val party = PartyState(
        session = n7.ad2.games.internal.party.Session(n7.ad2.games.room.Player("p", "Pixel 8"), Via.DIRECT),
        myPick = Pick(requireNotNull(games[0].quizId), GameMode.VERSUS),
        peerPick = Pick(requireNotNull(games[3].quizId), GameMode.COOP),
    )
    AppTheme(isDarkTheme = true) { PartySelect(party, "Pixel 8", games, onPick = { _, _ -> }, onLeave = {}, modifier = Modifier.padding(top = 40.dp)) }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 760)
@Composable
private fun PartyHelpPreview() {
    AppTheme(isDarkTheme = true) { PartyHelp(PartyState(myAddress = "192.168.31.189:43905"), onJoinManual = {}) }
}
