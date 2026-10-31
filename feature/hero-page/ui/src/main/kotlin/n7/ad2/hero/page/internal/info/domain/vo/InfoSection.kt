package n7.ad2.hero.page.internal.info.domain.vo

/** Chips under the tabs of the "Info" page, only one section is on the screen at a time. */
enum class InfoSection { ABILITIES, STATS, TALENTS, FACETS, STRATEGY, COUNTERS, ITEMS, LORE }

/** The chips this hero has data for, in the order they are shown. Wiki blocks exist only in some languages. */
fun VOHeroInfo.sections(): List<InfoSection> {
    val wiki = description.wiki
    return InfoSection.entries.filter { section ->
        when (section) {
            InfoSection.ABILITIES -> abilityGroups.isNotEmpty()
            InfoSection.STATS -> true
            InfoSection.TALENTS -> description.talents.isNotEmpty()
            InfoSection.FACETS -> description.facets.isNotEmpty()
            InfoSection.STRATEGY -> wiki.playstyle != null || wiki.tips.isNotEmpty()
            InfoSection.COUNTERS -> wiki.counters.isNotEmpty()
            InfoSection.ITEMS -> wiki.recommendedItems.isNotEmpty()
            InfoSection.LORE -> description.bio.isNotEmpty() || description.trivia.isNotEmpty()
        }
    }
}
