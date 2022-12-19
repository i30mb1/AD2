package n7.ad2.news.internal.screen.article

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import n7.ad2.news.internal.domain.usecase.LoadContentUseCase

internal class ArticleViewModel @AssistedInject constructor(
    @Assisted private val newsID: Int,
    loadContentUseCase: LoadContentUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(newsID: Int): ArticleViewModel
    }

    val state = MutableStateFlow(State.init())

    init {
        loadContentUseCase(newsID)
            .onEach { body ->
                state.update { oldState ->
                    oldState.copy(isLoading = false, body = body)
                }
            }
            .catch {
                state.update { oldState ->
                    oldState.copy(isLoading = false, isError = true)
                }
            }
            .launchIn(viewModelScope)
    }

    data class State(
        val isLoading: Boolean,
        val isError: Boolean,
        val body: String,
    ) {
        companion object {
            fun init() = State(true, false, "")
        }
    }

}