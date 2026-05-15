package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowStartingHeroItem

@Composable
internal fun StartingItemChip(item: VOHeroFlowStartingHeroItem) {
    AsyncImage(
        model = item.urlHeroItem,
        contentDescription = item.itemName,
        modifier = Modifier
            .padding(2.dp)
            .size(40.dp)
            .clip(RoundedCornerShape(4.dp)),
    )
}
