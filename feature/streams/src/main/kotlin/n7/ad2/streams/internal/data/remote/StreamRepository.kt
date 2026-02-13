package n7.ad2.streams.internal.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.streams.internal.data.remote.retrofit.TwitchApi
import javax.inject.Inject

internal class StreamRepository @Inject constructor(private val twitchApi: TwitchApi) {

    suspend fun getStreams(loadSize: Int, paginationKey: String): Streams = twitchApi.getStreams(loadSize, paginationKey)
}

internal class StreamPagingSource @Inject constructor(private val streamRepository: StreamRepository, private val dispatcher: DispatchersProvider) : PagingSource<String, Stream>() {

    override fun getRefreshKey(state: PagingState<String, Stream>): String? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPageToPosition = state.closestPageToPosition(anchorPosition) ?: return null
        return closestPageToPosition.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Stream> = withContext(dispatcher.IO) {
        val loadSize: Int = params.loadSize
        val paginationKey: String = params.key ?: ""

        try {
            val streams = streamRepository.getStreams(loadSize, paginationKey)
            LoadResult.Page(streams.list, null, streams.pagination.cursor)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
