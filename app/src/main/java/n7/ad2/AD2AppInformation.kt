package n7.ad2

import javax.inject.Inject

class AD2AppInformation @Inject constructor() : AppInformation {
    override val isDebug = BuildConfig.DEBUG
}