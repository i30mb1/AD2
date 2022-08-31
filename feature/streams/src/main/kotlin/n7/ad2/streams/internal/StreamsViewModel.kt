package n7.ad2.streams.internal

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import n7.ad2.logger.Logger
import n7.ad2.repositories.ItemRepository
import n7.ad2.streams.internal.data.remote.Stream
import n7.ad2.streams.internal.data.remote.StreamPagingSource
import n7.ad2.streams.internal.domain.ConvertStreamToVOStreamUseCase
import n7.ad2.streams.internal.domain.vo.VOStream
import java.util.concurrent.Executors

internal class StreamsViewModel @AssistedInject constructor(
    private val streamPagingSource: StreamPagingSource,
    private val convertStreamToVOStreamUseCase: ConvertStreamToVOStreamUseCase,
    private val logger: Logger,
    private val itemRepository: ItemRepository,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): StreamsViewModel
    }

    init {
        viewModelScope.launch {
            itemRepository.getAllItems().collect {
                logger.log("11111111items loaded1111111")
            }
        }
    }

    private val _error = MutableLiveData<Throwable?>(null)
    val error: LiveData<Throwable?> = _error
    val streams: Flow<PagingData<VOStream>> = Pager(
        config = PagingConfig(pageSize = 33, enablePlaceholders = false),
        pagingSourceFactory = { streamPagingSource }
    )
        .flow
        .map { data: PagingData<Stream> -> data.map(Executors.newSingleThreadExecutor(), convertStreamToVOStreamUseCase::invoke) }
        .catch { _error.value = it }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

}