package n7.ad2.common.application

import n7.ad2.AppSettings

private class AD2Settings : AppSettings {
    override val isMockInterceptorEnabled: Boolean = false
}

fun appSettingsFactory(): AppSettings {
    return AD2Settings()
}
