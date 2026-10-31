package n7.ad2.streams.internal.data.remote

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.streams.internal.data.remote.retrofit.TwitchApi
import javax.inject.Inject

internal class StreamRepository @Inject constructor(private val twitchApi: TwitchApi) {

    suspend fun getStreams(loadSize: Int, paginationKey: String, language: String?): Streams = twitchApi.getStreams(loadSize, paginationKey, language = language)

    suspend fun getStream(login: String): Stream? = twitchApi.getStream(login).list.firstOrNull()
}

/** [language] is a Twitch language code ("ru", "en"), null loads streams in every language. */
internal class StreamPagingSource(
    private val streamRepository: StreamRepository,
    private val dispatcher: DispatchersProvider,
    private val language: String?,
) : PagingSource<String, Stream>() {

    override fun getRefreshKey(state: PagingState<String, Stream>): String? {
        val anchorPosition = state.anchorPosition ?: return null
        val closestPageToPosition = state.closestPageToPosition(anchorPosition) ?: return null
        return closestPageToPosition.nextKey
    }

    override suspend fun load(params: LoadParams<String>): LoadResult<String, Stream> = withContext(dispatcher.IO) {
        val loadSize: Int = params.loadSize
        val paginationKey: String = params.key ?: ""

        try {
            val streams = streamRepository.getStreams(loadSize, paginationKey, language)
            // the last page comes with an empty cursor, an empty key would load the first page again
            LoadResult.Page(streams.list, null, streams.pagination.cursor.ifEmpty { null })
        } catch (e: Exception) {
            LoadResult.Error(e)
        }
    }
}
