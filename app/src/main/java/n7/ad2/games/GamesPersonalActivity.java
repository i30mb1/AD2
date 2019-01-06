package n7.ad2.games;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import n7.ad2.tournaments.TournamentsViewModel;
import n7.ad2.utils.AppExecutors;
import n7.ad2.R;
import n7.ad2.utils.BaseActivity;
import n7.ad2.tournaments.db.Games;

public class GamesPersonalActivity extends BaseActivity {

    public static final String URL = "URL";
    LinearLayout tv_activity_games_personal_holder;
    private Toolbar toolbar;
    private ImageView tv_activity_games_personal_team1_logo;
    private ImageView tv_activity_games_personal_team2_logo;
    private TextView tv_activity_games_personal_team1_name;
    private TextView tv_activity_games_personal_team2_name;
    private TextView tv_activity_games_personal_middle;
    private AppExecutors appExecutors;
    private String url;
    private ProgressBar pb_activity_games_personal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_games_personal);
        appExecutors = new AppExecutors();

        if (savedInstanceState == null) {
            setToolbar();
            initLoadedViews();
            loadingHeroes();
        } else {
            finish();
        }
    }

    private void loadingHeroes() {
        pb_activity_games_personal.setVisibility(View.VISIBLE);
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document documentSimple = Jsoup.connect(url).get();
                    Elements games = documentSimple.getElementsByClass("esport-match-view-map-single esport-tabs-content clearfix");
                    for (Element game : games) {
                        if (game.children().size() < 2) continue;
                        final View gameView = getLayoutInflater().inflate(R.layout.item_game_submatch, null, false);
                        LinearLayout ll_item_left_picks = gameView.findViewById(R.id.ll_item_left_picks);
                        LinearLayout ll_item_left_bans = gameView.findViewById(R.id.ll_item_left_bans);
                        LinearLayout ll_item_left_bans2 = gameView.findViewById(R.id.ll_item_left_bans2);
                        LinearLayout ll_item_right_picks = gameView.findViewById(R.id.ll_item_right_picks);
                        LinearLayout ll_item_right_bans = gameView.findViewById(R.id.ll_item_right_bans);
                        LinearLayout ll_item_right_bans2 = gameView.findViewById(R.id.ll_item_right_bans2);

                        Elements team1picks = game.child(0).getElementsByClass("esport-match-view-map-single-side-picks-single clearfix");
                        if (team1picks.size() != 0)
                            for (Element team1pick : team1picks) {
                                View pick = getLayoutInflater().inflate(R.layout.item_game_submatch_pick, null, false);
                                TextView tv_player = pick.findViewById(R.id.tv_player);
                                TextView tv_hero = pick.findViewById(R.id.tv_hero);
                                final ImageView iv = pick.findViewById(R.id.iv);
                                if (team1pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").size() > 0) {
                                    String player = team1pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").get(0).child(0).text().trim();
                                    tv_player.setText(player);
                                }
                                if (team1pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").size() > 0) {
                                    final String hero = team1pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").get(0).child(2).text().trim();
                                    tv_hero.setText(hero);
                                    appExecutors.mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Picasso.get().load("file:///android_asset/heroes/" + hero.toLowerCase().trim().replace(" ", "_") + "/full.webp")
                                                    .placeholder(R.drawable.hero_placeholder).error(R.drawable.hero_placeholder_error)
                                                    .into(iv);
                                        }
                                    });
                                }
                                ll_item_left_picks.addView(pick);
                            }

                        Elements team2picks = game.child(2).getElementsByClass("esport-match-view-map-single-side-picks-single clearfix");
                        if (team2picks.size() != 0)
                            for (Element team2pick : team2picks) {
                                View pick = getLayoutInflater().inflate(R.layout.item_game_submatch_pick_reverse, null, false);
                                TextView tv_player = pick.findViewById(R.id.tv_player);
                                TextView tv_hero = pick.findViewById(R.id.tv_hero);
                                final ImageView iv = pick.findViewById(R.id.iv);
                                if (team2pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").size() > 0) {
                                    String player = team2pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").get(0).child(0).text().trim();
                                    tv_player.setText(player);
                                }
                                if (team2pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").size() > 0) {
                                    final String hero = team2pick.getElementsByClass("esport-match-view-map-single-side-picks-single-info").get(0).child(2).text().trim();
                                    tv_hero.setText(hero);
                                    appExecutors.mainThread().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            Picasso.get().load("file:///android_asset/heroes/" + hero.toLowerCase().replace(" ", "_") + "/full.webp")
                                                    .placeholder(R.drawable.hero_placeholder).error(R.drawable.hero_placeholder_error)
                                                    .into(iv);
                                        }
                                    });
                                }
                                ll_item_right_picks.addView(pick);
                            }
                        Elements team1bans = game.child(0).getElementsByClass("esport-match-view-map-single-side-bans-single");
                        int count = 0;
                        if (team1bans.size() != 0)
                            for (Element team1ban : team1bans) {
                                View ban = getLayoutInflater().inflate(R.layout.item_game_submatch_ban, null, false);
                                final ImageView iv = ban.findViewById(R.id.iv);
                                final String hero = team1ban.child(0).attr("title").toLowerCase().trim().replace(" ", "_");
                                appExecutors.mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Picasso.get().load("file:///android_asset/heroes/" + hero + "/full.webp").transform(new GrayscaleTransformation())
                                                .placeholder(R.drawable.hero_placeholder).error(R.drawable.hero_placeholder_error)
                                                .into(iv);
                                    }
                                });
                                if (count < 3)
                                    ll_item_left_bans.addView(ban);
                                else
                                    ll_item_left_bans2.addView(ban);
                                count++;
                            }
                        Elements team2bans = game.child(2).getElementsByClass("esport-match-view-map-single-side-bans-single");
                        if (team2bans.size() != 0)
                            for (Element team2ban : team2bans) {
                                View ban = getLayoutInflater().inflate(R.layout.item_game_submatch_ban, null, false);
                                final ImageView iv = ban.findViewById(R.id.iv);
                                final String hero = team2ban.child(0).attr("title").toLowerCase().trim().replace(" ", "_");
                                appExecutors.mainThread().execute(new Runnable() {
                                    @Override
                                    public void run() {
                                        Picasso.get().load("file:///android_asset/heroes/" + hero + "/full.webp").transform(new GrayscaleTransformation())
                                                .placeholder(R.drawable.hero_placeholder).error(R.drawable.hero_placeholder_error)
                                                .into(iv);
                                    }
                                });
                                if (count < 9) {
                                    ll_item_right_bans.addView(ban);
                                } else {
                                    ll_item_right_bans2.addView(ban);
                                }
                                count++;
                            }
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
                                tv_activity_games_personal_holder.addView(gameView);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            pb_activity_games_personal.setVisibility(View.GONE);
                        }
                    });

                }
            }
        });
    }

    private void initLoadedViews() {
        url = getIntent().getStringExtra(URL);
        pb_activity_games_personal = findViewById(R.id.pb_activity_games_personal);
        tv_activity_games_personal_team1_logo = findViewById(R.id.tv_activity_games_personal_team1_logo);
        tv_activity_games_personal_team2_logo = findViewById(R.id.tv_activity_games_personal_team2_logo);
        tv_activity_games_personal_team1_name = findViewById(R.id.tv_item_list_games_team1_name);
        tv_activity_games_personal_team2_name = findViewById(R.id.tv_activity_games_personal_team2_name);
        tv_activity_games_personal_middle = findViewById(R.id.tv_activity_games_personal_middle);
        tv_activity_games_personal_holder = findViewById(R.id.tv_activity_games_personal_holder);

        TournamentsViewModel gamesViewModel = ViewModelProviders.of(this).get(TournamentsViewModel.class);
//        gamesViewModel.getGameByUrl(url).observe(this, new Observer<Games>() {
//            @Override
//            public void onChanged(@Nullable Games games) {
//                if (games != null) {
//                    Picasso.get().load(games.team1Logo).placeholder(R.drawable.games_unknown_team1).into(tv_activity_games_personal_team1_logo);
//                    Picasso.get().load(games.team2Logo).placeholder(R.drawable.games_unknown_team2).into(tv_activity_games_personal_team2_logo);
//                    tv_activity_games_personal_team1_name.setText(games.team1Name);
//                    tv_activity_games_personal_team2_name.setText(games.team2Name);
//                    tv_activity_games_personal_middle.setText(games.teamScore);
//                }
//            }
//        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.tournaments));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

}
