package n7.ad2.news.internal.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import n7.ad2.database_guides.api.dao.NewsDao

class ArticleViewModel @AssistedInject constructor(
    @Assisted private val newsID: Int,
    private val newsDao: NewsDao,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(newsID: Int): ArticleViewModel
    }

    val state = MutableStateFlow(State.init())

    init {
        viewModelScope.launch {
            val news = newsDao.getSingleNews(newsID)
            state.update {
                State(false, news.href)
            }
        }
    }

    data class State(
        val isLoading: Boolean,
        val href: String,
    ) {
        companion object {
            fun init() = State(true, "")
        }
    }

}