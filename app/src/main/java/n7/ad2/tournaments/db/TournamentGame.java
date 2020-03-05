package n7.ad2.tournaments.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity
public class TournamentGame {

    @PrimaryKey
    @NonNull
    public String url;

    public String team1Name = ".";
    public String team2Name = ".";
    public String team1Logo = ".";
    public String team2Logo = ".";
    public String teamScore = ".";
    public long teamTimeRemains = 0;
    public long teamTime = 0;

}
