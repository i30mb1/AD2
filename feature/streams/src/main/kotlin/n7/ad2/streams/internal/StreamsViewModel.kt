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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.streams.internal.data.remote.Stream
import n7.ad2.streams.internal.data.remote.StreamPagingSource
import n7.ad2.streams.internal.domain.ConvertStreamToVOStreamUseCase
import n7.ad2.streams.internal.domain.vo.VOStream
import javax.inject.Provider

internal class StreamsViewModel @AssistedInject constructor(
    private val streamPagingSource: Provider<StreamPagingSource>,
    private val convertStreamToVOStreamUseCase: ConvertStreamToVOStreamUseCase,
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(): StreamsViewModel
    }

    val streams: Flow<PagingData<VOStream>> = Pager(
        config = PagingConfig(pageSize = 33, enablePlaceholders = false),
        pagingSourceFactory = { streamPagingSource.get() }
    )
        .flow
        .map { data: PagingData<Stream> -> data.map(convertStreamToVOStreamUseCase::invoke) }
        .cachedIn(viewModelScope)

}