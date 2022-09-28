package ad2.n7.news.internal

import ad2.n7.news.internal.di.DaggerNewsComponent
import ad2.n7.news.internal.domain.model.NewsVO
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
import coil.compose.AsyncImage
import kotlinx.coroutines.launch
import n7.ad2.android.DrawerPercentListener
import n7.ad2.android.findDependencies
import n7.ad2.ktx.viewModel
import n7.ad2.logger.Logger
import n7.ad2.ui.ComposeView
import n7.ad2.ui.compose.AppTheme
import javax.inject.Inject

internal class NewsFragment : Fragment() {

    companion object {
        fun getInstance() = NewsFragment()
    }

    @Inject lateinit var newsViewModelFactory: NewsViewModel.Factory
    @Inject lateinit var logger: Logger

    private val viewModel: NewsViewModel by viewModel { newsViewModelFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView { NewsScreen(viewModel, parentFragment as DrawerPercentListener) }
    }

}

@Composable
internal fun NewsScreen(
    viewModel: NewsViewModel,
    drawerPercentListener: DrawerPercentListener,
    modifier: Modifier = Modifier,
) {
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()

    val insetsTop = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()
    var drawerPercent by remember { mutableStateOf(0f) }
    val showScrollToTopButton by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } }
    val news: LazyPagingItems<NewsVO> = viewModel.news.collectAsLazyPagingItems()
    Box {
        LazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 4.dp + insetsTop * drawerPercent, start = 4.dp, end = 4.dp),
        ) {
            items(news) { item -> NewsItem(item) }
        }
        if (showScrollToTopButton) Box(
            modifier = modifier
                .clickable { scope.launch { state.animateScrollToItem(0) } }
                .padding(bottom = 60.dp)
                .clip(RoundedCornerShape(topStart = 6.dp, bottomStart = 6.dp))
                .background(color = AppTheme.color.primary)
                .padding(start = 8.dp, end = 8.dp, top = 2.dp, bottom = 2.dp)
                .align(Alignment.BottomEnd),
        ) {
            Icon(
                modifier = modifier
                    .rotate(180f)
                    .align(Alignment.Center),
                imageVector = Icons.Default.ArrowDropDown,
                contentDescription = "scroll to top",
            )
        }
    }
    DisposableEffect(key1 = Unit) {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    }
}

@Composable
internal fun NewsItem(item: NewsVO? = null) {
    Surface(
        modifier = Modifier
            .height(170.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(6.dp)),
        color = AppTheme.color.surface,
        elevation = 4.dp,
    ) {
        AsyncImage(model = item?.image?.origin, contentDescription = item?.title, contentScale = ContentScale.Crop)
        Text(
            modifier = Modifier
                .background(Color(0x4D000000))
                .fillMaxHeight()
                .padding(start = 16.dp, end = 16.dp, top = 32.dp, bottom = 32.dp),
            textAlign = TextAlign.Center,
            text = item?.title ?: "Loading...",
            style = AppTheme.style.H5,
            color = AppTheme.color.textColor,
        )
    }
}