package n7.ad2.ui.heroInfo.domain.adapter

import n7.ad2.CustomHeroAttrs
import n7.ad2.data.source.local.Repository
import n7.ad2.ui.heroInfo.domain.model.MainAttribute
import n7.ad2.ui.heroInfo.domain.vo.VOHeroAttrs

fun MainAttribute.toVOHeroAttrs(heroName: String): VOHeroAttrs = VOHeroAttrs(
    Repository.getFullUrlHeroImage(heroName),
    CustomHeroAttrs.Companion.HeroAttrs(this.attrStrength, this.attrAgility, this.attrIntelligence)
)