package n7.ad2.hero.page.internal.responses.domain.interactor

import javax.inject.Inject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.serialization.json.Json
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero.page.internal.responses.domain.model.LocalHeroResponsesItem
import n7.ad2.hero.page.internal.responses.domain.usecase.ConvertLocalHeroToVOResponseUseCase
import n7.ad2.hero.page.internal.responses.domain.vo.VOResponse
import n7.ad2.repositories.ResponseRepository

class GetHeroResponsesInteractor @Inject constructor(
    private val convertLocalHeroToVOResponseUseCase: ConvertLocalHeroToVOResponseUseCase,
    private val repository: ResponseRepository,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(heroName: String, appLocale: AppLocale): Flow<List<VOResponse>> = flow {
        val json = repository.getHeroResponses(heroName, appLocale)

        val heroResponses = Json.decodeFromString<List<LocalHeroResponsesItem>>(json)

        val savedHeroResponses = repository.getSavedHeroResponses(heroName)

        val result = convertLocalHeroToVOResponseUseCase(heroName, heroResponses, savedHeroResponses)
        emit(result)
    }.flowOn(dispatchers.IO)

}