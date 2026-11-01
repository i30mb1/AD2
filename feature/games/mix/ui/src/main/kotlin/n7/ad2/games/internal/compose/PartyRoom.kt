package n7.ad2.games.internal.compose

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.data.maxPlayersOf
import n7.ad2.games.internal.games.show.compose.PlayerColors
import n7.ad2.games.internal.games.show.compose.ShowGold
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.InkGood
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperEdge
import n7.ad2.games.internal.party.Lobby
import n7.ad2.games.internal.party.Session
import n7.ad2.games.internal.party.Via
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.MAX_PLAYERS
import n7.ad2.games.room.Player

@Immutable
internal class RoomActions(
    val onPick: (gameId: String, mode: GameMode) -> Unit = { _, _ -> },
    val onReady: (Boolean) -> Unit = {},
    val onStart: () -> Unit = {},
    val onLeave: () -> Unit = {},
)

private val DashColor = Color(0xFF6B5F53)

/**
 * The room for up to six players: who is in and ready, the game the host picked and the rest of the games.
 * The host picks a game and its mode and starts once everyone is ready; a guest presses «Ready».
 */
@Composable
internal fun PartyRoom(session: Session, games: List<GameVO>, myAddress: String?, actions: RoomActions, modifier: Modifier = Modifier) {
    var inviting by rememberSaveable { mutableStateOf(false) }
    BackHandler(enabled = inviting) { inviting = false }
    val game = games.firstOrNull { it.id == session.lobby.gameId }
    Box(modifier.fillMaxSize()) {
        Column(Modifier.fillMaxSize().navigationBarsPadding().padding(horizontal = 16.dp)) {
            Column(
                Modifier.weight(1f).verticalScroll(rememberScrollState()).padding(vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                Header(actions.onLeave)
                SectionLabel(stringResource(R.string.party_in_room, session.players.size, MAX_PLAYERS))
                Players(session, onInvite = { inviting = true })
                SectionLabel(if (session.isHost) stringResource(R.string.party_game_you_pick) else stringResource(R.string.party_game_host_picks, session.host.name))
                if (game != null) PickedGame(game, session, actions.onPick) else NoGame(session)
                if (session.isHost) OtherGames(games.filter { it != game }, session, actions.onPick)
            }
            Bottom(session, game, actions)
        }
        if (inviting) Invite(myAddress, onClose = { inviting = false })
    }
}

@Composable
private fun Header(onLeave: () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(2.dp)) {
            Text(stringResource(R.string.party_room_title), color = AppTheme.color.textColor, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.party_room_subtitle, MAX_PLAYERS), color = AppTheme.color.textSecondaryColor, fontSize = 12.sp)
        }
        Text(
            text = stringResource(R.string.party_leave),
            color = AppTheme.color.textSecondaryColor,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onLeave).padding(horizontal = 12.dp, vertical = 12.dp),
        )
    }
}

@Composable
private fun SectionLabel(text: String) {
    Text(text.uppercase(), color = AppTheme.color.textSecondaryColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, letterSpacing = 1.sp)
}

/** Three tiles in a row: the players with their state, then the free places */
@Composable
private fun Players(session: Session, onInvite: () -> Unit) {
    val tiles = session.players.map { it as Player? } + List(MAX_PLAYERS - session.players.size) { null }
    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        tiles.chunked(3).forEach { row ->
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                row.forEach { player ->
                    val tile = Modifier.weight(1f).height(118.dp)
                    if (player != null) PlayerTile(player, session, tile) else FreeTile(invite = session.isHost, onInvite, tile)
                }
            }
        }
    }
}

@Composable
private fun PlayerTile(player: Player, session: Session, modifier: Modifier) {
    val index = session.players.indexOf(player)
    val isMe = player.id == session.meId
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier.clip(shape).background(PaperCard).border(3.dp, if (isMe) ShowGold else PaperEdge, shape).padding(6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(6.dp, Alignment.CenterVertically),
    ) {
        Avatar(player.name, PlayerColors[index % PlayerColors.size], 44.dp)
        Text(if (isMe) stringResource(R.string.party_you) else player.name, color = Ink, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        val chip = RoundedCornerShape(8.dp)
        when {
            index == 0 -> Text(stringResource(R.string.party_status_host), color = Ink, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(chip).background(ShowGold).padding(horizontal = 8.dp, vertical = 2.dp))
            session.lobby.isReady(player.id, session.players) -> Text(
                stringResource(R.string.party_status_ready),
                color = Color.White,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(chip).background(InkGood).padding(horizontal = 8.dp, vertical = 2.dp),
            )
            else -> Text(
                stringResource(R.string.party_status_picking),
                color = InkDim,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.border(1.dp, InkDim, chip).padding(horizontal = 8.dp, vertical = 2.dp),
            )
        }
    }
}

/** A free place; the host can tell a friend how to come in */
@Composable
private fun FreeTile(invite: Boolean, onInvite: () -> Unit, modifier: Modifier) {
    Column(
        modifier.drawBehind {
            drawRoundRect(DashColor, cornerRadius = CornerRadius(14.dp.toPx()), style = Stroke(2.dp.toPx(), pathEffect = PathEffect.dashPathEffect(floatArrayOf(6.dp.toPx(), 5.dp.toPx()))))
        },
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically),
    ) {
        Text(stringResource(R.string.party_free), color = AppTheme.color.textSecondaryColor, fontSize = 12.sp)
        if (invite) {
            Text(
                text = stringResource(R.string.party_invite),
                color = AppTheme.color.textColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(AppTheme.color.surface).clickable(onClick = onInvite).padding(horizontal = 12.dp, vertical = 8.dp),
            )
        }
    }
}

