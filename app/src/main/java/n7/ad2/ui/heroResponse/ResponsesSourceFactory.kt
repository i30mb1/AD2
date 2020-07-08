package n7.ad2.ui.heroResponse

import androidx.paging.DataSource
import n7.ad2.ui.heroResponse.domain.vo.VOResponse

class ResponsesSourceFactory(
        private val list: List<VOResponse>,
        private val search: String
) : DataSource.Factory<Int, VOResponse>() {

    override fun create(): DataSource<Int, VOResponse> {
        return PositionalResponsesDataSource(list, search)
    }

}