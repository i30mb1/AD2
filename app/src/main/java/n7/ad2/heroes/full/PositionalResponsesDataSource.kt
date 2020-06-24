package n7.ad2.heroes.full

import androidx.paging.PositionalDataSource
import n7.ad2.ui.heroResponse.domain.vo.VOResponse

class PositionalResponsesDataSource(
        private val list: List<VOResponse>,
        private val search: String
) : PositionalDataSource<VOResponse>() {

    override fun loadInitial(params: LoadInitialParams, callback: LoadInitialCallback<VOResponse>) {
        callback.onResult(list, 0)
    }

    override fun loadRange(params: LoadRangeParams, callback: LoadRangeCallback<VOResponse>) {
        callback.onResult(emptyList())
    }

}