package n7.ad2.hero.page.internal.responses.components

import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponseImage

@Composable
internal fun ResponseImage(image: VOResponseImage) {
    AsyncImage(
        model = image.imageUrl,
        contentDescription = image.heroName,
        modifier = Modifier
            .size(24.dp)
            .clip(CircleShape),
    )
}
