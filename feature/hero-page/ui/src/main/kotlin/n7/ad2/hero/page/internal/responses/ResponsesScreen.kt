package n7.ad2.hero.page.internal.responses

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import n7.ad2.core.ui.compose.view.ErrorScreen
import n7.ad2.hero.page.internal.info.components.HeaderItem
import n7.ad2.hero.page.internal.responses.components.ResponseBodyItem
import n7.ad2.hero.page.internal.responses.components.ResponseDownloadDialog
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun ResponsesScreen(
    viewModel: ResponsesViewModel,
    onPlay: (VOResponse.Body) -> Unit,
    onDownload: (VOResponse.Body) -> Unit,
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    var dialogTarget by remember { mutableStateOf<VOResponse.Body?>(null) }

    when (val s = state) {
        ResponsesViewModel.State.Loading -> Unit
        is ResponsesViewModel.State.Error -> ErrorScreen(error = s.error)
        is ResponsesViewModel.State.Data -> LazyColumn {
            s.list.forEachIndexed { idx, r ->
                when (r) {
                    is VOResponse.Title -> stickyHeader(key = "t-$idx") { HeaderItem(r.data) }
                    is VOResponse.Body -> item(key = "b-$idx-${r.title}") {
                        ResponseBodyItem(
                            item = r,
                            onClick = { onPlay(r) },
                            onLongClick = { if (!r.isSavedInMemory) dialogTarget = r },
                        )
                    }
                }
            }
        }
    }

    dialogTarget?.let { body ->
        ResponseDownloadDialog(
            onConfirm = { onDownload(body); dialogTarget = null },
            onDismiss = { dialogTarget = null },
        )
    }
}
