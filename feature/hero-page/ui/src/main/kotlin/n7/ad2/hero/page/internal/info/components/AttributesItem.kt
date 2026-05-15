package n7.ad2.hero.page.internal.info.components

import androidx.annotation.DrawableRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

@Composable
internal fun AttributesItem(item: VOHeroInfo.Attributes, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        AsyncImage(
            model = item.urlHeroImage,
            contentDescription = null,
            modifier = Modifier.size(72.dp),
        )
        StatColumn(R.drawable.strength, item.heroStatistics.strength.toString())
        StatColumn(R.drawable.agility, item.heroStatistics.agility.toString())
        StatColumn(R.drawable.intelligence, item.heroStatistics.intelligence.toString())
    }
}

@Composable
private fun StatColumn(@DrawableRes icon: Int, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Image(painter = painterResource(icon), contentDescription = null, modifier = Modifier.size(24.dp))
        Text(text = value, style = AppTheme.style.body, color = AppTheme.color.textColor)
    }
}
