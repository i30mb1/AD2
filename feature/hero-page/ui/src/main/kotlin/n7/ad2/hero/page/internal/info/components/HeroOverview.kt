package n7.ad2.hero.page.internal.info.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.heroes.domain.model.HeroAttribute

@Composable
internal fun AttributeIcon(attribute: HeroAttribute, modifier: Modifier = Modifier) {
    when (attribute) {
        HeroAttribute.STRENGTH -> Image(painterResource(R.drawable.strength), null, modifier)
        HeroAttribute.AGILITY -> Image(painterResource(R.drawable.agility), null, modifier)
        HeroAttribute.INTELLIGENCE -> Image(painterResource(R.drawable.intelligence), null, modifier)
        // there is no universal icon in the app, the three attributes together mean the same in game
        HeroAttribute.UNIVERSAL -> Box(modifier) {
            Image(painterResource(R.drawable.strength), null, Modifier.fillMaxSize(0.62f).align(Alignment.TopCenter))
            Image(painterResource(R.drawable.agility), null, Modifier.fillMaxSize(0.62f).align(Alignment.BottomStart))
            Image(painterResource(R.drawable.intelligence), null, Modifier.fillMaxSize(0.62f).align(Alignment.BottomEnd))
        }
    }
}
