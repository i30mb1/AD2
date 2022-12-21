package n7.ad2

sealed class AppLocale(val value: String) {
    object English : AppLocale("ru")
    object Russian : AppLocale("en")
}