package n7.ad2.tournaments.internal.di

import n7.ad2.tournaments.api.TournamentsDependencies
import n7.ad2.tournaments.internal.TournamentsFragment
import n7.ad2.tournaments.internal.TournamentsViewModel
import n7.ad2.tournaments.internal.match.MatchFragment

@dagger.Component(
    dependencies = [
        TournamentsDependencies::class,
    ],
)
internal interface TournamentsComponent {

    @dagger.Component.Factory
    interface Factory {
        fun create(dependencies: TournamentsDependencies): TournamentsComponent
    }

    fun inject(tournamentsFragment: TournamentsFragment)
    fun inject(matchFragment: MatchFragment)

    val tournamentsViewModelFactory: TournamentsViewModel.Factory
}
