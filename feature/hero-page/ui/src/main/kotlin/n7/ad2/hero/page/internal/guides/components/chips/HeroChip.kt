package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowItem

@Composable
internal fun HeroChip(item: VOHeroFlowItem, good: Boolean) {
    Column(
        modifier = Modifier.padding(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = item.urlHeroImage,
            contentDescription = item.heroName,
            modifier = Modifier
                .size(48.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = item.heroWinrate,
            style = AppTheme.style.body,
            color = if (good) Color(0xFF4CAF50) else Color(0xFFF44336),
        )
    }
}
