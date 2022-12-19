@file:OptIn(ExperimentalMaterialApi::class)

package n7.ad2.news.internal.list.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.news.internal.domain.model.Image
import n7.ad2.news.internal.domain.model.NewsVO
import n7.ad2.ui.R
import n7.ad2.ui.compose.AppTheme
import n7.ad2.ui.compose.bounceClick


@Preview
@Composable
private fun NewsItemPreview() {
    NewsItem(item = NewsVO(0, "Title", Image("")), onNewsClicked = {})
}

@Composable
internal fun NewsItem(
    item: NewsVO,
    onNewsClicked: (newsID: Int) -> Unit,
) {
    Surface(
        modifier = Modifier
            .height(170.dp)
            .bounceClick()
            .clip(RoundedCornerShape(6.dp)),
        color = AppTheme.color.surface,
        elevation = 4.dp,
        onClick = { onNewsClicked(item.id) },
    ) {
        AsyncImage(
            model = item.image.origin,
            contentDescription = item.title,
            contentScale = ContentScale.Crop,
            placeholder = painterResource(id = R.drawable.widht_placeholder),
        )
        Text(
            modifier = Modifier
                .background(Color(0x4D000000))
                .fillMaxSize()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp),
            textAlign = TextAlign.Center,
            text = item.title,
            style = AppTheme.style.H5,
            color = Color.White,
        )
    }
}