package n7.ad2.ui.items.domain.interactor

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import n7.ad2.ui.items.domain.usecase.ConvertLocalItemsToVoItemsUseCase
import n7.ad2.ui.items.domain.usecase.GetAllLocalItemsUseCase
import n7.ad2.ui.items.domain.vo.VOItemBody
import javax.inject.Inject

class GetVOItemsInteractor @Inject constructor(
    private val getAllLocalItemsUseCase: GetAllLocalItemsUseCase,
    private val convertLocalItemsToVoItemsUseCase: ConvertLocalItemsToVoItemsUseCase,
) {

    operator fun invoke(): Flow<List<VOItemBody>> {
        return getAllLocalItemsUseCase()
            .map(convertLocalItemsToVoItemsUseCase::invoke)
    }
}