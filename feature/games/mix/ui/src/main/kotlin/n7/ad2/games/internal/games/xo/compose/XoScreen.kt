package n7.ad2.games.internal.games.xo.compose

import androidx.activity.compose.BackHandler
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.Buzz
import n7.ad2.games.internal.compose.BuzzOnIncrease
import n7.ad2.games.internal.games.xo.Mark
import n7.ad2.games.internal.games.xo.XoMove
import n7.ad2.games.internal.games.xo.XoViewModel
import n7.ad2.games.internal.games.xo.XoViewModel.DrawOffer
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkBad
import n7.ad2.games.internal.paper.InkGood
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.room.GameMode

@Immutable
internal class XoActions(
    val onMove: (XoMove) -> Unit = {},
    val onOfferDraw: () -> Unit = {},
    val onAcceptDraw: () -> Unit = {},
    val onDeclineDraw: () -> Unit = {},
    val onResign: () -> Unit = {},
    val onAgain: () -> Unit = {},
    val onOtherGame: () -> Unit = {},
)

internal val XoLight = Color(0xFFF4EEE3)
internal val XoLightDim = Color(0xFFC9C2B6)
private val DarkCard = Color(0xFF2F3136)
private val Outline = Color(0xFF6B5F53)
private val Warn = Color(0xFFE58C80)

@Composable
internal fun XoScreen(state: XoViewModel.State, actions: XoActions) {
    BuzzOnIncrease(state.game.taken(state.myMark), Buzz.GOOD)
    BuzzOnIncrease(state.game.taken(state.myMark.other), Buzz.BAD)
    var zoomed by rememberSaveable { mutableStateOf<Int?>(null) }
    // a zoomed board stays only while I can move there
    val zoom = zoomed?.takeIf { state.myTurn && state.game.isPlayable(it) }
    BackHandler(enabled = zoom != null) { zoomed = null }
    val over = state.result != null
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AppTheme.color.background)
            .systemBarsPadding()
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        Header()
        Players(state)
        if (!over) Text(stringResource(R.string.xo_rule), color = XoLightDim, fontSize = 12.sp, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(horizontal = 8.dp))
        XoField(
            state = state,
            zoom = zoom,
            onZoom = { zoomed = it },
            onMove = { move ->
                zoomed = null
                actions.onMove(move)
            },
            modifier = Modifier.fillMaxWidth(if (over) 0.72f else 1f).align(Alignment.CenterHorizontally),
        )
        if (over) {
            XoEnd(state, actions)
        } else {
            Status(state, zoomed = zoom != null, onBack = { zoomed = null })
            Spacer(Modifier.weight(1f))
            Bottom(state, actions)
        }
    }
}

@Composable
private fun Header() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(stringResource(R.string.xo), color = XoLight, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
        Text(stringResource(R.string.xo_sides), color = XoLightDim, fontSize = 12.sp)
    }
}

