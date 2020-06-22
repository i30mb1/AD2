package n7.ad2.heroes.full

import androidx.paging.DataSource
import n7.ad2.data.source.local.Repository

class ResponsesSourceFactory(
        private val repository: Repository, private val search: String
) : DataSource.Factory<Int, Response>() {

    override fun create(): DataSource<Int, Response> {
        return PositionalResponsesDataSource(repository, search)
    }

}