package n7.ad2.parseinfo

enum class LOCALE(val urlAllItems: String, val baseUrl: String, val directory: String) {
    RU(
        "https://dota2-ru.gamepedia.com/%D0%9F%D1%80%D0%B5%D0%B4%D0%BC%D0%B5%D1%82%D1%8B",
        "https://dota2-ru.gamepedia.com",
        "ru"
    ),
    EN(
        "https://dota2.gamepedia.com/Items",
        "https://dota2.gamepedia.com",
        "en"
    )
}