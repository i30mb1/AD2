package n7.ad2.tournaments;

import android.arch.lifecycle.Observer;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import jp.wasabeef.picasso.transformations.GrayscaleTransformation;
import n7.ad2.R;
import n7.ad2.databinding.ActivityGameFullBinding;
import n7.ad2.tournaments.db.GamesDao;
import n7.ad2.tournaments.db.GamesRoomDatabase;
import n7.ad2.tournaments.db.TournamentGame;
import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.BaseActivity;

public class GameFullActivity extends BaseActivity {

    public static final String URL = "URL";

    private AppExecutors appExecutors;
    private String url;
    private ActivityGameFullBinding binding;
    public ObservableBoolean isLoading = new ObservableBoolean(false);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game_full);
        binding.setActivity(this);
        binding.executePendingBindings();

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
        isLoading.set(true);
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    Document documentSimple = Jsoup.connect(url).get();
                    Elements games = documentSimple.getElementsByClass("esport-match-view-map-single esport-tabs-content clearfix");
                    for (Element game : games) {
                        if (game.children().size() < 2) continue;
                        final View gameView = getLayoutInflater().inflate(R.layout.item_game_submatch, binding.tvActivityGamesPersonalHolder, false);
                        LinearLayout ll_item_left_picks = gameView.findViewById(R.id.ll_item_left_picks);
                        LinearLayout ll_item_left_bans = gameView.findViewById(R.id.ll_item_left_bans);
                        LinearLayout ll_item_left_bans2 = gameView.findViewById(R.id.ll_item_left_bans2);
                        LinearLayout ll_item_right_picks = gameView.findViewById(R.id.ll_item_right_picks);
                        LinearLayout ll_item_right_bans = gameView.findViewById(R.id.ll_item_right_bans);
                        LinearLayout ll_item_right_bans2 = gameView.findViewById(R.id.ll_item_right_bans2);

                        Elements team1picks = game.child(0).getElementsByClass("esport-match-view-map-single-side-picks-single clearfix");
                        if (team1picks.size() != 0)
                            for (Element team1pick : team1picks) {
                                View pick = getLayoutInflater().inflate(R.layout.item_game_submatch_pick, ll_item_left_picks, false);
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
                                View pick = getLayoutInflater().inflate(R.layout.item_game_submatch_pick_reverse, ll_item_right_picks, false);
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
                                View ban = getLayoutInflater().inflate(R.layout.item_game_submatch_ban, ll_item_left_bans, false);
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
                                View ban = getLayoutInflater().inflate(R.layout.item_game_submatch_ban, ll_item_right_bans, false);
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
                                binding.tvActivityGamesPersonalHolder.addView(gameView);
                            }
                        });
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    isLoading.set(false);
                }
            }
        });
    }

    private void initLoadedViews() {
        url = getIntent().getStringExtra(URL);
        GamesDao gamesDao = GamesRoomDatabase.getDatabase(this).gamesDao();
        gamesDao.getGameByUrl(url).observe(this, new Observer<TournamentGame>() {
            @Override
            public void onChanged(@Nullable TournamentGame games) {
                if (games != null) {
                    Picasso.get().load(games.team1Logo).placeholder(R.drawable.game_unknown_team1).into(binding.ivActivityGameFullTeam1);
                    Picasso.get().load(games.team2Logo).placeholder(R.drawable.game_unknown_team2).into(binding.ivActivityGameFullTeam2);
                    binding.tvActivityGameFullTeam1.setText(games.team1Name);
                    binding.tvActivityGameFullTeam2.setText(games.team2Name);
                    binding.tvActivityGameFullScore.setText(games.teamScore);
                }
            }
        });
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
        binding.toolbarActivityGameFull.setTitle(getString(R.string.tournaments));
        setSupportActionBar(binding.toolbarActivityGameFull);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

}
