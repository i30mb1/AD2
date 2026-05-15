package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyTalent

@Composable
internal fun TalentItem(item: VOBodyTalent) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Text(
            text = item.talentLeft,
            style = AppTheme.style.body,
            color = AppTheme.color.textColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.End,
        )
        Text(
            text = item.talentLvl,
            style = AppTheme.style.H5,
            color = AppTheme.color.primary,
            modifier = Modifier.padding(horizontal = 8.dp),
        )
        Text(
            text = item.talentRight,
            style = AppTheme.style.body,
            color = AppTheme.color.textColor,
            modifier = Modifier.weight(1f),
            textAlign = TextAlign.Start,
        )
    }
}
