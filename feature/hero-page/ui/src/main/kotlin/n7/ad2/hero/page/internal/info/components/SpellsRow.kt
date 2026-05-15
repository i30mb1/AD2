package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo
import n7.ad2.hero.page.internal.info.domain.vo.VOSpell

@Composable
internal fun SpellsRow(
    item: VOHeroInfo.Spells,
    onSpellClick: (VOSpell.Simple) -> Unit,
    onTalentClick: (VOSpell.Talent) -> Unit,
) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        modifier = Modifier.padding(vertical = 4.dp),
    ) {
        items(items = item.spells) { spell ->
            when (spell) {
                is VOSpell.Simple -> SpellCard(spell, onClick = { onSpellClick(spell) })
                is VOSpell.Talent -> TalentCard(spell, onClick = { onTalentClick(spell) })
            }
        }
    }
}

@Composable
private fun SpellCard(spell: VOSpell.Simple, onClick: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    val border = if (spell.isSelected) 2.dp else 0.dp
    AsyncImage(
        model = spell.urlSpellImage,
        contentDescription = spell.name,
        placeholder = painterResource(n7.ad2.core.ui.R.drawable.square_placeholder),
        error = painterResource(n7.ad2.core.ui.R.drawable.square_error_placeholder),
        modifier = Modifier
            .size(48.dp)
            .clip(shape)
            .border(border, AppTheme.color.primary, shape)
            .clickable { onClick() },
    )
}

@Composable
private fun TalentCard(spell: VOSpell.Talent, onClick: () -> Unit) {
    val shape = RoundedCornerShape(6.dp)
    val border = if (spell.isSelected) 2.dp else 0.dp
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .size(48.dp)
            .clip(shape)
            .border(border, AppTheme.color.primary, shape)
            .clickable { onClick() },
        contentAlignment = Alignment.Center,
    ) {
        AsyncImage(
            model = null,
            contentDescription = spell.name,
            placeholder = painterResource(n7.ad2.core.ui.R.drawable.talent),
            error = painterResource(n7.ad2.core.ui.R.drawable.talent),
            modifier = Modifier.size(48.dp),
        )
        if (spell.isSelected) {
            Text(
                text = spell.name,
                style = AppTheme.style.info,
                color = Color.White,
                textAlign = TextAlign.Center,
            )
        }
    }
}
