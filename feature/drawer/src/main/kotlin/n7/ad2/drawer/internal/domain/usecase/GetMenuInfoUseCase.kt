package n7.ad2.drawer.internal.domain.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.launch
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.drawer.internal.data.remote.model.VOMenuType
import n7.ad2.drawer.internal.domain.vo.VOMenuInfo
import n7.ad2.heroes.domain.usecase.GetHeroesUseCase
import n7.ad2.items.domain.usecase.GetItemsUseCase
import n7.ad2.items.domain.usecase.GetNewsUseCase
import javax.inject.Inject

/**
 * Numbers for the menu cards, each section as soon as it is known: local counts come first, the network ones later.
 * A section that failed or has nothing to tell (no live matches, no unread news) is left out and keeps its static subtitle.
 */
internal class GetMenuInfoUseCase @Inject constructor(
    private val getHeroesUseCase: GetHeroesUseCase,
    private val getItemsUseCase: GetItemsUseCase,
    private val getNewsUseCase: GetNewsUseCase,
    private val source: MenuInfoSource,
    private val logger: Logger,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(): Flow<Pair<VOMenuType, VOMenuInfo>> = channelFlow {
        fun load(type: VOMenuType, block: suspend () -> VOMenuInfo?) = launch {
            runCatching { block() }
                .onSuccess { info -> if (info != null) send(type to info) }
                .onFailure { error -> logger.log("menu info for $type failed (${error.message})") }
        }
        load(VOMenuType.HEROES) { VOMenuInfo.Heroes(getHeroesUseCase().first().size) }
        load(VOMenuType.ITEMS) { VOMenuInfo.Items(getItemsUseCase().first().size) }
        load(VOMenuType.NEWS) { unreadNews() }
        load(VOMenuType.TOURNAMENTS) { source.getLiveMatchesCount().takeIf { it > 0 }?.let(VOMenuInfo::LiveMatches) }
        load(VOMenuType.STREAMS) { source.getViewersCount().takeIf { it > 0 }?.let(VOMenuInfo::Viewers) }
    }.flowOn(dispatchers.IO)

    private suspend fun unreadNews(): VOMenuInfo? {
        val dates = getNewsUseCase(1).single().map { news -> news.date }
        val seenAt = source.getNewsSeenAt()
        if (seenAt == null) {
            dates.maxOrNull()?.let { source.setNewsSeenAt(it) }
            return null
        }
        return dates.count { date -> date > seenAt }.takeIf { it > 0 }?.let(VOMenuInfo::News)
    }
}
