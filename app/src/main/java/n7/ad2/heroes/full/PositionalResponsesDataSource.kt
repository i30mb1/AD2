package n7.ad2.heroes.full

import androidx.paging.PositionalDataSource
import n7.ad2.data.source.local.Repository

class PositionalResponsesDataSource(
        private val responsesStorage: Repository,
        private val search: String
) : PositionalDataSource<Response>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<Response>) {
        val list = responsesStorage.getHeroResponses("", "")
        callback.onResult(list, 0)
        if (search == "") {
            val list: List<Response?> = responsesStorage.getData(params.requestedStartPosition, params.requestedLoadSize)
            callback.onResult(list, params.requestedStartPosition)
        } else {
            val list: List<Response?> = responsesStorage.getDataSearch(search)
            callback.onResult(list, 0)
        }
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<Response>) {
            callback.onResult(emptyList())
    }

}