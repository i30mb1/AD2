package n7.ad2.tournaments.api

import android.app.Application
import n7.ad2.app.logger.Logger
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies

interface TournamentsDependencies : Dependencies {
    val application: Application
    val logger: Logger
    val dispatchersProvider: DispatchersProvider
}
