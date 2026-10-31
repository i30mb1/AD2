package n7.ad2.games.internal.data

import n7.ad2.Resources
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.show.ShowViewModel

/** [quizId] - id игры в рекордах и комнате для игр с режимами Solo/Versus/Coop, null у остальных; обложка рисуется по типу игры в GameCover */
internal sealed class GameVO(val title: String, val quizId: String? = null) {
    class Show(resources: Resources) : GameVO(resources.getString(R.string.show), ShowViewModel.GAME_ID)
    class Mid(resources: Resources) : GameVO(resources.getString(R.string.mid), MidViewModel.GAME_ID)
    class XO(resources: Resources) : GameVO(resources.getString(R.string.xo))
}
