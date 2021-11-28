package n7.ad2.streams.internal.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import javax.inject.Inject

internal class StreamRepository @Inject constructor(
    private val twitchApi: TwitchApi,
) {

    suspend fun getStreams(loadSize: Int, paginationKey: String): Streams {
        return twitchApi.getStreams(loadSize, paginationKey)
    }

}

internal class StreamPagingSource @Inject constructor(
    private val streamRepository: StreamRepository,
) : PagingSource<String, Stream>() {

    override fun getRefreshKey(state: PagingState<String, Stream>): String? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPageToPosition = state.closestPageToPosition(anchorPosition) ?: return null
        return closestPageToPosition.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Stream> {
        val loadSize: Int = params.loadSize
        val paginationKey: String = params.key ?: ""

        return try {
            val streams = streamRepository.getStreams(loadSize, paginationKey)
            LoadResult.Page(streams.list, null, streams.pagination.cursor)
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }

}