package n7.ad2

sealed class AppLocale(val value: String) {
    object English : AppLocale("en")
    object Russian : AppLocale("ru")
}