package n7.ad2

import javax.inject.Inject

class AD2Settings @Inject constructor() : AppSettings {
    override val isMockInterceptorEnabled: Boolean = false
}