package n7.ad2.hero.page.internal.info.domain.vo

/** Rarely needed parts of the "Info" page: a row at the bottom each, the content opens in a sheet. */
enum class InfoRow { FACETS, TIPS, ITEMS, COUNTERS, CHANGES, LORE }

/**
 * The rows this hero has data for, in the order they are shown. Wiki blocks exist only in some languages.
 * [hasMatchups] is true once the OpenDota guide with real matchups is loaded: then the matchups have a section
 * of their own on the page, which opens the same sheet.
 */
fun VOHeroInfo.rows(hasMatchups: Boolean = false): List<InfoRow> {
    val wiki = description.wiki
    return InfoRow.entries.filter { row ->
        when (row) {
            InfoRow.FACETS -> description.facets.isNotEmpty()
            InfoRow.TIPS -> wiki.playstyle != null || wiki.tips.isNotEmpty()
            InfoRow.ITEMS -> wiki.recommendedItems.isNotEmpty()
            InfoRow.COUNTERS -> !hasMatchups && wiki.counters.isNotEmpty()
            InfoRow.CHANGES -> description.changes.isNotEmpty()
            InfoRow.LORE -> description.bio.isNotEmpty() || description.trivia.isNotEmpty()
        }
    }
}
