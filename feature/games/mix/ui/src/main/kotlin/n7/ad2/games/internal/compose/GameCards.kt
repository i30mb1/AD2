package n7.ad2.games.internal.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.data.GameVO
import n7.ad2.games.internal.games.mid.CreepType
import n7.ad2.games.internal.games.mid.Side
import n7.ad2.games.internal.games.show.compose.ShowGold
import n7.ad2.games.internal.paper.Ink
import n7.ad2.games.internal.paper.InkDim
import n7.ad2.games.internal.paper.PaperCard
import n7.ad2.games.internal.paper.PaperCreep
import n7.ad2.games.internal.paper.PaperEdge
import n7.ad2.games.internal.paper.PaperLaneBackground
import n7.ad2.games.internal.progress.Progress
import n7.ad2.games.room.MAX_PLAYERS

private const val ASSETS = "file:///android_asset/"
private val InkBody = Color(0xFF4A4038)
private val ChipPaper = Color(0xFFE2D5BC)
private val BarTrack = Color(0xFF45474D)

/** The level of the player and how far the next one is */
@Composable
internal fun LevelChip(progress: Progress, modifier: Modifier = Modifier) {
    Row(
        modifier.clip(RoundedCornerShape(20.dp)).background(AppTheme.color.surface).padding(start = 5.dp, end = 12.dp, top = 5.dp, bottom = 5.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        Box(Modifier.size(28.dp).clip(CircleShape).background(ShowGold), contentAlignment = Alignment.Center) {
            Text(progress.level.toString(), color = Ink, fontSize = 12.sp, fontWeight = FontWeight.ExtraBold)
        }
        Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
            Text(stringResource(R.string.games_level, progress.inLevel, progress.needed), color = AppTheme.color.textSecondaryColor, fontSize = 11.sp)
            Box(Modifier.width(86.dp).height(4.dp).clip(RoundedCornerShape(2.dp)).background(BarTrack)) {
                Box(Modifier.fillMaxHeight().fillMaxWidth(progress.fraction).clip(RoundedCornerShape(2.dp)).background(ShowGold))
            }
        }
    }
}

/** A game on a big paper card: the picture, the record, what it is about and how to play it */
@Composable
internal fun BigGameCard(game: GameVO, record: Int?, tilt: Float, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        Modifier
            .fillMaxWidth()
            .rotate(tilt)
            .shadow(6.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, PaperEdge, shape)
            .clickable(role = Role.Button, onClick = onClick),
    ) {
        GameBanner(game, Modifier.fillMaxWidth().height(116.dp))
        Column(Modifier.padding(start = 14.dp, end = 14.dp, top = 12.dp, bottom = 14.dp), verticalArrangement = Arrangement.spacedBy(6.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(game.title, color = Ink, fontSize = 18.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.weight(1f))
                if (record != null) Text(stringResource(R.string.games_record_badge, record), color = InkDim, fontSize = 12.sp)
            }
            Text(stringResource(game.rules()), color = InkBody, fontSize = 13.sp, lineHeight = 18.sp)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                game.chips().forEach { chip ->
                    Text(chip, color = InkBody, fontSize = 11.sp, fontWeight = FontWeight.Bold, modifier = Modifier.clip(RoundedCornerShape(6.dp)).background(ChipPaper).padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}

/** A game on a small paper card: its sign, the name and a line */
@Composable
internal fun SmallGameCard(game: GameVO, record: Int?, tilt: Float, modifier: Modifier = Modifier, onClick: () -> Unit) {
    val shape = RoundedCornerShape(16.dp)
    Column(
        modifier
            .rotate(tilt)
            .shadow(6.dp, shape)
            .clip(shape)
            .background(PaperCard)
            .border(3.dp, PaperEdge, shape)
            .clickable(role = Role.Button, onClick = onClick)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        GameSign(game)
        Text(game.title, color = Ink, fontSize = 15.sp, fontWeight = FontWeight.ExtraBold)
        Text(stringResource(game.rules()), color = InkBody, fontSize = 12.sp, lineHeight = 16.sp, minLines = 2)
        if (record != null) Text(stringResource(R.string.games_record_badge, record), color = InkDim, fontSize = 11.sp)
    }
}

/** A wide picture of a big card: the lane with creeps for the mid, hero cards on the table for the show */
@Composable
private fun GameBanner(game: GameVO, modifier: Modifier) {
    BoxWithConstraints(modifier.clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))) {
        when (game) {
            is GameVO.Mid -> {
                PaperLaneBackground(Modifier.fillMaxSize())
                val creep = 40.dp
                listOf(0.24f to Side.RADIANT, 0.33f to Side.RADIANT, 0.58f to Side.DIRE, 0.67f to Side.DIRE).forEachIndexed { index, (x, side) ->
                    PaperCreep(
                        if (index % 2 == 0) CreepType.MELEE else CreepType.RANGED,
                        side,
                        killable = index == 2,
                        modifier = Modifier
                            .offset(x = maxWidth * x - creep / 2, y = if (side == Side.RADIANT) 50.dp else 26.dp)
                            .size(creep)
                            .graphicsLayer { if (side == Side.DIRE) scaleX = -1f },
                    )
                }
                Text("+41", color = Color(0xFFF3C969), fontSize = 16.sp, fontWeight = FontWeight.ExtraBold, modifier = Modifier.offset(x = maxWidth * 0.44f, y = 6.dp))
            }
            is GameVO.Show -> {
                Box(Modifier.fillMaxSize().background(Color(0xFF3E6E9C)))
                val cards = listOf(Triple("heroes/pudge/full.webp", 0.08f, -9f), Triple("heroes/lion/full.webp", 0.38f, 4f), Triple("heroes/axe/full.webp", 0.66f, 8f))
                cards.forEach { (path, x, angle) ->
                    val cardShape = RoundedCornerShape(8.dp)
                    AsyncImage(
                        model = ASSETS + path,
                        contentDescription = null,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .offset(x = maxWidth * x, y = 22.dp)
                            .rotate(angle)
                            .size(width = maxWidth * 0.26f, height = 62.dp)
                            .shadow(4.dp, cardShape)
                            .clip(cardShape)
                            .background(PaperCard)
                            .border(3.dp, PaperEdge, cardShape),
                    )
                }
            }
            else -> GameCover(game, maxHeight, Modifier.align(Alignment.Center))
        }
    }
}

/** The sign of a small card: the three orbs of Invoker, a board of nine for tic-tac-toe */
@Composable
private fun GameSign(game: GameVO) {
    when (game) {
        is GameVO.Invoker -> Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            listOf("quas", "wex", "exort").forEach { orb ->
                AsyncImage(
                    model = "${ASSETS}images/abilities/invoker_$orb.webp",
                    contentDescription = null,
                    modifier = Modifier.size(34.dp).clip(CircleShape).border(2.dp, PaperEdge, CircleShape),
                )
            }
        }
        is GameVO.XO -> {
            val cells = listOf(XGreen, Blank, ORed, ORed, XGreen, Blank, Blank, ORed, XGreen)
            Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                cells.chunked(3).forEach { row ->
                    Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                        row.forEach { color -> Box(Modifier.size(width = 12.dp, height = 12.dp).clip(RoundedCornerShape(3.dp)).background(color)) }
                    }
                }
            }
        }
        else -> GameCover(game, 34.dp)
    }
}

