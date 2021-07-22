package n7.ad2.ui.streams

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import n7.ad2.data.source.remote.StreamPagingSource
import n7.ad2.data.source.remote.model.Stream
import n7.ad2.ui.streams.domain.vo.VOSimpleStream
import n7.ad2.ui.streams.domain.vo.VOStream
import java.util.concurrent.Executors
import javax.inject.Inject

class StreamsViewModel @Inject constructor(
    private val streamPagingSource: StreamPagingSource,
) : ViewModel() {

    val streams: Flow<PagingData<VOStream>> = Pager(
        config = PagingConfig(
            pageSize = 33,
            enablePlaceholders = false,
        ),
        pagingSourceFactory = { streamPagingSource }
    )
        .flow
        .map { data: PagingData<Stream> ->
            data.map(Executors.newSingleThreadExecutor()) { stream: Stream ->
                VOSimpleStream(stream.title) as VOStream
            }
        }
        .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())

}