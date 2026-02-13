package n7.ad2.parser

enum class LOCALE(val urlAllItems: String, val baseUrl: String, val directory: String) {
    RU(
        "https://dota2.fandom.com/ru/wiki/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B",
        "https://dota2.fandom.com",
        "ru",
    ),
    EN(
        "https://dota2.fandom.com/wiki/Items",
        "https://dota2.fandom.com",
        "en",
    ),
}
