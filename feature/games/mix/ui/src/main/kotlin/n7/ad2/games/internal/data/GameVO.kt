package n7.ad2.games.internal.data

import n7.ad2.Resources
import n7.ad2.feature.games.mix.ui.R
import n7.ad2.games.internal.games.invoker.InvokerViewModel
import n7.ad2.games.internal.games.mid.MidViewModel
import n7.ad2.games.internal.games.show.ShowViewModel
import n7.ad2.games.internal.games.xo.XoViewModel
import n7.ad2.games.room.GameMode
import n7.ad2.games.room.MAX_PLAYERS

/** [id] - id игры в рекордах и комнате, [modes] - в каких режимах в нее играют; обложка рисуется по типу игры в GameCover */
internal sealed class GameVO(val title: String, val id: String) {
    val modes: List<GameMode> get() = modesOf(id)

    class Show(resources: Resources) : GameVO(resources.getString(R.string.show), ShowViewModel.GAME_ID)
    class Mid(resources: Resources) : GameVO(resources.getString(R.string.mid), MidViewModel.GAME_ID)
    class Invoker(resources: Resources) : GameVO(resources.getString(R.string.invoker), InvokerViewModel.GAME_ID)
    class XO(resources: Resources) : GameVO(resources.getString(R.string.xo), XoViewModel.GAME_ID)
}

/** Крестики - только соло и друг против друга, остальные игры - во всех режимах */
internal fun modesOf(gameId: String): List<GameMode> = if (gameId == XoViewModel.GAME_ID) listOf(GameMode.SOLO, GameMode.VERSUS) else GameMode.entries

/** Сколько игроков вмещает игра по сети: мид и крестики - вдвоем, викторина и инвокер - вся комната */
internal fun maxPlayersOf(gameId: String): Int = if (gameId == MidViewModel.GAME_ID || gameId == XoViewModel.GAME_ID) 2 else MAX_PLAYERS
