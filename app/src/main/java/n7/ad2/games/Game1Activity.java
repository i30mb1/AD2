package n7.ad2.games;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.Utils;

import static n7.ad2.games.GameFragment.COUNT_FOR_5_DAYS_PREMIUM;
import static n7.ad2.games.GameFragment.SCORE_GAME1_GAME_FRAGMENT;


public class Game1Activity extends BaseActivity implements View.OnClickListener {

    private String rightMP;
    private ImageView iv_activity_game1_spell, iv_activity_game1_lvl2, iv_activity_game1_lvl3, iv_activity_game1_lvl4, iv_activity_game1_heart2, iv_activity_game1_heart1;
    private TextView tv_activity_game1_mp1, tv_activity_game1_mp2, tv_activity_game1_mp3, tv_activity_game1_mp4, tv_activity_game1_score;
    private int lives = 3, score = 0, bestScore = 0, oldColor = 0;
    private AppExecutors appExecutors;
    private ProgressBar pb_activity_game1;
    private int hero;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        appExecutors = new AppExecutors();
        setContentView(R.layout.activity_game1);

        showInstructionDialog();
        initViews();
        loadSpell();
    }

    private void showInstructionDialog() {
        if (MySharedPreferences.getSharedPreferences(this).getBoolean(getString(R.string.game1_activity_tip_key), true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_info);
            final AlertDialog dialog = builder.show();
            TextView tv_dialog_tip = dialog.findViewById(R.id.tv_dialog_info);
            if (tv_dialog_tip != null)
                tv_dialog_tip.setText(R.string.game1_activity_tip);
            if (tv_dialog_tip != null)
                tv_dialog_tip.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        MySharedPreferences.getSharedPreferences(Game1Activity.this).edit().putBoolean(getString(R.string.game1_activity_tip_key), false).apply();
                        dialog.dismiss();
                    }
                });
        }
    }

    private void initViews() {
        bestScore = getIntent().getIntExtra(SCORE_GAME1_GAME_FRAGMENT, 0);
        pb_activity_game1 = findViewById(R.id.pb_activity_game1);
        tv_activity_game1_score = findViewById(R.id.tv_activity_game1_score);
        iv_activity_game1_spell = findViewById(R.id.iv_activity_game1_spell);
        iv_activity_game1_heart1 = findViewById(R.id.iv_activity_game1_heart1);
        iv_activity_game1_heart2 = findViewById(R.id.iv_activity_game1_heart2);
        iv_activity_game1_lvl2 = findViewById(R.id.iv_activity_game1_lvl2);
        iv_activity_game1_lvl3 = findViewById(R.id.iv_activity_game1_lvl3);
        iv_activity_game1_lvl4 = findViewById(R.id.iv_activity_game1_lvl4);
        tv_activity_game1_mp1 = findViewById(R.id.tv_activity_game1_mp1);
        tv_activity_game1_mp1.setOnClickListener(this);
        tv_activity_game1_mp2 = findViewById(R.id.tv_activity_game1_mp2);
        tv_activity_game1_mp2.setOnClickListener(this);
        tv_activity_game1_mp3 = findViewById(R.id.tv_activity_game1_mp3);
        tv_activity_game1_mp3.setOnClickListener(this);
        tv_activity_game1_mp4 = findViewById(R.id.tv_activity_game1_mp4);
        tv_activity_game1_mp4.setOnClickListener(this);
        oldColor = tv_activity_game1_mp4.getCurrentTextColor();
    }


    private void loadSpell() {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                pb_activity_game1.setVisibility(View.VISIBLE);
            }
        });
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String[] listHeroes = getAssets().list("heroes");
                    hero = new Random().nextInt(listHeroes.length);
                    JSONObject obj = new JSONObject(Utils.readJSONFromAsset(Game1Activity.this, "heroes/" + listHeroes[hero] + "/" + "ru_abilities.json"));
                    JSONArray arraySpells = obj.getJSONArray("abilities");
                    final int spell = new Random().nextInt(arraySpells.length());

                    //загружаем ману для способности и уровень способности
                    String spellCosts = arraySpells.getJSONObject(spell).get("mana").toString();
                    if (spellCosts.equals("0") || spellCosts.contains("(") || spellCosts.contains("+") || spellCosts.contains("%"))
                        throw new IOException();

                    String[] spells = spellCosts.split("/");
                    int spellLevel = new Random().nextInt(spells.length);
                    String spellCost = spells[spellLevel];

                    setLVL(spellLevel);
                    setMp(spellCost);
                    setImageSpell(String.format("file:///android_asset/heroes/%s/%s.webp", listHeroes[hero], String.valueOf(spell + 1)));

                } catch (IOException e) {
                    loadSpell();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            pb_activity_game1.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void setImageSpell(final String format) {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).into(iv_activity_game1_spell);
            }
        });
    }


    private void setMp(final String s) {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                int[] num = new int[]{-30, -25, -20, -15, -10, -5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
                rightMP = s;
                int rightMP = Integer.valueOf(s);
                HashSet<Integer> itemsMp = new HashSet<>();
                itemsMp.add(rightMP);
                do {
                    int minus = rightMP + num[new Random().nextInt(num.length)];
                    if (minus > 0)
                        itemsMp.add(minus);
                } while (itemsMp.size() != 4);

                int count = 0;
                for (int i : itemsMp) {
                    if (count == 0)
                        prepareTextViewMp(tv_activity_game1_mp1, i);
                    if (count == 1)
                        prepareTextViewMp(tv_activity_game1_mp2, i);
                    if (count == 2)
                        prepareTextViewMp(tv_activity_game1_mp3, i);
                    if (count == 3)
                        prepareTextViewMp(tv_activity_game1_mp4, i);
                    count++;
                }
            }
        });
    }

    private void prepareTextViewMp(final TextView tv, final int i) {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                tv.setTextColor(oldColor);
                tv.setClickable(true);
                tv.setText(String.valueOf(i));
            }
        });
    }

    private void setLVL(final int i) {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                iv_activity_game1_lvl2.setVisibility(View.GONE);
                iv_activity_game1_lvl3.setVisibility(View.GONE);
                iv_activity_game1_lvl4.setVisibility(View.GONE);
                switch (i) {
                    case 3:
                        iv_activity_game1_lvl4.setVisibility(View.VISIBLE);
                    case 2:
                        iv_activity_game1_lvl3.setVisibility(View.VISIBLE);
                    case 1:
                        iv_activity_game1_lvl2.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void loseLive() {
        if (lives == 1) {
            if (bestScore < score) {
                if (score > COUNT_FOR_5_DAYS_PREMIUM) {
                    MediaPlayer.create(Game1Activity.this, R.raw.you_are_the_most_sucsesful).start();
                } else {
                    MediaPlayer.create(Game1Activity.this, R.raw.you_are_sucseded_where_other_did_not).start();
                }
            } else {
                MediaPlayer.create(Game1Activity.this, R.raw.critical_faulire).start();
            }
            Bundle bundle = new Bundle();
            bundle.putInt(SCORE_GAME1_GAME_FRAGMENT, score);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            MediaPlayer.create(Game1Activity.this, R.raw.no).start();
            removeOneLive();
        }
    }

    private void removeOneLive() {
        lives = lives - 1;
        switch (lives) {
            case 2:
                iv_activity_game1_heart1.setVisibility(View.INVISIBLE);
                break;
            case 1:
                iv_activity_game1_heart2.setVisibility(View.INVISIBLE);
                break;
        }

    }

    @Override
    public void onClick(final View v) {
        if (((TextView) v).getText().equals(rightMP)) {
            MediaPlayer.create(Game1Activity.this, R.raw.yes).start();
            score = score + 1;
            tv_activity_game1_score.setText(String.valueOf(score));
            ((TextView) v).setTextColor(getResources().getColor(android.R.color.holo_green_light));
            v.setClickable(false);
        } else {
            ((TextView) v).setTextColor(getResources().getColor(android.R.color.holo_red_light));
            v.setClickable(false);
            loseLive();
        }
        pb_activity_game1.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadSpell();
            }
        }, 1000);

    }

}
