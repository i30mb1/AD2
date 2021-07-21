package n7.ad2.ui.streams

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import n7.ad2.data.source.remote.StreamPagingSource
import n7.ad2.data.source.remote.model.Stream
import javax.inject.Inject

class StreamsViewModel @Inject constructor(
    private val application: Application,
    private val streamPagingSource: StreamPagingSource,
) : ViewModel() {

    fun getStreams(): Flow<PagingData<Stream>> {
        return Pager(
            config = PagingConfig(
                pageSize = 33,
                enablePlaceholders = false,
            ),
            pagingSourceFactory = { streamPagingSource }
        )
            .flow
            .stateIn(viewModelScope, SharingStarted.Lazily, PagingData.empty())
    }

}