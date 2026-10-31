package n7.ad2.drawer.internal.compose

import android.icu.text.CompactDecimalFormat
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.vo.VOMenu
import n7.ad2.drawer.internal.domain.vo.VOMenuInfo
import n7.ad2.drawer.internal.domain.vo.VOPerformance
import n7.ad2.feature.drawer.R

internal val icelandFont = FontFamily(Font(R.font.iceland_normal))
internal val dimColor = Color(0xFF8E9297)
private val cardShape = RoundedCornerShape(10.dp)
private val iconShape = RoundedCornerShape(8.dp)

/**
 * The menu under the screen: the logo, a card per section, the device tile and the version next to the settings button that stays in the layout.
 * The parts go in one column with flexible gaps, so on a short screen they come closer instead of overlapping.
 */
@Composable
internal fun DrawerMenu(
    menu: List<VOMenu>,
    performance: List<VOPerformance>,
    isJank: Boolean?,
    version: String,
    onMenuClicked: (VOMenu) -> Unit,
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(start = 12.dp, top = 24.dp, bottom = 72.dp),
        ) {
            Column(modifier = Modifier.padding(start = 4.dp)) {
                Text(text = "AD2", fontFamily = icelandFont, fontSize = 44.sp, lineHeight = 44.sp, letterSpacing = 1.sp, color = AppTheme.color.primary)
                Text(
                    text = stringResource(R.string.menu_tagline).uppercase(),
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Medium,
                    letterSpacing = 1.6.sp,
                    color = dimColor,
                )
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(modifier = Modifier.width(186.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                menu.forEach { item -> MenuCard(item, onClick = { onMenuClicked(item) }) }
            }
            Spacer(modifier = Modifier.weight(1f).heightIn(min = 12.dp))
            if (performance.isNotEmpty()) DeviceTile(performance, isJank, modifier = Modifier.width(186.dp))
        }
        Text(
            text = version,
            fontSize = 11.sp,
            color = dimColor,
            modifier = Modifier
                .align(Alignment.BottomStart)
                .navigationBarsPadding()
                .padding(start = 64.dp, bottom = 29.dp),
        )
    }
}

@Composable
private fun MenuCard(item: VOMenu, onClick: () -> Unit) {
    val accent = AppTheme.color.primary
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .alpha(if (item.isEnable) 1f else 0.5f)
            .clip(cardShape)
            .background(AppTheme.color.surface.copy(alpha = if (item.isSelected) 0.95f else 0.55f))
            .border(1.dp, if (item.isSelected) accent else Color.White.copy(alpha = 0.04f), cardShape)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Box(
            modifier = Modifier
                .size(34.dp)
                .clip(iconShape)
                .background(if (item.isSelected) accent.copy(alpha = 0.16f) else Color.White.copy(alpha = 0.05f)),
            contentAlignment = Alignment.Center,
        ) {
            val icon = item.type.icon()
            if (icon != null) {
                Icon(
                    painter = painterResource(icon),
                    contentDescription = null,
                    tint = if (item.isSelected) accent else dimColor,
                    modifier = Modifier.size(20.dp),
                )
            }
        }
        Column(modifier = Modifier.padding(start = 10.dp)) {
            Text(
                text = item.title,
                fontSize = 15.sp,
                letterSpacing = 0.sp,
                fontWeight = if (item.isSelected) FontWeight.Bold else FontWeight.Normal,
                color = if (item.isSelected) AppTheme.color.textColor else Color(0xFFDCDDDE),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
            val info = item.info
            Text(
                text = if (info != null) infoText(info) else stringResource(item.subtitle),
                fontSize = 11.sp,
                letterSpacing = 0.sp,
                color = if (info != null && info.isHighlighted) accent else dimColor,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
            )
        }
    }
}

@Composable
private fun infoText(info: VOMenuInfo): String = when (info) {
    is VOMenuInfo.Heroes -> pluralStringResource(R.plurals.menu_heroes_count, info.count, info.count)
    is VOMenuInfo.Items -> pluralStringResource(R.plurals.menu_items_count, info.count, info.count)
    is VOMenuInfo.News -> pluralStringResource(R.plurals.menu_news_unread, info.unread, info.unread)
    is VOMenuInfo.LiveMatches -> pluralStringResource(R.plurals.menu_live_matches, info.count, info.count)
    is VOMenuInfo.Viewers -> {
        val locale = LocalConfiguration.current.locales[0]
        stringResource(R.string.menu_viewers, CompactDecimalFormat.getInstance(locale, CompactDecimalFormat.CompactStyle.SHORT).format(info.count))
    }
}

/** Live and unread numbers are painted in the accent color, plain counts stay grey. */
private val VOMenuInfo.isHighlighted: Boolean
    get() = this is VOMenuInfo.LiveMatches || this is VOMenuInfo.Viewers || this is VOMenuInfo.News

private fun VOMenuType.icon(): Int? = when (this) {
    VOMenuType.HEROES -> R.drawable.ic_menu_heroes
    VOMenuType.ITEMS -> R.drawable.ic_menu_items
    VOMenuType.NEWS -> R.drawable.ic_menu_news
    VOMenuType.TOURNAMENTS -> R.drawable.ic_menu_tournaments
    VOMenuType.STREAMS -> R.drawable.ic_menu_streams
    VOMenuType.GAMES -> R.drawable.ic_menu_games
    VOMenuType.UNKNOWN -> null
}
