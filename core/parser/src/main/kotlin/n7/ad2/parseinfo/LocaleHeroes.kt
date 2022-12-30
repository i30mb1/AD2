package n7.ad2.parseinfo

internal enum class LocaleHeroes(val mainUrl: String, val heroesUrl: String, val soundUrl: String, val folder: String) {
    RU(
        "https://dota2.fandom.com/ru/wiki/%s",
        "https://dota2.fandom.com/ru/wiki/Heroes",
        "https://dota2.fandom.com/ru/wiki/%s/Реплики",
        "ru",
    ),
    EN(
        "https://dota2.fandom.com/wiki/%s",
        "https://dota2.fandom.com/wiki/Heroes",
        "https://dota2.fandom.com/wiki/%s/Responses",
        "en",
    )
}