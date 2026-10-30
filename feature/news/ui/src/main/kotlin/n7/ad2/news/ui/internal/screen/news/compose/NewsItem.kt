package n7.ad2.news.ui.internal.screen.news.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.news.ui.internal.screen.news.model.NewsVO

private const val IMAGE_RATIO = 16f / 9f
internal val pressColor = Color(0xFF64B5F6)
private val pressBadgeColor = Color(0xFF3A5A80)

/** The newest post: big picture, the title under it so long titles stay readable. */
@Composable
internal fun FeaturedNewsItem(item: NewsVO, onNewsClicked: (newsID: Int) -> Unit, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .clickable { onNewsClicked(item.id) },
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(IMAGE_RATIO)
                .background(AppTheme.color.background),
        )
        Column(
            modifier = Modifier.padding(start = 12.dp, end = 12.dp, top = 12.dp, bottom = 14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                SourceBadge(item)
                if (item.date.isNotEmpty()) {
                    Text(
                        text = item.date.uppercase(),
                        style = AppTheme.style.body,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.textSecondaryColor,
                        modifier = Modifier.padding(start = 8.dp),
                    )
                }
            }
            Text(text = item.title, fontSize = 18.sp, lineHeight = 23.sp, fontWeight = FontWeight.Bold, color = AppTheme.color.textColor)
        }
    }
}

/** Every other post: small picture on the left, two lines of title and where it came from. */
@Composable
internal fun NewsItem(item: NewsVO, onNewsClicked: (newsID: Int) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(AppTheme.shape.medium)
            .background(AppTheme.color.surface)
            .clickable { onNewsClicked(item.id) }
            .padding(8.dp),
    ) {
        AsyncImage(
            model = item.imageUrl,
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .width(112.dp)
                .aspectRatio(IMAGE_RATIO)
                .clip(AppTheme.shape.small)
                .background(AppTheme.color.background),
        )
        Column(
            modifier = Modifier
                .weight(1f)
                .padding(start = 12.dp)
                .height(63.dp),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(
                text = item.title,
                style = AppTheme.style.H5,
                fontWeight = FontWeight.Medium,
                color = AppTheme.color.textColor,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    Modifier
                        .size(6.dp)
                        .clip(CircleShape)
                        .background(if (item.isOfficial) AppTheme.color.primary else pressColor),
                )
                Text(
                    text = if (item.date.isEmpty()) item.source else "${item.source} · ${item.date}",
                    style = AppTheme.style.body,
                    color = AppTheme.color.textSecondaryColor,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp),
                )
            }
        }
    }
}

@Composable
internal fun SourceBadge(item: NewsVO) {
    if (item.source.isEmpty()) return
    Text(
        text = item.source.uppercase(),
        style = AppTheme.style.body,
        fontWeight = FontWeight.Bold,
        color = Color.White,
        modifier = Modifier
            .clip(AppTheme.shape.small)
            .background(if (item.isOfficial) AppTheme.color.primary else pressBadgeColor)
            .padding(horizontal = 6.dp, vertical = 2.dp),
    )
}

@Preview
@Composable
private fun NewsItemPreview() {
    AppTheme {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            FeaturedNewsItem(NewsVO(0, "7.41f Gameplay Patch", "", "Valve", "15 сен", true), onNewsClicked = {})
            NewsItem(NewsVO(1, "Dota 2's Dark Carnival has begun", "", "PCGamesN", "26 июн", false), onNewsClicked = {})
        }
    }
}
