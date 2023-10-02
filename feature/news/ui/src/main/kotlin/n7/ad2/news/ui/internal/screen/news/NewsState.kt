package n7.ad2.news.ui.internal.screen.news

import n7.ad2.news.ui.internal.screen.NewsVO

data class NewsState(
    val isLoading: Boolean,
    val isError: Boolean,
    val list: List<NewsVO>,
) {

    companion object {
        fun init() = NewsState(
            isLoading = true,
            isError = false,
            list = emptyList()
        )
    }
}
