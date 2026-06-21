package n7.ad2.heroes.domain.internal.usecase

import android.app.Application
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import n7.ad2.AppInformation
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.heroes.domain.internal.data.model.HeroDescriptionJson
import n7.ad2.heroes.domain.model.Ability
import n7.ad2.heroes.domain.model.HeroDescription
import n7.ad2.heroes.domain.model.MainAttribute
import n7.ad2.heroes.domain.usecase.GetHeroDescriptionUseCase

internal class GetHeroDescriptionUseCaseImpl(
    private val application: Application,
    private val appInformation: AppInformation,
    private val dispatcher: DispatchersProvider,
) : GetHeroDescriptionUseCase {

    private val json = Json { ignoreUnknownKeys = true }

    override fun invoke(name: String): Flow<HeroDescription> = flow {
        val key = name.lowercase().replace(" ", "_")
        val locale = appInformation.appLocale.value
        val text = application.assets
            .open("heroes/$key/$locale/description.json")
            .bufferedReader()
            .use { reader -> reader.readText() }
        emit(json.decodeFromString<HeroDescriptionJson>(text).toHeroDescription())
    }.flowOn(dispatcher.IO)
}

private fun HeroDescriptionJson.toHeroDescription(): HeroDescription = HeroDescription(
    abilities = abilities.map { ability ->
        Ability(
            imageUrl = "",
            cooldown = ability.cooldown,
            description = ability.description,
            effects = ability.effects,
            hotKey = ability.hotKey,
            itemBehaviour = ability.itemBehaviour,
            legacyKey = ability.legacyKey,
            mana = ability.mana,
            notes = ability.notes,
            params = ability.params,
            audioUrl = ability.audioUrl,
            name = ability.name,
            story = ability.story,
            talents = null,
        )
    },
    description = description,
    history = history,
    mainAttributes = MainAttribute(
        attrAgility = mainAttributes.attrAgility,
        attrAgilityInc = mainAttributes.attrAgilityInc,
        attrIntelligence = mainAttributes.attrIntelligence,
        attrIntelligenceInc = mainAttributes.attrIntelligenceInc,
        attrStrength = mainAttributes.attrStrength,
        attrStrengthInc = mainAttributes.attrStrengthInc,
    ),
    trivia = trivia,
)
