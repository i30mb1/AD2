package n7.ad2.news.ui.internal.screen.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.database_guides.api.AppDatabase
import n7.ad2.items.domain.model.ArticleBlock
import n7.ad2.items.domain.usecase.GetArticleUseCase
import n7.ad2.news.ui.internal.common.formatLongNewsDate

internal class ArticleViewModel @AssistedInject constructor(
    @Assisted private val newsID: Int,
    private val database: AppDatabase,
    private val getArticleUseCase: GetArticleUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(newsID: Int): ArticleViewModel
    }

    /** What the header shows, it is in the database already, so it appears before the body is loaded. */
    data class Header(val title: String, val imageUrl: String, val source: String, val date: String, val author: String, val isOfficial: Boolean, val url: String)

    data class State(val header: Header?, val blocks: List<ArticleBlock>, val isLoading: Boolean, val isError: Boolean)

    private val _state = MutableStateFlow(State(header = null, blocks = emptyList(), isLoading = true, isError = false))
    val state: StateFlow<State> = _state.asStateFlow()
    private var loadJob: Job? = null

    init {
        viewModelScope.launch {
            val news = database.newsDao.getSingleNews(newsID)
            val header = Header(news.title, news.urlImage, news.source, formatLongNewsDate(news.date), news.author, news.isOfficial, news.href)
            _state.update { it.copy(header = header) }
        }
        loadBody()
    }

    fun loadBody() {
        loadJob?.cancel()
        _state.update { it.copy(isLoading = true, isError = false) }
        loadJob = getArticleUseCase(newsID)
            .onEach { article -> _state.update { it.copy(blocks = article.blocks, isLoading = false) } }
            .catch { _state.update { it.copy(isLoading = false, isError = true) } }
            .launchIn(viewModelScope)
    }
}
