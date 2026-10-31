package n7.ad2.hero.page.internal.pager

import android.os.Bundle
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.launch
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.HeroGuideFragment
import n7.ad2.hero.page.internal.info.HeroInfoFragment
import n7.ad2.hero.page.internal.responses.ResponsesFragment

@Composable
internal fun HeroPageScreen(heroName: String, artUrl: String, viewModel: HeroPageViewModel) {
    val description by viewModel.description.collectAsStateWithLifecycle()
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(stringResource(R.string.hero_info), stringResource(R.string.hero_sound), stringResource(R.string.hero_guide))
    val args: Bundle = bundleOf("HERO_NAME" to heroName)
    val rangePx = with(LocalDensity.current) { (BANNER_HEIGHT - TOOLBAR_HEIGHT).toPx() }
    val collapse = remember(rangePx) { BannerCollapse(rangePx) }

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        HeroBanner(heroName = description?.name?.ifEmpty { null } ?: heroName, artUrl = artUrl, description = description, collapse = collapse.fraction)
        HeroPageTabs(
            titles = tabTitles,
            selected = pagerState.currentPage,
            onSelect = { index -> scope.launch { pagerState.animateScrollToPage(index) } },
        )
        CompositionLocalProvider(LocalBannerCollapse provides collapse) {
            HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                when (page) {
                    0 -> AndroidFragment<HeroInfoFragment>(arguments = args)
                    1 -> AndroidFragment<ResponsesFragment>(arguments = args)
                    2 -> AndroidFragment<HeroGuideFragment>(arguments = args)
                }
            }
        }
    }
}
