package n7.ad2.drawer.internal

import LoggerFake
import com.google.common.truth.Truth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import n7.ad2.coroutines.CoroutineTestRule
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.usecase.GetMenuInfoUseCase
import n7.ad2.drawer.internal.domain.usecase.MenuInfoSource
import n7.ad2.drawer.internal.domain.vo.VOMenuInfo
import n7.ad2.heroes.domain.model.Hero
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.items.domain.model.Item
import n7.ad2.items.domain.model.News
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.GetNewsUseCase
import org.junit.Rule
import org.junit.Test

internal class GetMenuInfoUseCaseTest {

    @get:Rule val coroutineRule = CoroutineTestRule(StandardTestDispatcher())

    private val source = MenuInfoSourceFake()
    private var newsDates = listOf(300L, 200L, 100L)
    private var isNewsError = false

    private val getHeroes = object : GetHeroesUseCase {
        override fun invoke(): Flow<List<Hero>> = flowOf(List(3) { Hero("h$it", "", false, "str") })
    }
    private val getItems = object : GetItemsUseCase {
        override fun invoke(): Flow<List<Item>> = flowOf(List(5) { Item("i$it", "", "", "", false) })
    }
    private val getNews = object : GetNewsUseCase {
        override suspend fun invoke(page: Int): Flow<List<News>> {
            if (isNewsError) error("no network")
            return flowOf(newsDates.map { date -> News("", "", page, "", date, "", "", false) })
        }
    }

    private fun useCase() = GetMenuInfoUseCase(getHeroes, getItems, getNews, source, LoggerFake(), coroutineRule.dispatchers)

    private suspend fun info(): Map<VOMenuType, VOMenuInfo> = useCase()().toList().toMap()

    @Test
    fun `counts heroes, items, live matches and viewers`() = runTest {
        val info = info()
        Truth.assertThat(info[VOMenuType.HEROES]).isEqualTo(VOMenuInfo.Heroes(3))
        Truth.assertThat(info[VOMenuType.ITEMS]).isEqualTo(VOMenuInfo.Items(5))
        Truth.assertThat(info[VOMenuType.TOURNAMENTS]).isEqualTo(VOMenuInfo.LiveMatches(2))
        Truth.assertThat(info[VOMenuType.STREAMS]).isEqualTo(VOMenuInfo.Viewers(48_200))
    }

    @Test
    fun `unread news are the ones published after the news were last opened`() = runTest {
        source.newsSeenAt = 150L
        Truth.assertThat(info()[VOMenuType.NEWS]).isEqualTo(VOMenuInfo.News(2))
    }

    @Test
    fun `nothing to show when there are no live matches and no unread news`() = runTest {
        source.liveMatches = 0
        source.newsSeenAt = 300L
        val info = info()
        Truth.assertThat(info).doesNotContainKey(VOMenuType.TOURNAMENTS)
        Truth.assertThat(info).doesNotContainKey(VOMenuType.NEWS)
    }

    @Test
    fun `first launch remembers the newest news as seen instead of calling all of them new`() = runTest {
        source.newsSeenAt = null
        Truth.assertThat(info()).doesNotContainKey(VOMenuType.NEWS)
        Truth.assertThat(source.newsSeenAt).isEqualTo(300L)
    }

    @Test
    fun `a failed source does not hide the others`() = runTest {
        isNewsError = true
        source.isStreamsError = true
        val info = info()
        Truth.assertThat(info.keys).containsExactly(VOMenuType.HEROES, VOMenuType.ITEMS, VOMenuType.TOURNAMENTS)
    }
}

internal class MenuInfoSourceFake : MenuInfoSource {
    var liveMatches = 2
    var isStreamsError = false
    var newsSeenAt: Long? = 0L

    override suspend fun getLiveMatchesCount(): Int = liveMatches
    override suspend fun getViewersCount(): Int = if (isStreamsError) error("twitch is down") else 48_200
    override suspend fun getNewsSeenAt(): Long? = newsSeenAt
    override suspend fun setNewsSeenAt(seconds: Long) {
        newsSeenAt = seconds
    }
}
