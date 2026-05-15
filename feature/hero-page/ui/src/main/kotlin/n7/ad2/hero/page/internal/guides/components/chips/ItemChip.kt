package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowHeroItem

@Composable
internal fun ItemChip(item: VOHeroFlowHeroItem) {
    Row(
        modifier = Modifier.padding(2.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(2.dp),
    ) {
        AsyncImage(
            model = item.urlHeroItem,
            contentDescription = item.itemName,
            modifier = Modifier
                .size(32.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
        item.itemTiming?.let {
            Image(painter = painterResource(R.drawable.ic_arrow_right), contentDescription = null)
            Text(text = it, style = AppTheme.style.body, color = AppTheme.color.textColor)
        }
    }
}
