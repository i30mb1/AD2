package n7.ad2

import n7.ad2.common.jvm.DIComponent

interface AppInformation : DIComponent {
    val isDebug: Boolean
    val appLocale: AppLocale
    val appVersion: String
    val isNightMode: Boolean
}
