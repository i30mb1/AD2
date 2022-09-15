package ad2.n7.news.internal

import ad2.n7.news.internal.di.DaggerNewsComponent
import ad2.n7.news.internal.domain.model.NewsVO
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
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
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.items
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
        fun getInstance(): NewsFragment = NewsFragment()
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
    val showScrollToTopButton by remember { derivedStateOf { state.firstVisibleItemIndex > 0 } /* recomposition only triggered when the state of calculation changed*/ }
    val news: LazyPagingItems<NewsVO> = viewModel.news.collectAsLazyPagingItems()
    Box {
        LazyColumn(
            modifier = modifier,
            state = state,
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(
                top = 4.dp + insetsTop * drawerPercent,
                start = 4.dp,
                end = 4.dp,
            )
        ) {
            items(news) { item -> NewsItem(item) }
        }
        if (showScrollToTopButton) {
            Button(
                modifier = modifier
                    .height(100.dp)
                    .width(200.dp)
                    .align(Alignment.BottomEnd)
                    .padding(
                        vertical = 8.dp,
                        horizontal = 4.dp
                    ),
                onClick = { scope.launch { state.animateScrollToItem(0) } }
            ) {
                Text(text = "Scroll to Top")
            }
        }
    }
    DisposableEffect(key1 = Unit, effect = {
        drawerPercentListener.setDrawerPercentListener { percent -> drawerPercent = percent }
        onDispose { drawerPercentListener.setDrawerPercentListener(null) }
    })
}

@Composable
internal fun NewsItem(item: NewsVO?) {
    Surface(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(6.dp))
            .heightIn(min = 32.dp)
            .fillMaxWidth()
            .background(AppTheme.color.surface),
        elevation = 8.dp,
    ) {
        Text(
            modifier = Modifier
                .padding(
                    start = 16.dp,
                    end = 16.dp,
                    top = 32.dp,
                    bottom = 32.dp,
                ),
            text = item?.title ?: "Loading...",
            style = AppTheme.style.H5,
            color = AppTheme.color.textColor,
        )
    }
}