package n7.ad2.heroes.db;

import android.app.Activity;
import android.arch.persistence.room.Entity;
import android.arch.persistence.room.PrimaryKey;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.view.View;

import n7.ad2.activity.HeroFullActivity;

import static n7.ad2.activity.HeroFullActivity.HERO_CODE_NAME;
import static n7.ad2.activity.HeroFullActivity.HERO_NAME;

@Entity
public class HeroModel {

    @PrimaryKey(autoGenerate = true)
    private int id;
    @NonNull
    private String codeName;
    @NonNull
    private String name;

    private String winrate = "-";
    private String pickrate = "-";
    private String lane = "-+-+-+-+-";
    private String time = "-+-+-+-+-";
    private String bestVersus = "-^-";
    private String worstVersus = "-^-";
    private String startingItems = "-+-+-+-+-";
    private String furtherItems = "-+-+-+-+-";
    private String skillBuilds = "-+-+-+-+-";

    public HeroModel(@NonNull String codeName, @NonNull String name) {
        this.codeName = codeName;
        this.name = name.replace("%27", "'");
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

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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


    public void startHeroFull(View view) {
        Intent intent = new Intent(view.getContext(), HeroFullActivity.class);
        ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), view, "iv");
        intent.putExtra(HERO_NAME, name);
        intent.putExtra(HERO_CODE_NAME, codeName);
        view.getContext().startActivity(intent, options.toBundle());
    }
}
