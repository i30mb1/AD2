package n7.ad2.drawer.internal.data

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import kotlinx.coroutines.withContext
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.drawer.internal.domain.usecase.MenuInfoSource
import n7.ad2.navigator.Navigator
import javax.inject.Inject

private const val PREFERENCES = "drawer"
private const val KEY_NEWS_SEEN_AT = "news_seen_at"

internal class MenuInfoSourceImpl @Inject constructor(
    private val application: Application,
    private val navigator: Navigator,
    private val dispatchers: DispatchersProvider,
) : MenuInfoSource {

    private val preferences by lazy { application.getSharedPreferences(PREFERENCES, Context.MODE_PRIVATE) }

    override suspend fun getLiveMatchesCount(): Int = navigator.tournamentsApi.getLiveMatchesCount()

    override suspend fun getViewersCount(): Int = navigator.streamApi.getViewersCount(application)

    override suspend fun getNewsSeenAt(): Long? = withContext(dispatchers.IO) {
        if (preferences.contains(KEY_NEWS_SEEN_AT)) preferences.getLong(KEY_NEWS_SEEN_AT, 0) else null
    }

    override suspend fun setNewsSeenAt(seconds: Long) = withContext(dispatchers.IO) {
        preferences.edit { putLong(KEY_NEWS_SEEN_AT, seconds) }
    }
}
