package n7.ad2.games.leaderboard

import android.content.Context
import android.os.Build
import n7.ad2.games.room.Player
import java.util.UUID

/** Игрок этого телефона: случайный id создается один раз и хранится локально, имя - модель устройства */
class LocalPlayerStore(context: Context) : LocalPlayerProvider {

    private val preferences = context.getSharedPreferences("games_player", Context.MODE_PRIVATE)

    override fun localPlayer(): Player {
        val id = preferences.getString(KEY_ID, null) ?: UUID.randomUUID().toString().also { preferences.edit().putString(KEY_ID, it).apply() }
        return Player(id, Build.MODEL.orEmpty().ifEmpty { "Player" })
    }

    private companion object {
        const val KEY_ID = "id"
    }
}
