package n7.ad2.news.ui.internal.screen.news.model

/** [date] is already formatted for the list, empty for news saved before dates were stored. */
data class NewsVO(val id: Int, val title: String, val imageUrl: String, val source: String, val date: String, val isOfficial: Boolean)

enum class NewsFilter { ALL, OFFICIAL, PRESS }
