package n7.ad2.drawer.internal.domain.usecase

/** What the menu reads from other sections and remembers about the news. */
internal interface MenuInfoSource {
    suspend fun getLiveMatchesCount(): Int
    suspend fun getViewersCount(): Int

    /** Unix seconds of the newest news the user has seen, null before the first launch. */
    suspend fun getNewsSeenAt(): Long?
    suspend fun setNewsSeenAt(seconds: Long)
}
