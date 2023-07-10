package n7.ad2

interface AppInformation {
    val isDebug: Boolean
    val appLocale: AppLocale
    val appVersion: String
    val isNightMode: Boolean
}
