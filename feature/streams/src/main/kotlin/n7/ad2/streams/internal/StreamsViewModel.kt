package n7.ad2.streams.internal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.cachedIn
import androidx.paging.map
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.streams.internal.data.remote.Stream
import n7.ad2.streams.internal.data.remote.StreamPagingSource
import n7.ad2.streams.internal.data.remote.StreamRepository
import n7.ad2.streams.internal.domain.ConvertStreamToVOStreamUseCase
import n7.ad2.streams.internal.domain.vo.VOStream
import n7.ad2.streams.internal.model.StreamsLanguage

internal class StreamsViewModel @AssistedInject constructor(
    private val streamRepository: StreamRepository,
    private val dispatchers: DispatchersProvider,
    private val convertStreamToVOStreamUseCase: ConvertStreamToVOStreamUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): StreamsViewModel
    }

    private val _language = MutableStateFlow(StreamsLanguage.ALL)
    val language: StateFlow<StreamsLanguage> = _language.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val streams: Flow<PagingData<VOStream>> = _language
        .flatMapLatest { language ->
            Pager(
                config = PagingConfig(pageSize = 33, enablePlaceholders = false),
                pagingSourceFactory = { StreamPagingSource(streamRepository, dispatchers, language.code) },
            ).flow
        }
        .map { data: PagingData<Stream> -> data.map(convertStreamToVOStreamUseCase::invoke) }
        .cachedIn(viewModelScope)

    fun setLanguage(language: StreamsLanguage) {
        _language.value = language
    }
}
