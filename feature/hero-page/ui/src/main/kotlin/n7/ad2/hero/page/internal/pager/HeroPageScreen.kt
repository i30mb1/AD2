package n7.ad2.hero.page.internal.pager

import android.os.Bundle
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.Tab
import androidx.compose.material.TabRow
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.core.os.bundleOf
import androidx.fragment.compose.AndroidFragment
import kotlinx.coroutines.launch
import n7.ad2.AppLocale
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.guides.HeroGuideFragment
import n7.ad2.hero.page.internal.info.HeroInfoFragment
import n7.ad2.hero.page.internal.responses.ResponsesFragment

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun HeroPageScreen(
    heroName: String,
    appLocale: AppLocale,
    onLocaleChange: (AppLocale) -> Unit,
) {
    val pagerState = rememberPagerState(pageCount = { 3 })
    val scope = rememberCoroutineScope()
    val tabTitles = listOf(R.string.hero_info, R.string.hero_sound, R.string.hero_guide)
    val args: Bundle = bundleOf("HERO_NAME" to heroName)

    Column(
        Modifier
            .fillMaxSize()
            .statusBarsPadding(),
    ) {
        HeroPageToolbar(
            heroName = heroName,
            locale = appLocale,
            currentPage = pagerState.currentPage,
            onLocaleChange = onLocaleChange,
        )
        TabRow(selectedTabIndex = pagerState.currentPage) {
            tabTitles.forEachIndexed { idx, titleRes ->
                Tab(
                    selected = pagerState.currentPage == idx,
                    onClick = { scope.launch { pagerState.animateScrollToPage(idx) } },
                    text = { Text(stringResource(titleRes)) },
                )
            }
        }
        HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
            when (page) {
                0 -> AndroidFragment<HeroInfoFragment>(arguments = args)
                1 -> AndroidFragment<ResponsesFragment>(arguments = args)
                2 -> AndroidFragment<HeroGuideFragment>(arguments = args)
            }
        }
    }
}