private val XGreen = Color(0xFF3F7D45)
private val ORed = Color(0xFFA33F36)
private val Blank = Color(0xFFD8CDB8)

internal fun GameVO.rules(): Int = when (this) {
    is GameVO.Show -> R.string.games_rules_show
    is GameVO.Mid -> R.string.games_rules_mid
    is GameVO.Invoker -> R.string.games_rules_invoker
    is GameVO.XO -> R.string.games_rules_xo
}

/** How a game is played: alone or against the bot, and over the network with how many */
@Composable
private fun GameVO.chips(): List<String> = when (this) {
    is GameVO.Mid, is GameVO.XO -> listOf(stringResource(R.string.games_chip_bot), stringResource(R.string.games_chip_duel))
    else -> listOf(stringResource(R.string.games_chip_solo), stringResource(R.string.games_chip_room, MAX_PLAYERS))
}

@Preview(backgroundColor = 0xFF202225, showBackground = true, widthDp = 390, heightDp = 900)
@Composable
private fun GameCardsPreview() {
    val context = androidx.compose.ui.platform.LocalContext.current
    val res = object : n7.ad2.Resources {
        override fun getString(resourceID: Int): String = context.getString(resourceID)
        override fun getString(resourceID: Int, formatArgs: Any): String = context.getString(resourceID, formatArgs)
        override fun getAssets(path: String) = throw UnsupportedOperationException()
        override fun getExternalFilesDir(path: String) = null
        override fun getFilesDir() = null
        override fun getConfiguration() = null
    }
    AppTheme(isDarkTheme = true) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(18.dp)) {
            LevelChip(Progress((1..6).sumOf { Progress.needed(it) } + 340))
            BigGameCard(GameVO.Mid(res), record = 38, tilt = 0f, onClick = {})
            BigGameCard(GameVO.Show(res), record = 2450, tilt = -0.4f, onClick = {})
            Row(Modifier.height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                SmallGameCard(GameVO.Invoker(res), record = null, tilt = 0f, modifier = Modifier.weight(1f).fillMaxHeight(), onClick = {})
                SmallGameCard(GameVO.XO(res), record = 3, tilt = 0.6f, modifier = Modifier.weight(1f).fillMaxHeight(), onClick = {})
            }
        }
    }
}
