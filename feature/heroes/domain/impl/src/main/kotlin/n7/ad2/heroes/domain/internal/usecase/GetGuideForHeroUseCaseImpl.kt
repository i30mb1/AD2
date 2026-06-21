package n7.ad2.heroes.domain.internal.usecase

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.internal.data.OpenDotaGuideMapper
import n7.ad2.heroes.domain.internal.data.model.OpenDotaHeroStat
import n7.ad2.heroes.domain.internal.data.model.OpenDotaItemPopularity
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

    override fun invoke(name: String): Flow<List<Guide>> = flow {
        val guide = runCatching { fetchGuide(name) }
            .onFailure { logger.log("guide fetch failed for $name: ${it.message}") }
            .getOrNull()
        emit(guide?.let { listOf(it) } ?: emptyList())
    }.flowOn(dispatcher.IO)

    private fun fetchGuide(name: String): Guide? {
        val stats = json.decodeFromString<List<OpenDotaHeroStat>>(httpGet("$BASE/heroStats"))
        val self = stats.firstOrNull { OpenDotaGuideMapper.matches(it, name) } ?: return null

        val matchups = runCatching {
            json.decodeFromString<List<OpenDotaMatchup>>(httpGet("$BASE/heroes/${self.id}/matchups"))
        }.getOrDefault(emptyList())

        val itemPopularity = runCatching {
            json.decodeFromString<OpenDotaItemPopularity>(httpGet("$BASE/heroes/${self.id}/itemPopularity"))
        }.getOrNull()

        val itemIds = runCatching {
            json.decodeFromString<Map<String, String>>(httpGet("$BASE/constants/item_ids"))
        }.getOrDefault(emptyMap())

        val abilities = runCatching {
            OpenDotaGuideMapper.parseAbilities(json, httpGet("$BASE/constants/hero_abilities"), self.name)
        }.getOrDefault(emptyList())

        return OpenDotaGuideMapper.toGuide(name, stats, matchups, itemPopularity, itemIds, abilities)
    }

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
        const val CONNECT_TIMEOUT_MS = 10_000
        const val READ_TIMEOUT_MS = 20_000
    }
}
