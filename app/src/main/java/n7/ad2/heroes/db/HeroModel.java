package n7.ad2.heroes.db;

import android.arch.persistence.room.ColumnInfo;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.support.annotation.NonNull;

@Entity(tableName = "HeroModel")
public class HeroModel {

    @PrimaryKey
    @NonNull
    @ColumnInfo(name = "codeName")
    private String codeName;
    @NonNull
    private String name;

    @ColumnInfo(name = "winrate")
    private String winrate = "-";
    @ColumnInfo(name = "pickrate")
    private String pickrate = "-";
    @ColumnInfo(name = "lane")
    private String lane = "-+-+-+-+-";
    @ColumnInfo(name = "time")
    private String time = "-+-+-+-+-";
    @ColumnInfo(name = "bestVersus")
    private String bestVersus = "-^-";
    @ColumnInfo(name = "worstVersus")
    private String worstVersus = "-^-";
    @ColumnInfo(name = "startingItems")
    private String startingItems = "-+-+-+-+-";
    @ColumnInfo(name = "furtherItems")
    private String furtherItems = "-+-+-+-+-";
    @ColumnInfo(name = "skillBuilds")
    private String skillBuilds = "-+-+-+-+-";
    @ColumnInfo(name = "guideLastDay")
    private int guideLastDay = 0;

    public HeroModel(@NonNull String codeName, @NonNull String name) {
        this.codeName = codeName;
        this.name = name.replace("%27", "'");
    }

    public int getGuideLastDay() {
        return guideLastDay;
    }

    public void setGuideLastDay(int guideLastDay) {
        this.guideLastDay = guideLastDay;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSkillBuilds() {
        return skillBuilds;
    }

    public void setSkillBuilds(String skillBuilds) {
        this.skillBuilds = skillBuilds;
    }

    public String getLane() {
        return lane;
    }

    public void setLane(String lane) {
        this.lane = lane;
    }

    public String getFurtherItems() {
        return furtherItems;
    }

    public void setFurtherItems(String furtherItems) {
        this.furtherItems = furtherItems;
    }

    public String getStartingItems() {
        return startingItems;
    }

    public void setStartingItems(String startingItems) {
        this.startingItems = startingItems;
    }

    public String getBestVersus() {
        return bestVersus;
    }

    public void setBestVersus(String bestVersus) {
        this.bestVersus = bestVersus;
    }

    public String getWorstVersus() {
        return worstVersus;
    }

    public void setWorstVersus(String worstVersus) {
        this.worstVersus = worstVersus;
    }

    public String getWinrate() {
        return winrate;
    }

    public void setWinrate(String winrate) {
        this.winrate = winrate;
    }

    public String getPickrate() {
        return pickrate;
    }

    public void setPickrate(String pickrate) {
        this.pickrate = pickrate;
    }

    @NonNull
    public String getCodeName() {
        return codeName;
    }

    @NonNull
    public String getName() {
        return name;
    }

}
