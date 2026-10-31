package n7.ad2.hero.page.internal.responses

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import n7.ad2.hero.page.internal.pager.foldsHeroBanner
import n7.ad2.hero.page.internal.common.SectionChip
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.ModalBottomSheetLayout
import androidx.compose.material.ModalBottomSheetValue
import androidx.compose.material.Text
import androidx.compose.material.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import n7.ad2.AppLocale
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.responses.components.ResponseActionsSheet
import n7.ad2.hero.page.internal.responses.components.ResponseBodyItem
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.hero.page.internal.responses.domain.vo.key

private const val PROGRESS_TICK_MS = 100L

private class ResponseGroup(val title: String, val items: List<VOResponse.Body>)

@OptIn(ExperimentalMaterialApi::class)
@Composable
internal fun ResponsesScreen(
    viewModel: ResponsesViewModel,
    playingKey: String?,
    progress: () -> Float,
    downloading: Set<String>,
    settingTarget: ResponseRingtoneSetter.Target?,
    onPlay: (VOResponse.Body) -> Unit,
    onDownload: (VOResponse.Body) -> Unit,
    onSetAs: (VOResponse.Body, ResponseRingtoneSetter.Target) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val locale by viewModel.locale.collectAsStateWithLifecycle()
    val scope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden, skipHalfExpanded = true)
    var selectedCategory by rememberSaveable { mutableStateOf<String?>(null) }
    // the sheet keeps the row key, so after a download the refreshed item is shown, not the stale one
    var sheetKey by rememberSaveable { mutableStateOf<String?>(null) }
    var playedPart by remember { mutableFloatStateOf(0f) }

    // back closes the sheet first, the page is left only with the next press
    BackHandler(enabled = sheetState.isVisible) { scope.launch { sheetState.hide() } }

    LaunchedEffect(playingKey) {
        playedPart = 0f
        if (playingKey == null) return@LaunchedEffect
        while (isActive) {
            playedPart = progress()
            delay(PROGRESS_TICK_MS)
        }
    }

    val groups = when (val s = state) {
        is ResponsesViewModel.State.Data -> s.list.toGroups()
        else -> emptyList()
    }
    val sheetGroup = groups.firstOrNull { group -> group.items.any { it.key == sheetKey } }
    val sheetItem = sheetGroup?.items?.firstOrNull { it.key == sheetKey }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        sheetBackgroundColor = AppTheme.color.surface,
        scrimColor = Color.Black.copy(alpha = 0.6f),
        sheetContent = {
            if (sheetItem != null && sheetGroup != null) {
                ResponseActionsSheet(
                    item = sheetItem,
                    category = sheetGroup.title,
                    isPlaying = playingKey == sheetItem.key,
                    progress = playedPart,
                    isDownloading = sheetItem.titleForFile in downloading,
                    settingTarget = settingTarget,
                    onPlayClick = { onPlay(sheetItem) },
                    onDownloadClick = { onDownload(sheetItem) },
                    onSetAsClick = { target -> onSetAs(sheetItem, target) },
                )
            } else {
                // the sheet layout needs some content even while hidden
                Box(Modifier.height(1.dp))
            }
        },
    ) {
        when (val s = state) {
            ResponsesViewModel.State.Loading -> Unit
            is ResponsesViewModel.State.Error -> ErrorScreen(error = s.error)
            is ResponsesViewModel.State.Data -> Column(Modifier.fillMaxSize()) {
                Filters(
                    groups = groups,
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it },
                    locale = locale,
                    onLocaleChange = viewModel::loadResponses,
                )
                ResponsesList(
                    groups = groups.filter { selectedCategory == null || it.title == selectedCategory },
                    playingKey = playingKey,
                    progress = playedPart,
                    onPlay = onPlay,
                    onMore = { item ->
                        sheetKey = item.key
                        scope.launch { sheetState.show() }
                    },
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ResponsesList(
    groups: List<ResponseGroup>,
    playingKey: String?,
    progress: Float,
    onPlay: (VOResponse.Body) -> Unit,
    onMore: (VOResponse.Body) -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .foldsHeroBanner(),
        contentPadding = PaddingValues(bottom = bottomInset + 16.dp),
    ) {
        groups.forEachIndexed { groupIndex, group ->
            // the category stays on top while its lines scroll, so it is clear what is playing
            stickyHeader(key = "t-$groupIndex-${group.title}") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(AppTheme.color.surface)
                        .padding(horizontal = 16.dp, vertical = 6.dp),
                ) {
                    Text(
                        text = group.title.uppercase(),
                        style = AppTheme.style.body,
                        fontWeight = FontWeight.Bold,
                        color = AppTheme.color.textSecondaryColor,
                        modifier = Modifier.weight(1f),
                    )
                    Text(text = group.items.size.toString(), style = AppTheme.style.body, color = AppTheme.color.textSecondaryColor)
                }
            }
            group.items.forEachIndexed { index, item ->
                item(key = "b-$groupIndex-$index-${item.titleForFile}") {
                    ResponseBodyItem(
                        item = item,
                        isPlaying = playingKey == item.key,
                        progress = progress,
                        onPlayClick = { onPlay(item) },
                        onMoreClick = { onMore(item) },
                    )
                }
            }
        }
    }
}

@Composable
private fun Filters(
    groups: List<ResponseGroup>,
    selected: String?,
    onSelect: (String?) -> Unit,
    locale: AppLocale,
    onLocaleChange: (AppLocale) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        LazyRow(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(start = 16.dp, end = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp),
        ) {
            item(key = "all") {
                SectionChip(text = stringResource(R.string.responses_all), count = null, isSelected = selected == null) { onSelect(null) }
            }
            itemsIndexed(groups, key = { index, group -> "$index-${group.title}" }) { _, group ->
                SectionChip(text = group.title, count = group.items.size, isSelected = selected == group.title) { onSelect(group.title) }
            }
        }
        LocaleToggle(locale = locale, onLocaleChange = onLocaleChange, modifier = Modifier.padding(end = 12.dp))
    }
}

@Composable
private fun LocaleToggle(locale: AppLocale, onLocaleChange: (AppLocale) -> Unit, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .clip(CircleShape)
            .background(AppTheme.color.surface)
            .padding(2.dp),
    ) {
        listOf(AppLocale.Russian, AppLocale.English).forEach { option ->
            val isSelected = option == locale
            Box(
                modifier = Modifier
                    .height(28.dp)
                    .clip(CircleShape)
                    .background(if (isSelected) AppTheme.color.primary else Color.Transparent)
                    .clickable(role = Role.RadioButton) { if (!isSelected) onLocaleChange(option) }
                    .padding(horizontal = 10.dp),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    text = option.value.uppercase(),
                    style = AppTheme.style.body,
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else AppTheme.color.textSecondaryColor,
                )
            }
        }
    }
}

private fun List<VOResponse>.toGroups(): List<ResponseGroup> {
    val result = mutableListOf<ResponseGroup>()
    var title: String? = null
    val items = mutableListOf<VOResponse.Body>()
    fun flush() {
        val current = title ?: return
        if (items.isNotEmpty()) result.add(ResponseGroup(current, items.toList()))
        items.clear()
    }
    forEach { response ->
        when (response) {
            is VOResponse.Title -> {
                flush()
                title = response.data.title
            }
            is VOResponse.Body -> items.add(response)
        }
    }
    flush()
    return result
}
