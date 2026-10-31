package n7.ad2.games.internal.games.show.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.compose.AnswerLook
import n7.ad2.games.internal.compose.answerLook
import n7.ad2.games.internal.games.show.BuildQuestion
import n7.ad2.games.internal.games.show.BuildTile
import n7.ad2.games.internal.games.show.ShowKind
import n7.ad2.games.internal.games.show.ShowOption
import n7.ad2.games.internal.games.show.ShowQuestion
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.show.Voice
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkBad
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.InkGood
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperEdge

/** Cards lie a little askew, like on a table */
private val tilts = listOf(-1.2f, 0.8f, 0.6f, -0.7f)

@Composable
internal fun ColumnScope.QuestionView(state: ShowViewModel.State, question: ShowQuestion, now: Long, actions: ShowActions) {
    when (question) {
        is ShowQuestion.Pick -> {
            if (question.kind == ShowKind.VOICE) VoiceCard(state, question, actions) else SpellCard(question)
            val final = state.match.current.final
            if (final && state.buzzShare == null && state.myAnswer == null) {
                BuzzBoard(state, actions)
            } else {
                Answers(state, question.options, actions)
            }
        }
        is ShowQuestion.Compare -> CompareView(state, question, actions)
        is ShowQuestion.Build -> BuildView(state, question.question, actions)
    }
}

/** The spell to guess about: for the mana question with its level */
@Composable
private fun SpellCard(question: ShowQuestion.Pick) {
    PaperPanel {
        AsyncImage(
            model = question.spellImage,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.size(104.dp).clip(RoundedCornerShape(14.dp)).border(3.dp, Ink.copy(alpha = 0.15f), RoundedCornerShape(14.dp)),
        )
        if (question.kind == ShowKind.MANA) {
            Text(stringResource(R.string.show_level, question.spellLevel), color = InkDim, fontSize = 13.sp, fontWeight = FontWeight.Bold)
        }
    }
}

/** The voice: plays by itself when the question opens, one more time on a tap; without a sound - the line as text */
@Composable
private fun VoiceCard(state: ShowViewModel.State, question: ShowQuestion.Pick, actions: ShowActions) {
    PaperPanel {
        Text(if (state.voice == Voice.PLAYING) "🔊" else "🔈", fontSize = 40.sp)
        when (state.voice) {
            Voice.PLAYING -> Text(stringResource(R.string.show_voice_playing), color = Ink, fontSize = 15.sp, fontWeight = FontWeight.Bold)
            Voice.FAILED -> Text(
                stringResource(R.string.show_voice_failed, question.voice?.text.orEmpty()),
                color = Ink,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
            )
            Voice.READY -> {
                val canReplay = state.replaysLeft > 0 && state.phase == ShowViewModel.State.Phase.QUESTION
                Text(
                    stringResource(R.string.show_voice_replay, state.replaysLeft),
                    color = if (canReplay) Ink else InkDim,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .clip(RoundedCornerShape(10.dp))
                        .border(1.5.dp, if (canReplay) Ink else InkDim.copy(alpha = 0.4f), RoundedCornerShape(10.dp))
                        .clickable(enabled = canReplay, role = Role.Button, onClick = actions.onReplay)
                        .padding(horizontal = 14.dp, vertical = 8.dp),
                )
            }
            Voice.LOADING, Voice.NONE -> Text(stringResource(R.string.show_voice_loading), color = InkDim, fontSize = 14.sp)
        }
        // after the answer everyone sees what the line was
        if (state.myAnswer != null && state.voice != Voice.FAILED) {
            Text("«${question.voice?.text.orEmpty()}»", color = InkDim, fontSize = 13.sp, textAlign = TextAlign.Center)
        }
    }
}

/** Four answer cards in two rows */
@Composable
private fun Answers(state: ShowViewModel.State, options: List<ShowOption>, actions: ShowActions) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        options.chunked(2).forEachIndexed { row, pair ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                pair.forEachIndexed { column, option ->
                    val index = row * 2 + column
                    AnswerCard(option, index, state, Modifier.weight(1f)) { actions.onPick(index) }
                }
            }
        }
    }
}

