package n7.ad2.games.api

import android.app.Application
import n7.ad2.AppResources
import n7.ad2.dagger.Dependencies
import n7.ad2.logger.AD2Logger

interface GamesDependencies : Dependencies {
    val application: Application
    val res: AppResources
    val logger: AD2Logger
}