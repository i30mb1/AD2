package n7.ad2.tournaments.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
import n7.ad2.tournaments.internal.data.MatchesParser
import n7.ad2.tournaments.internal.data.TournamentMatch

class TournamentsViewModel @AssistedInject constructor(private val dispatchers: DispatchersProvider) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): TournamentsViewModel
    }

    internal sealed interface State {
        data object Loading : State
        data class Content(val matches: List<TournamentMatch>) : State
        data class Error(val error: Throwable) : State
    }

    internal val state = MutableStateFlow<State>(State.Loading)

    init {
        load()
    }

    internal fun load() {
        flow {
            val matches = MatchesParser.parse(Dota2Ru.connect(Dota2Ru.MATCHES_URL).get())
            if (matches.isEmpty()) error("no matches on ${Dota2Ru.MATCHES_URL}")
            emit(matches)
        }
            .flowOn(dispatchers.IO)
            .onStart { state.value = State.Loading }
            .onEach { matches -> state.value = State.Content(matches) }
            .catch { error -> state.value = State.Error(error) }
            .launchIn(viewModelScope)
    }
}
