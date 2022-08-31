package n7.ad2.games.api

import android.app.Application
import n7.ad2.Resources
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.Logger

interface GamesDependencies : Dependencies {
    val application: Application
    val res: Resources
    val logger: Logger
}