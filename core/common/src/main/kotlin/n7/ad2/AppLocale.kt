package n7.ad2

sealed class AppLocale(val value: String) {
    class English(value: String) : AppLocale(value)
    class Russian(value: String) : AppLocale(value)
}