package n7.ad2.feature.games.xo.domain.internal.usecase

import android.os.Build
import n7.ad2.feature.games.xo.domain.GetDeviceNameUseCase

internal class GetDeviceNameUseCaseImpl : GetDeviceNameUseCase {

    override suspend fun invoke(): String = Build.MODEL
}
