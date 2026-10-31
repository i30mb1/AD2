package n7.ad2.games.wiring

import android.app.Application
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import n7.ad2.feature.games.xo.domain.WifiDirect
import n7.ad2.feature.games.xo.domain.di.XoDomainComponent
import n7.ad2.games.leaderboard.LeaderboardRepository
import n7.ad2.games.leaderboard.LeaderboardSync
import n7.ad2.games.leaderboard.LocalPlayerProvider
import n7.ad2.games.leaderboard.LocalPlayerStore
import n7.ad2.games.room.RoomFactory
import n7.ad2.games.room.WebsocketRoomFactory

/**
 * Сетевые комнаты и рекорды живут дольше экрана (лобби -> игра), поэтому держим их на уровне процесса
 */
@dagger.Module
object GamesModule {

    private val processScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)
    private var leaderboard: LeaderboardRepository? = null
    private var sync: LeaderboardSync? = null
    private var wifiDirect: WifiDirect? = null

    @dagger.Provides
    fun provideRoomFactory(): RoomFactory = WebsocketRoomFactory(processScope)

    @dagger.Provides
    fun provideLeaderboardRepository(application: Application): LeaderboardRepository = synchronized(this) {
        leaderboard ?: LeaderboardRepository(application).also { leaderboard = it }
    }

    @dagger.Provides
    fun provideLeaderboardSync(repository: LeaderboardRepository): LeaderboardSync = synchronized(this) {
        sync ?: LeaderboardSync(repository, processScope).also { sync = it }
    }

    // группа Wi-Fi Direct переживает лобби и держится, пока идет игра
    @dagger.Provides
    fun provideWifiDirect(component: XoDomainComponent): WifiDirect = synchronized(this) {
        wifiDirect ?: component.wifiDirect.also { wifiDirect = it }
    }

    @dagger.Provides
    fun provideLocalPlayerProvider(application: Application): LocalPlayerProvider = LocalPlayerStore(application)
}
