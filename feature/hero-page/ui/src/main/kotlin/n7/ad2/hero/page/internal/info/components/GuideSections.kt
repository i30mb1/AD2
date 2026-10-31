package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlin.math.roundToInt
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideHero
import n7.ad2.hero.page.internal.guides.domain.vo.VOGuideItem

private const val MATCHUP_HEROES = 5
private val strongColor = Color(0xFF8FD17F)
private val weakColor = Color(0xFFFF8A80)

/** The heroes it wins against most and least often, from OpenDota. A tap opens the full lists with the wiki advice. */
@Composable
internal fun MatchupsCards(easyToWin: List<VOGuideHero>, hardToWin: List<VOGuideHero>, onOpen: () -> Unit) {
    Row(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .clickable(role = Role.Button, onClick = onOpen),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        MatchupCard(stringResource(R.string.easy_to_win_heroes), easyToWin.take(MATCHUP_HEROES), strongColor, Modifier.weight(1f))
        MatchupCard(stringResource(R.string.hard_to_win_heroes), hardToWin.take(MATCHUP_HEROES), weakColor, Modifier.weight(1f))
    }
}

@Composable
private fun MatchupCard(title: String, heroes: List<VOGuideHero>, color: Color, modifier: Modifier) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Caption(title, color = color)
        Row(
            modifier = Modifier.fillMaxWidth(),
            // a full row spreads over the card, a short one keeps its heroes together
            horizontalArrangement = if (heroes.size >= MATCHUP_HEROES) Arrangement.SpaceBetween else Arrangement.spacedBy(10.dp),
        ) {
            heroes.forEach { hero ->
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    AsyncImage(
                        model = hero.imageUrl,
                        contentDescription = hero.heroName,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(InfoColors.chip),
                    )
                    VSpace(4.dp)
                    Text("${hero.winrate.roundToInt()}%", fontSize = 10.sp, color = AppTheme.color.textSecondaryColor)
                }
            }
        }
    }
}

/** What the hero is built around by the middle of the game, from OpenDota. */
@Composable
internal fun KeyItemsCard(items: List<VOGuideItem>) {
    Column(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(AppTheme.color.surface)
            .padding(12.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        Caption(stringResource(R.string.info_items_mid))
        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
            items.forEach { item ->
                AsyncImage(
                    model = item.imageUrl,
                    contentDescription = item.itemName,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .size(width = 44.dp, height = 33.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(InfoColors.chip),
                )
            }
        }
    }
}
