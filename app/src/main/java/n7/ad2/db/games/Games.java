package n7.ad2.db.games;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity
public class Games {

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