/** Me on paper, the opponent on the dark card; the one to move is ringed in gold, the boards taken in the middle */
@Composable
private fun Players(state: XoViewModel.State) {
    val game = state.game
    val peerMark = state.myMark.other
    Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        PlayerCard(
            name = stringResource(if (state.myTurn) R.string.xo_you_move else R.string.show_you),
            mark = state.myMark,
            paper = true,
            active = state.result == null && game.turn == state.myMark,
            modifier = Modifier.weight(1f),
        )
        Text("${game.taken(state.myMark)} : ${game.taken(peerMark)}", color = XoLight, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold)
        PlayerCard(
            name = state.peerName ?: stringResource(R.string.xo_bot),
            mark = peerMark,
            paper = false,
            active = state.result == null && game.turn == peerMark,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PlayerCard(name: String, mark: Mark, paper: Boolean, active: Boolean, modifier: Modifier) {
    val shape = RoundedCornerShape(14.dp)
    val side = stringResource(if (mark == Mark.X) R.string.xo_side_x else R.string.xo_side_o)
    val sideColor = when {
        !paper -> mark.color()
        mark == Mark.X -> InkGood
        else -> InkBad
    }
    Row(
        modifier = modifier
            .clip(shape)
            .background(if (paper) PaperCard else DarkCard)
            .then(if (active) Modifier.border(3.dp, XoGold, shape) else Modifier)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp, if (paper) Alignment.Start else Alignment.End),
    ) {
        if (paper) MarkAvatar(mark)
        Column(horizontalAlignment = if (paper) Alignment.Start else Alignment.End) {
            Text(name, color = if (paper) Ink else XoLight, fontSize = 13.sp, fontWeight = FontWeight.Bold, maxLines = 1, overflow = TextOverflow.Ellipsis)
            Text(side, color = sideColor, fontSize = 11.sp, fontWeight = FontWeight.Bold, maxLines = 1)
        }
        if (!paper) MarkAvatar(mark)
    }
}

@Composable
private fun MarkAvatar(mark: Mark) {
    Box(Modifier.size(32.dp).clip(CircleShape).background(if (mark == Mark.X) InkGood else InkBad), contentAlignment = Alignment.Center) {
        Text(mark.name, color = Color.White, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
    }
}

/** Whose move and where; under it how to zoom a board in or back to the field */
@Composable
private fun Status(state: XoViewModel.State, zoomed: Boolean, onBack: () -> Unit) {
    val game = state.game
    val peer = state.peerName
    val text = when {
        !state.peerReady -> stringResource(R.string.xo_waiting, peer.orEmpty())
        state.myTurn -> game.target?.let { stringResource(R.string.xo_your_move_board, stringResource(BOARD_NAMES[it])) } ?: stringResource(R.string.xo_your_move_any)
        peer == null -> stringResource(R.string.xo_bot_thinking)
        else -> stringResource(R.string.xo_peer_move, peer)
    }
    Column(Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(text, color = XoLight, fontSize = 15.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center)
        when {
            zoomed -> Text(
                text = stringResource(R.string.xo_back_to_field),
                color = XoGold,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.clip(RoundedCornerShape(10.dp)).clickable(onClick = onBack).padding(horizontal = 12.dp, vertical = 6.dp),
            )
            state.myTurn -> Text(stringResource(R.string.xo_zoom_hint), color = XoLightDim, fontSize = 12.sp)
        }
    }
}

/** A draw offered to me, or my buttons: offer a draw (over the network) and resign */
@Composable
private fun Bottom(state: XoViewModel.State, actions: XoActions) {
    if (state.drawOffer == DrawOffer.RECEIVED) {
        Column(
            Modifier.fillMaxWidth().clip(RoundedCornerShape(14.dp)).background(PaperCard).padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Text(stringResource(R.string.xo_draw_offered, state.peerName.orEmpty()), color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                XoButton(stringResource(R.string.xo_accept), filled = true, Modifier.weight(1f), onClick = actions.onAcceptDraw)
                XoButton(stringResource(R.string.xo_decline), filled = false, Modifier.weight(1f), dark = true, onClick = actions.onDeclineDraw)
            }
        }
        return
    }
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        if (state.mode != GameMode.SOLO) {
            val draw = when (state.drawOffer) {
                DrawOffer.SENT -> R.string.xo_draw_sent
                DrawOffer.DECLINED -> R.string.xo_draw_declined
                else -> R.string.xo_offer_draw
            }
            XoButton(stringResource(draw), filled = true, Modifier.weight(1f), enabled = state.drawOffer == null && state.peerReady, onClick = actions.onOfferDraw)
        }
        XoButton(stringResource(if (state.confirmResign) R.string.xo_resign_sure else R.string.xo_resign), filled = false, Modifier.weight(1f), warn = state.confirmResign, onClick = actions.onResign)
    }
}

@Composable
private fun XoButton(text: String, filled: Boolean, modifier: Modifier, enabled: Boolean = true, dark: Boolean = false, warn: Boolean = false, onClick: () -> Unit) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier
            .height(48.dp)
            .clip(shape)
            .then(if (filled) Modifier.background(DarkCard) else Modifier.border(1.dp, if (warn) Warn else Outline, shape))
            .clickable(enabled = enabled, onClick = onClick)
            .padding(horizontal = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        val color = when {
            warn -> Warn
            dark -> Ink
            filled -> XoLight
            else -> XoLightDim
        }
        Text(text, color = if (enabled) color else color.copy(alpha = 0.6f), fontSize = 14.sp, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, maxLines = 2)
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun XoScreenPreview() {
    AppTheme(isDarkTheme = true) {
        XoScreen(XoViewModel.State(GameMode.VERSUS, previewGame(), Mark.X, peerName = "Pixel 8", peerReady = true), XoActions())
    }
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 844)
@Composable
private fun XoScreenDrawPreview() {
    AppTheme(isDarkTheme = true) {
        XoScreen(
            XoViewModel.State(GameMode.VERSUS, previewGame().copy(turn = Mark.O, target = 8), Mark.X, peerName = "Pixel 8", peerReady = true, drawOffer = DrawOffer.RECEIVED),
            XoActions(),
        )
    }
}
