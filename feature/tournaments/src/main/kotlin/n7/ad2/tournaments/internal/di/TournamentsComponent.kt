package n7.ad2.tournaments.internal.di

import n7.ad2.tournaments.api.TournamentsDependencies
import n7.ad2.tournaments.internal.TournamentsFragment
import n7.ad2.tournaments.internal.TournamentsViewModel

@dagger.Component(
    dependencies = [
        TournamentsDependencies::class
    ],
)
internal interface TournamentsComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: TournamentsDependencies): TournamentsComponent
    }

    fun inject(tournamentsFragment: TournamentsFragment)

    val tournamentsViewModelFactory: TournamentsViewModel.Factory

}