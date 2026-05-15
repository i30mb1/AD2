package n7.ad2.hero.page.internal.guides.components.chips

import androidx.compose.foundation.layout.Box
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
import n7.ad2.hero.page.internal.guides.domain.vo.VOHeroFlowSpell

@Composable
internal fun SpellChip(item: VOHeroFlowSpell) {
    Box(modifier = Modifier.padding(2.dp)) {
        AsyncImage(
            model = item.urlImageSkill,
            contentDescription = item.skillName,
            modifier = Modifier
                .size(40.dp)
                .clip(RoundedCornerShape(4.dp)),
        )
        Text(
            text = item.skillOrder,
            color = Color.White,
            style = AppTheme.style.info,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(2.dp),
        )
    }
}
