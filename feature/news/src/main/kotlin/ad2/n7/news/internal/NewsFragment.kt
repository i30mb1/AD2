package ad2.n7.news.internal

import ad2.n7.news.internal.di.DaggerNewsComponent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import kotlinx.coroutines.launch
import n7.ad2.android.findDependencies
import n7.ad2.ktx.viewModel
import n7.ad2.ui.ComposeView
import javax.inject.Inject

internal class NewsFragment : Fragment() {

    companion object {
        fun getInstance(): NewsFragment = NewsFragment()
    }

    @Inject lateinit var newsViewModelFactory: NewsViewModel.Factory

    private val viewModel: NewsViewModel by viewModel { newsViewModelFactory.create() }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        DaggerNewsComponent.factory().create(findDependencies()).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View = ComposeView { NewsScreen(viewModel = viewModel) }

}

@Composable
internal fun NewsScreen(
    modifier: Modifier = Modifier,
    viewModel: NewsViewModel,
) {
    val state: State<NewsViewModel.State> = viewModel.state.collectAsState()
    when (val data = state.value) {
        is NewsViewModel.State.Data -> NewsData(modifier, data)
        is NewsViewModel.State.Error -> Unit
        NewsViewModel.State.Loading -> Unit
    }

}

@Composable
internal fun NewsData(
    modifier: Modifier = Modifier,
    data: NewsViewModel.State.Data,
) {
    val scope = rememberCoroutineScope()
    val state = rememberLazyListState()
    val showScrollToTopButton by remember {
        derivedStateOf { state.firstVisibleItemIndex > 0 } // recomposition only triggered when the state of calculation changed
    }
    LazyColumn(
        state = state,
        verticalArrangement = Arrangement.spacedBy(4.dp),
        contentPadding = PaddingValues(
            start = 12.dp,
            end = 12.dp,
        )
    ) {
        items(data.list) { item ->
            Text(
                text = item.title,
            )
        }
        item {
            Button(
                onClick = { scope.launch { state.animateScrollToItem(0) } }
            ) { }
        }
    }
}