package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.serialization.json.Json
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.internal.data.OpenDotaGuideMapper
import n7.ad2.heroes.domain.internal.data.model.OpenDotaDuration
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroMatch
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroStat
import n7.ad2.heroes.domain.internal.data.model.OpenDotaItemPopularity
import n7.ad2.heroes.domain.internal.data.model.OpenDotaMatch
import n7.ad2.heroes.domain.internal.data.model.OpenDotaMatchup
import n7.ad2.heroes.domain.model.Guide
import n7.ad2.heroes.domain.usecase.GetGuideForHeroUseCase
import java.net.HttpURLConnection
import java.net.URL

/**
 * Builds a hero guide (win-rate, popular item builds, skill order, easy/hard match-ups) from the public
 * OpenDota JSON API. Each secondary call is best-effort: a failure leaves that section empty rather than
 * breaking the whole guide. The whole thing falls back to an empty emission on any hard failure.
 */
internal class GetGuideForHeroUseCaseImpl(
    private val dispatcher: DispatchersProvider,
    private val logger: Logger,
) : GetGuideForHeroUseCase {

    private val json = Json { ignoreUnknownKeys = true }

    // the Info and Guides tabs both need the guide and start together, the second one waits and takes the copy
    private val cache = mutableMapOf<String, Guide>()
    private val mutex = Mutex()

    override fun invoke(name: String): Flow<List<Guide>> = flow {
        val guide = mutex.withLock {
            cache[name] ?: runCatching { fetchGuide(name) }
                .onFailure { logger.log("guide fetch failed for $name: ${it.message}") }
                .getOrNull()
                ?.also { cache[name] = it }
        }
        emit(guide?.let { listOf(it) } ?: emptyList())
    }.flowOn(dispatcher.IO)

    private suspend fun fetchGuide(name: String): Guide? = coroutineScope {
        val stats = json.decodeFromString<List<OpenDotaHeroStat>>(httpGet("$BASE/heroStats"))
        val self = stats.firstOrNull { OpenDotaGuideMapper.matches(it, name) } ?: return@coroutineScope null

        val matchups = async { optional(emptyList()) { json.decodeFromString<List<OpenDotaMatchup>>(httpGet("$BASE/heroes/${self.id}/matchups")) } }
        val itemPopularity = async { optional(null) { json.decodeFromString<OpenDotaItemPopularity>(httpGet("$BASE/heroes/${self.id}/itemPopularity")) } }
        val itemIds = async { optional(emptyMap()) { json.decodeFromString<Map<String, String>>(httpGet("$BASE/constants/item_ids")) } }
        val durations = async { optional(emptyList()) { json.decodeFromString<List<OpenDotaDuration>>(httpGet("$BASE/heroes/${self.id}/durations")) } }
        val upgrades = async { optional(emptyList()) { fetchUpgradeSequences(self.id) } }

        OpenDotaGuideMapper.toGuide(
            heroName = name,
            stats = stats,
            matchups = matchups.await(),
            itemPopularity = itemPopularity.await(),
            itemIds = itemIds.await(),
            durations = durations.await(),
            upgradeSequences = upgrades.await(),
        )
    }

    /** Skill builds of the hero in its latest pro matches, OpenDota has no aggregate for them. */
    private suspend fun fetchUpgradeSequences(heroId: Int): List<List<String>> = coroutineScope {
        val abilityIds = async { json.decodeFromString<Map<String, String>>(httpGet("$BASE/constants/ability_ids")) }
        val matchIds = json.decodeFromString<List<OpenDotaHeroMatch>>(httpGet("$BASE/heroes/$heroId/matches"))
            .take(SKILL_BUILD_MATCHES)
            .map { it.matchId }
        val upgrades = matchIds.map { matchId ->
            async {
                optional(null) {
                    json.decodeFromString<OpenDotaMatch>(httpGet("$BASE/matches/$matchId"))
                        .players.firstOrNull { it.heroId == heroId }?.abilityUpgrades
                }
            }
        }.awaitAll().filterNotNull()
        val names = abilityIds.await()
        upgrades.map { ids -> ids.mapNotNull { names[it.toString()] } }
    }

    private inline fun <T> optional(default: T, block: () -> T): T = runCatching(block)
        .onFailure { logger.log("guide part failed: ${it.message}") }
        .getOrDefault(default)

    private fun httpGet(urlString: String): String {
        val connection = (URL(urlString).openConnection() as HttpURLConnection).apply {
            connectTimeout = CONNECT_TIMEOUT_MS
            readTimeout = READ_TIMEOUT_MS
            requestMethod = "GET"
            setRequestProperty("Accept", "application/json")
            setRequestProperty("User-Agent", "AD2/1.0")
        }
        return try {
            connection.inputStream.bufferedReader().use { it.readText() }
        } finally {
            connection.disconnect()
        }
    }

    private companion object {
        const val BASE = "https://api.opendota.com/api"
        const val SKILL_BUILD_MATCHES = 8
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 20_000
    }
}