/** The game of the room on paper with its modes; the host switches the mode */
@Composable
private fun PickedGame(game: GameVO, session: Session, onPick: (String, GameMode) -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    val fits = session.players.size <= maxPlayersOf(game.id)
    Row(
        Modifier.fillMaxWidth().clip(shape).background(PaperCard).border(3.dp, ShowGold, shape).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp),
    ) {
        GameCover(game, 76.dp, shape = RoundedCornerShape(12.dp))
        Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(4.dp)) {
            Text(game.title, color = Ink, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
            val about = stringResource(game.about())
            val everyone = when (session.players.size) {
                2 -> stringResource(R.string.party_both_players)
                else -> pluralStringResource(R.plurals.party_all_players, session.players.size, session.players.size)
            }
            Text(
                text = if (fits) "$everyone · $about" else stringResource(R.string.party_limit_two),
                color = InkDim,
                fontSize = 12.sp,
            )
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                game.modes.filter { it != GameMode.SOLO }.forEach { mode ->
                    val selected = mode == session.lobby.mode
                    val chip = RoundedCornerShape(8.dp)
                    Text(
                        text = stringResource(mode.label(session.players.size)),
                        color = Ink,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .clip(chip)
                            .then(if (selected) Modifier.background(ShowGold) else Modifier.border(1.dp, InkDim, chip))
                            .clickable(enabled = session.isHost && !selected) { onPick(game.id, mode) }
                            .padding(horizontal = 8.dp, vertical = 4.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun NoGame(session: Session) {
    val shape = RoundedCornerShape(16.dp)
    Box(Modifier.fillMaxWidth().clip(shape).border(2.dp, DashColor, shape).padding(20.dp), contentAlignment = Alignment.Center) {
        Text(
            text = if (session.isHost) stringResource(R.string.party_no_game_host) else stringResource(R.string.party_no_game_guest, session.host.name),
            color = AppTheme.color.textSecondaryColor,
            fontSize = 14.sp,
            textAlign = TextAlign.Center,
        )
    }
}

/** The other games for the host to pick; the ones for two are dimmed when more are in the room */
@Composable
private fun OtherGames(games: List<GameVO>, session: Session, onPick: (String, GameMode) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        games.forEach { game ->
            val max = maxPlayersOf(game.id)
            val fits = session.players.size <= max
            val mode = session.lobby.mode.takeIf { it in game.modes } ?: GameMode.VERSUS
            Row(
                Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .alpha(if (fits) 1f else 0.45f)
                    .clip(RoundedCornerShape(12.dp))
                    .background(AppTheme.color.surface)
                    .clickable(enabled = fits) { onPick(game.id, mode) }
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(game.title, color = AppTheme.color.textColor, fontSize = 14.sp, fontWeight = FontWeight.Bold, modifier = Modifier.weight(1f))
                Text(
                    text = if (max == 2) stringResource(R.string.party_limit_two) else stringResource(R.string.party_limit_room, max, stringResource(game.about())),
                    color = AppTheme.color.textSecondaryColor,
                    fontSize = 12.sp,
                )
            }
        }
    }
}

/** What the room waits for and the button: the host starts, a guest is ready or not */
@Composable
private fun Bottom(session: Session, game: GameVO?, actions: RoomActions) {
    val lobby = session.lobby
    val waiting = session.players.filter { !lobby.isReady(it.id, session.players) }.map { it.name }
    val hint = when {
        game == null -> if (session.isHost) stringResource(R.string.party_hint_pick) else stringResource(R.string.party_no_game_guest, session.host.name)
        session.players.size > maxPlayersOf(game.id) -> stringResource(R.string.party_hint_two_only, game.title)
        !session.isHost && !session.meReady -> stringResource(R.string.party_hint_press_ready)
        !session.isHost -> stringResource(R.string.party_hint_wait_start, session.host.name)
        waiting.size == 1 -> stringResource(R.string.party_hint_waiting_one, waiting.single())
        waiting.isNotEmpty() -> stringResource(R.string.party_hint_waiting_many, waiting.joinToString(", "))
        else -> stringResource(R.string.party_hint_all_ready)
    }
    Column(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text(hint, color = AppTheme.color.textSecondaryColor, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
        if (session.isHost) {
            val ready = lobby.readyCount(session.players)
            val canStart = lobby.canStart(session.players)
            BigButton(
                text = if (ready == session.players.size) stringResource(R.string.party_start_all) else stringResource(R.string.party_start, ready, session.players.size),
                filled = true,
                enabled = canStart,
                onClick = actions.onStart,
            )
        } else {
            BigButton(
                text = stringResource(if (session.meReady) R.string.party_not_ready else R.string.party_ready),
                filled = !session.meReady,
                enabled = game != null,
                onClick = { actions.onReady(!session.meReady) },
            )
        }
    }
}

@Composable
private fun BigButton(text: String, filled: Boolean, enabled: Boolean, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Box(
        Modifier
            .fillMaxWidth()
            .height(56.dp)
            .alpha(if (enabled) 1f else 0.5f)
            .clip(shape)
            .then(if (filled) Modifier.background(ShowGold).border(3.dp, PaperEdge, shape) else Modifier.border(1.dp, DashColor, shape))
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center,
    ) {
        Text(text, color = if (filled) Ink else AppTheme.color.textColor, fontSize = 16.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** How a friend comes in: open Games on the same Wi-Fi, or type my address */
@Composable
private fun Invite(myAddress: String?, onClose: () -> Unit) {
    Box(
        Modifier
            .fillMaxSize()
            .background(Color(0x99000000))
            .clickable(interactionSource = remember { MutableInteractionSource() }, indication = null, onClick = onClose)
            .padding(24.dp),
        contentAlignment = Alignment.Center,
    ) {
        val shape = RoundedCornerShape(18.dp)
        Column(
            Modifier.fillMaxWidth().clip(shape).background(PaperCard).border(3.dp, PaperEdge, shape).padding(18.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(stringResource(R.string.party_invite_title), color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
            Text(stringResource(R.string.party_invite_text), color = Ink, fontSize = 14.sp)
            if (myAddress != null) {
                Text(stringResource(R.string.party_invite_address), color = InkDim, fontSize = 13.sp)
                Text(myAddress, color = Ink, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
            Spacer(Modifier.height(4.dp))
            Text(
                text = stringResource(R.string.party_invite_ok),
                color = Ink,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(12.dp)).background(ShowGold).clickable(onClick = onClose).padding(12.dp),
            )
        }
    }
}

private fun GameVO.about(): Int = when (this) {
    is GameVO.Show -> R.string.party_about_show
    is GameVO.Mid -> R.string.party_about_mid
    is GameVO.Invoker -> R.string.party_about_invoker
    is GameVO.XO -> R.string.party_about_xo
}

/** Versus is head to head for two and «who scores more» for more */
private fun GameMode.label(players: Int): Int = when (this) {
    GameMode.COOP -> R.string.party_mode_coop
    else -> if (players <= 2) R.string.party_mode_duel else R.string.party_mode_versus
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyRoomHostPreview() {
    val players = listOf(Player("me", "Евгений"), Player("p", "Pixel 8"), Player("g", "Galaxy"), Player("r", "Redmi"))
    val session = Session(players, meId = "me", lobby = Lobby("show", GameMode.VERSUS, setOf("p", "g")), via = null)
    AppTheme(isDarkTheme = true) { PartyRoom(session, previewGames(), myAddress = "192.168.31.189:43905", actions = RoomActions()) }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun PartyRoomGuestPreview() {
    val players = listOf(Player("h", "Pixel 8"), Player("me", "Евгений"))
    val session = Session(players, meId = "me", lobby = Lobby("xo", GameMode.VERSUS), via = Via.WIFI)
    AppTheme(isDarkTheme = true) { PartyRoom(session, previewGames(), myAddress = null, actions = RoomActions()) }
}

@Composable
private fun previewGames(): List<GameVO> {
    val context = androidx.compose.ui.platform.LocalContext.current
    val res = object : n7.ad2.Resources {
        override fun getString(resourceID: Int): String = context.getString(resourceID)
        override fun getString(resourceID: Int, formatArgs: Any): String = context.getString(resourceID, formatArgs)
        override fun getAssets(path: String) = throw UnsupportedOperationException()
        override fun getExternalFilesDir(path: String) = null
        override fun getFilesDir() = null
        override fun getConfiguration() = null
    }
    return listOf(GameVO.Show(res), GameVO.Mid(res), GameVO.Invoker(res), GameVO.XO(res))
}
