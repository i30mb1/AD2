package n7.ad2.parser

/** [countersPage] and [guidePage] are hero subpage names on the wiki of this language. */
internal enum class LocaleHeroes(
    val mainUrl: String,
    val heroesUrl: String,
    val soundUrl: String,
    val folder: String,
    val countersPage: String,
    val guidePage: String,
) {
    RU(
        "https://dota2.fandom.com/ru/wiki/%s",
        "https://dota2.fandom.com/ru/wiki/Heroes",
        "https://dota2.fandom.com/ru/wiki/%s/Реплики",
        "ru",
        "Противостоящие",
        "Руководство",
    ),
    EN(
        "https://dota2.fandom.com/wiki/%s",
        "https://dota2.fandom.com/wiki/Heroes",
        "https://dota2.fandom.com/wiki/%s/Responses",
        "en",
        "Counters",
        "Guide",
    ),
}
