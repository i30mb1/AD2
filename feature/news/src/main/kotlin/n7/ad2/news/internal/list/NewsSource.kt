package n7.ad2.news.internal.list

import androidx.paging.PagingSource
import androidx.paging.PagingState
import kotlinx.coroutines.flow.single
import n7.ad2.database_guides.internal.model.NewsLocal
import n7.ad2.news.internal.domain.usecase.GetNewsUseCase
import javax.inject.Inject

internal class NewsSource @Inject constructor(
    private val getNewsUseCase: GetNewsUseCase,
) : PagingSource<Int, NewsLocal>() {

    override fun getRefreshKey(state: PagingState<Int, NewsLocal>): Int? {
        return state.anchorPosition
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, NewsLocal> = try {
        val page = params.key ?: 1
        error("")
        val news = getNewsUseCase(page).single()
        LoadResult.Page(
            news,
            if (page == 1) null else page - 1,
            if (news.isEmpty()) null else page + 1,
        )
    } catch (error: Exception) {
        LoadResult.Error(error)
    }

}