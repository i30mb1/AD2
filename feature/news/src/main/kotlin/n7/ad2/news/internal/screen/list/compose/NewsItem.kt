@file:OptIn(ExperimentalMaterialApi::class)

package n7.ad2.news.internal.screen.list.compose

import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.spring
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.compose.SubcomposeAsyncImageScope
import coil.compose.rememberAsyncImagePainter
import n7.ad2.news.internal.domain.model.Image
import n7.ad2.news.internal.domain.model.NewsVO
import n7.ad2.ui.R
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.bounceClick


@Preview
@Composable
private fun NewsItemPreview() {
    AppTheme {
        NewsItem(item = NewsVO(0, "Title", Image("")), onNewsClicked = {})
    }
}

@Composable
internal fun NewsItem(
    item: NewsVO,
    onNewsClicked: (newsID: Int) -> Unit,
) {
    Surface(
        modifier = Modifier
            .bounceClick()
            .clip(RoundedCornerShape(6.dp)),
        color = AppTheme.color.surface,
        elevation = 4.dp,
        onClick = { onNewsClicked(item.id) },
    ) {
        SubcomposeAsyncImage(
            modifier = Modifier.fillMaxSize(),
            model = item.image.origin,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
//                loading = painterResource(R.drawable.widht_placeholder),
//                error = painterResource(R.drawable.widht_placeholder),
            success = {
                SubcomposeAsyncImageContent()
                Text(
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color(0x4D000000))
                        .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp),
                    textAlign = TextAlign.Center,
                    text = item.title,
                    style = AppTheme.style.H5,
                    color = Color.White,
                )
                var isLiked by remember { mutableStateOf(false) }
                FavoriteIcon(
                    isLiked = isLiked,
                    onLikeClicked = { isLiked = !isLiked },
                    modifier = Modifier.align(Alignment.TopEnd)
                )
                Box(
                    modifier = Modifier
                        .width(1.dp)
                        .fillMaxHeight()
                        .background(AppTheme.color.primary)
                )
            }
        )
    }
}

@Composable
private fun FavoriteIcon(
    isLiked: Boolean,
    onLikeClicked: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var filledLikeScale by remember { mutableStateOf(0f) }
    var showEmptyLike by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .clickable {
                onLikeClicked()
            }
            .padding(8.dp),
    ) {
        if (isLiked) Icon(
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
        ) else Icon(
            imageVector = Icons.Default.FavoriteBorder,
            contentDescription = null,
        )
        if (filledLikeScale > 0f) Icon(
            modifier = Modifier.scale(filledLikeScale),
            imageVector = Icons.Default.Favorite,
            contentDescription = null,
        )
    }

    LaunchedEffect(isLiked) {
        if (isLiked) {
            animate(
                0f,
                1f,
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioHighBouncy,
                ),
            ) { value, _ ->
                filledLikeScale = value
                if (value >= 1f) {
                    showEmptyLike = false
                }
            }
        }
    }
}