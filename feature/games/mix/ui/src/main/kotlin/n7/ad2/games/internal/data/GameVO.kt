package n7.ad2.games.internal.data

import n7.ad2.Resources
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.skillmp.SkillGameViewModel

internal sealed class Players {
    object One : Players()
    object Two : Players()
}

/** [quizId] - id игры в рекордах и комнате для викторин с режимами Solo/Versus/Coop, null у остальных; обложка рисуется по типу игры в GameCover */
internal sealed class GameVO(val title: String, val quizId: String? = null) {
    class GuessSkillMana(resources: Resources) : GameVO(resources.getString(R.string.spell_cost), SkillGameViewModel.GAME_ID)
    class GuessHero(resources: Resources) : GameVO(resources.getString(R.string.guess_hero), SkillGameViewModel.HERO_GAME_ID)
    class HigherLower(resources: Resources) : GameVO(resources.getString(R.string.higher_lower), SkillGameViewModel.COMPARE_GAME_ID)
    class BuildItem(resources: Resources) : GameVO(resources.getString(R.string.build_item), SkillGameViewModel.BUILD_GAME_ID)
    class Mid(resources: Resources) : GameVO(resources.getString(R.string.mid), MidViewModel.GAME_ID)
    class CanYouBuyIt(resources: Resources) : GameVO(resources.getString(R.string.can_you_buy_it))
    class XO(resources: Resources) : GameVO(resources.getString(R.string.xo))
}
