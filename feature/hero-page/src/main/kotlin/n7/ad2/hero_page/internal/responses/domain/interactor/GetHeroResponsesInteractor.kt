package n7.ad2.hero_page.internal.responses.domain.interactor

import com.squareup.moshi.JsonAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import n7.ad2.AppLocale
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.hero_page.internal.responses.domain.model.LocalHeroResponsesItem
import n7.ad2.hero_page.internal.responses.domain.usecase.ConvertLocalHeroToVOResponseUseCase
import n7.ad2.hero_page.internal.responses.domain.vo.VOResponse
import n7.ad2.repositories.ResponseRepository
import javax.inject.Inject

class GetHeroResponsesInteractor @Inject constructor(
    private val convertLocalHeroToVOResponseUseCase: ConvertLocalHeroToVOResponseUseCase,
    private val repository: ResponseRepository,
    private val moshi: Moshi,
    private val dispatchers: DispatchersProvider,
) {

    operator fun invoke(heroName: String, appLocale: AppLocale): Flow<List<VOResponse>> = flow {
        val json = repository.getHeroResponses(heroName, appLocale)

        val type = Types.newParameterizedType(List::class.java, LocalHeroResponsesItem::class.java)
        val adapter: JsonAdapter<List<LocalHeroResponsesItem>> = moshi.adapter(type)
        val heroResponses = adapter.fromJson(json)!!

        val savedHeroResponses = repository.getSavedHeroResponses(heroName)

        val result = convertLocalHeroToVOResponseUseCase(heroName, heroResponses, savedHeroResponses)
        emit(result)
    }.flowOn(dispatchers.IO)

}