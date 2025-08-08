package n7.ad2.tournaments.internal.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class TournamentGame(
    @PrimaryKey
    var url: String = "",

    var team1Name: String = ".",
    var team2Name: String = ".",
    var team1Logo: String = ".",
    var team2Logo: String = ".",
    var teamScore: String = ".",
    var teamTimeRemains: Long = 0,
    var teamTime: Long = 0,
)
