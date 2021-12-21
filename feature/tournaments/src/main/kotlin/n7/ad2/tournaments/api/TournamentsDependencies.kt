package n7.ad2.tournaments.api

import android.app.Application
import n7.ad2.coroutines.DispatchersProvider
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.AD2Logger

interface TournamentsDependencies : Dependencies {
    val application: Application
    val logger: AD2Logger
    val dispatchersProvider: DispatchersProvider
}