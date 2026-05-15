package n7.ad2.hero.page.internal.info

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.core.ui.compose.view.LoadingAnimation
import n7.ad2.hero.page.internal.info.components.AttributesItem
import n7.ad2.hero.page.internal.info.components.BodyItem
import n7.ad2.hero.page.internal.info.components.BodyLineItem
import n7.ad2.hero.page.internal.info.components.BodySimpleItem
import n7.ad2.hero.page.internal.info.components.BodyWithImageItem
import n7.ad2.hero.page.internal.info.components.HeaderItem
import n7.ad2.hero.page.internal.info.components.HeaderSoundItem
import n7.ad2.hero.page.internal.info.components.SpellsRow
import n7.ad2.hero.page.internal.info.components.TalentItem
import n7.ad2.hero.page.internal.info.domain.usecase.GetVOHeroDescriptionUseCase
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyLine
import n7.ad2.hero.page.internal.info.domain.vo.VOBodySimple
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyTalent
import n7.ad2.hero.page.internal.info.domain.vo.VOBodyWithImage
import n7.ad2.hero.page.internal.info.domain.vo.VOHeroInfo

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HeroInfoScreen(
    viewModel: HeroInfoViewModel,
    onPlaySound: (String) -> Unit = {},
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    when (val s = state) {
        HeroInfoViewModel.State.Loading -> Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center,
        ) { LoadingAnimation() }

        is HeroInfoViewModel.State.Error -> ErrorScreen(error = s.error)

        is HeroInfoViewModel.State.Data -> {
            val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
            LazyColumn(
                contentPadding = PaddingValues(bottom = bottomInset),
                verticalArrangement = Arrangement.spacedBy(4.dp),
            ) {
                s.list.forEachIndexed { idx, vo ->
                    when (vo) {
                        is VOHeroInfo.Header -> stickyHeader(key = "h-$idx") { HeaderItem(vo.item) }
                        is VOHeroInfo.HeaderSound -> item(key = "hs-$idx") {
                            HeaderSoundItem(item = vo, onPlayClick = onPlaySound)
                        }
                        is VOHeroInfo.Attributes -> item(key = "a-$idx") {
                            AttributesItem(
                                item = vo,
                                onClick = { viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Main) },
                            )
                        }
                        is VOHeroInfo.Body -> item(key = "b-$idx") { BodyItem(vo.item) }
                        is VOHeroInfo.Spells -> item(key = "sp-$idx") {
                            SpellsRow(
                                item = vo,
                                onSpellClick = { viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(it.name)) },
                                onTalentClick = { viewModel.load(GetVOHeroDescriptionUseCase.HeroInfo.Spell(it.name)) },
                            )
                        }
                        is VOBodyLine -> item(key = "bl-$idx") { BodyLineItem(vo) }
                        is VOBodySimple -> item(key = "bs-$idx") { BodySimpleItem(vo) }
                        is VOBodyWithImage -> item(key = "bwi-$idx") { BodyWithImageItem(vo) }
                        is VOBodyTalent -> item(key = "bt-$idx") { TalentItem(vo) }
                    }
                }
            }
        }
    }
}
