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
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import n7.ad2.hero.page.internal.pager.LocalResponsesLanguage
import n7.ad2.hero.page.internal.pager.foldsHeroBanner
import n7.ad2.hero.page.internal.common.SectionChip
import n7.ad2.hero.page.internal.common.keepsSidewaysDrag
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
import n7.ad2.core.ui.compose.AppTheme
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.feature.hero.page.ui.R
import n7.ad2.hero.page.internal.responses.components.ResponseActionsSheet
import n7.ad2.hero.page.internal.responses.components.ResponseBodyItem
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.hero.page.internal.responses.domain.vo.key

private const val PROGRESS_TICK_MS = 100L

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
    val listState = rememberLazyListState()
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
    // the language switch is in the banner above the tabs
    val language = LocalResponsesLanguage.current
    DisposableEffect(language, locale) {
        language?.current = locale
        language?.onPick = viewModel::loadResponses
        onDispose { }
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
                Categories(groups = groups, listState = listState)
                ResponsesList(
                    groups = groups,
                    listState = listState,
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
    listState: LazyListState,
    playingKey: String?,
    progress: Float,
    onPlay: (VOResponse.Body) -> Unit,
    onMore: (VOResponse.Body) -> Unit,
) {
    val bottomInset = WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()
    LazyColumn(
        state = listState,
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

/** A chip jumps to its category, the highlighted one follows the scroll so it always names what is on screen. */
@Composable
private fun Categories(groups: List<ResponseGroup>, listState: LazyListState) {
    val scope = rememberCoroutineScope()
    val sizes = remember(groups) { groups.map { it.items.size } }
    val current by remember(sizes) { derivedStateOf { groupAt(sizes, listState.firstVisibleItemIndex) } }
    val chipsState = rememberLazyListState()
    LaunchedEffect(current) { if (groups.isNotEmpty()) chipsState.animateScrollToItem(current) }
    LazyRow(
        state = chipsState,
        modifier = Modifier
            .fillMaxWidth()
            .keepsSidewaysDrag()
            .padding(vertical = 10.dp),
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(6.dp),
    ) {
        itemsIndexed(groups, key = { index, group -> "$index-${group.title}" }) { index, group ->
            SectionChip(text = group.title, count = group.items.size, isSelected = index == current) {
                scope.launch { listState.animateScrollToItem(headerIndex(sizes, index)) }
            }
        }
    }
}