@Composable
private fun AnswerCard(option: ShowOption, index: Int, state: ShowViewModel.State, modifier: Modifier, onClick: () -> Unit) {
    val hidden = index in state.hidden
    val look = answerLook(isRight = option.isRight, isPicked = state.picked == index, reveal = state.myAnswer != null)
    val edge = when (look) {
        AnswerLook.RIGHT -> InkGood
        AnswerLook.WRONG -> InkBad
        AnswerLook.PICKED -> ShowGold
        AnswerLook.IDLE -> PaperEdge
    }
    val shape = RoundedCornerShape(14.dp)
    Column(
        modifier = modifier
            .rotate(tilts[index % tilts.size])
            .alpha(if (hidden) 0.25f else 1f)
            .shadow(5.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, edge, shape)
            .clickable(enabled = state.canAnswer && !hidden, role = Role.Button, onClick = onClick)
            .padding(8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        if (option.imageUrl != null) {
            AsyncImage(
                model = option.imageUrl,
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxWidth().aspectRatio(1.6f).clip(RoundedCornerShape(10.dp)),
            )
            Text(option.label, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold, maxLines = 1, overflow = TextOverflow.Ellipsis)
        } else {
            Text(option.label, color = Ink, fontSize = 30.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.padding(vertical = 18.dp))
        }
    }
}

/** The final before the buzz: the answers lie face down */
@Composable
private fun BuzzBoard(state: ShowViewModel.State, actions: ShowActions) {
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        repeat(4) { index ->
            val shape = RoundedCornerShape(12.dp)
            Box(
                Modifier.weight(1f).height(64.dp).rotate(tilts[index]).shadow(4.dp, shape).clip(shape).background(Ink).border(3.dp, PaperEdge, shape),
                contentAlignment = Alignment.Center,
            ) {
                Text("?", color = ShowGold, fontSize = 26.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
    Spacer(Modifier.height(4.dp))
    val shape = RoundedCornerShape(18.dp)
    Text(
        text = stringResource(R.string.show_buzz),
        color = Ink,
        fontSize = 26.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .shadow(6.dp, shape)
            .clip(shape)
            .background(ShowGold)
            .border(3.dp, PaperEdge, shape)
            .clickable(enabled = state.phase == ShowViewModel.State.Phase.QUESTION, role = Role.Button, onClick = actions.onBuzz)
            .padding(vertical = 22.dp),
    )
}

/** Two heroes and a stat: tap the one who has more; the values open after the answer */
@Composable
private fun CompareView(state: ShowViewModel.State, question: ShowQuestion.Compare, actions: ShowActions) {
    PaperPanel {
        Text(stringResource(question.stat.question), color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, textAlign = TextAlign.Center)
    }
    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
        question.options.forEachIndexed { index, option ->
            Column(Modifier.weight(1f), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AnswerCard(option, index, state, Modifier.fillMaxWidth()) { actions.onPick(index) }
                val value = if (state.myAnswer != null) question.values[index] else "?"
                Text(value, color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

/** The item and its slots, the tiles to pick from and the button */
@Composable
private fun ColumnScope.BuildView(state: ShowViewModel.State, build: BuildQuestion, actions: ShowActions) {
    val reveal = state.myAnswer != null
    PaperPanel {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
            AsyncImage(
                model = build.target.imageUrl,
                contentDescription = build.target.title,
                contentScale = ContentScale.Crop,
                modifier = Modifier.size(width = 72.dp, height = 52.dp).clip(RoundedCornerShape(10.dp)),
            )
            Column(Modifier.weight(1f)) {
                Text(build.target.title, color = Ink, fontWeight = FontWeight.ExtraBold, fontSize = 17.sp, maxLines = 2)
                Text(stringResource(R.string.build_pick, build.slots), color = InkDim, fontSize = 12.sp)
            }
            Text(build.target.cost.toString(), color = Color(0xFF9A6B12), fontSize = 15.sp, fontWeight = FontWeight.Bold)
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            val chosen = state.tiles.toList()
            repeat(build.slots) { slot ->
                val tile = chosen.getOrNull(slot)?.let { build.tiles[it] }
                val shape = RoundedCornerShape(10.dp)
                Box(
                    Modifier.weight(1f).height(48.dp).clip(shape).background(Ink.copy(alpha = if (tile != null) 0.12f else 0.05f)).border(1.5.dp, InkDim.copy(alpha = 0.5f), shape),
                    contentAlignment = Alignment.Center,
                ) {
                    if (tile != null) TileImage(tile) else Text("+", color = InkDim, fontSize = 20.sp)
                }
            }
        }
    }
    Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        build.tiles.chunked(4).forEachIndexed { row, tiles ->
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                tiles.forEachIndexed { column, tile ->
                    val index = row * 4 + column
                    BuildTileCard(tile, isSelected = index in state.tiles, reveal = reveal, enabled = state.canAnswer, modifier = Modifier.weight(1f)) { actions.onTile(index) }
                }
            }
        }
    }
    val ready = state.canAnswer && state.tiles.size == build.slots
    val shape = RoundedCornerShape(14.dp)
    Text(
        text = stringResource(R.string.build_button),
        color = Ink,
        fontSize = 16.sp,
        fontWeight = FontWeight.ExtraBold,
        textAlign = TextAlign.Center,
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (ready) 1f else 0.4f)
            .clip(shape)
            .background(ShowGold)
            .clickable(enabled = ready, role = Role.Button, onClick = actions.onBuild)
            .padding(14.dp),
    )
}

@Composable
private fun BuildTileCard(tile: BuildTile, isSelected: Boolean, reveal: Boolean, enabled: Boolean, modifier: Modifier, onClick: () -> Unit) {
    val edge = when {
        reveal && tile.isCorrect -> InkGood
        reveal && isSelected -> InkBad
        isSelected -> ShowGold
        else -> PaperEdge
    }
    val shape = RoundedCornerShape(10.dp)
    Column(
        modifier = modifier
            .clip(shape)
            .background(PaperCard)
            .border(2.dp, edge, shape)
            .clickable(enabled = enabled, onClick = onClick)
            .padding(vertical = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        // a picked tile already lies in a slot, here it fades
        val contentAlpha = if (isSelected && !reveal) 0.4f else 1f
        Box(Modifier.size(48.dp, 36.dp).clip(RoundedCornerShape(6.dp)).alpha(contentAlpha), contentAlignment = Alignment.Center) { TileImage(tile) }
        Text(
            text = tile.item?.cost?.toString() ?: stringResource(R.string.build_recipe),
            color = InkDim,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            maxLines = 1,
            modifier = Modifier.padding(top = 4.dp).alpha(contentAlpha),
        )
    }
}

@Composable
private fun TileImage(tile: BuildTile) {
    val item = tile.item
    if (item == null) {
        Text("📜", fontSize = 20.sp)
    } else {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(6.dp)),
        )
    }
}
