package n7.ad2.hero.page.internal.guides.domain.vo

data class VOHeroFlowItem(val heroName: String, val urlHeroImage: String, val heroWinrate: String)
data class VOHeroFlowSpell(val skillName: String, val urlImageSkill: String, val skillOrder: String)
data class VOHeroFlowStartingHeroItem(val itemName: String, val urlHeroItem: String)
data class VOHeroFlowHeroItem(val itemName: String, val urlHeroItem: String, val itemTiming: String?)
