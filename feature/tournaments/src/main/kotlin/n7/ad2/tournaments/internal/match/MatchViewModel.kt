package n7.ad2.tournaments.internal.match

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.tournaments.internal.data.Dota2Ru

internal class MatchViewModel @AssistedInject constructor(
    @Assisted private val url: String,
    private val dispatchers: DispatchersProvider,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(url: String): MatchViewModel
    }

    sealed interface State {
        data object Loading : State
        data class Content(val details: MatchDetails) : State
        data class Error(val error: Throwable) : State
    }

    val state = MutableStateFlow<State>(State.Loading)

    init {
        load()
    }

    fun load() {
        flow { emit(MatchDetailsParser.parse(url, Dota2Ru.html(url))) }
            .flowOn(dispatchers.IO)
            .onStart { state.value = State.Loading }
            .onEach { details -> state.value = State.Content(details) }
            .catch { error -> state.value = State.Error(error) }
            .launchIn(viewModelScope)
    }
}
