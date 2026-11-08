package n7.ad2.hero.page.internal.responses.domain.interactor

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
import java.io.FileNotFoundException
import javax.inject.Inject

private val lenientJson = Json { ignoreUnknownKeys = true }

class GetHeroResponsesInteractor @Inject constructor(
    private val convertLocalHeroToVOResponseUseCase: ConvertLocalHeroToVOResponseUseCase,
    private val repository: ResponseRepository,
    private val dispatchers: DispatchersProvider,
) {

    /** The wiki of a language may have no voice lines of a hero (Kez and Largo in English), then the list is empty. */
    operator fun invoke(heroName: String, appLocale: AppLocale): Flow<List<VOResponse>> = flow {
        val json = try {
            repository.getHeroResponses(heroName, appLocale)
        } catch (e: FileNotFoundException) {
            null
        }

        val heroResponses = json?.let { lenientJson.decodeFromString<List<LocalHeroResponsesItem>>(it) }.orEmpty()

        val savedHeroResponses = repository.getSavedHeroResponses(heroName)

        val result = convertLocalHeroToVOResponseUseCase(heroName, heroResponses, savedHeroResponses)
        emit(result)
    }.flowOn(dispatchers.IO)
}
