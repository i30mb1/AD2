package n7.ad2.games;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashSet;
import java.util.Random;

import jp.wasabeef.picasso.transformations.BlurTransformation;
import jp.wasabeef.picasso.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.picasso.transformations.gpu.ToonFilterTransformation;
import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.Utils;

import static n7.ad2.games.GameFragment.COUNT_FOR_5_DAYS_PREMIUM;
import static n7.ad2.games.GameFragment.SCORE_GAME1_GAME_FRAGMENT;

public class Game1HardActivity extends BaseActivity implements View.OnClickListener {

    private String rightMP;
    private ImageView iv_activity_game1_hard_spell, iv_activity_game1_hard_lvl2, iv_activity_game1_hard_lvl3, iv_activity_game1_hard_lvl4, iv_activity_game1_hard_heart2, iv_activity_game1_hard_heart1;
    private ImageView iv_activity_game1_hard_hero1, iv_activity_game1_hard_hero2, iv_activity_game1_hard_hero3, iv_activity_game1_hard_hero4, iv_activity_game1_hard_hero5;
    private LinearLayout ll_activity_game1_hard_heroes_row, ll_activity_game1_hard_spell_row2, ll_activity_game1_hard_spell_row1;
    private TextView
            tv_activity_game1_hard_mp1, tv_activity_game1_hard_mp2, tv_activity_game1_hard_mp3, tv_activity_game1_hard_mp4,
            tv_activity_game1_hard_mp5, tv_activity_game1_hard_mp6, tv_activity_game1_hard_mp7, tv_activity_game1_hard_mp8,
            tv_activity_game1_hard_score;
    private int lives = 3, score = 0, bestScore = 0, oldColor = 0;
    private AppExecutors appExecutors;
    private ProgressBar pb_activity_game1_hard;
    private int hero;
    private ImageView right_hero_iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);

        appExecutors = new AppExecutors();
        setContentView(R.layout.activity_game1_hard);

        showInstructionDialog();
        initViews();
        loadSpell();
    }

    private void showInstructionDialog() {
        if (MySharedPreferences.getSharedPreferences(this).getBoolean(getString(R.string.game1_hard_activity_tip_key), true)) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_info);
            final AlertDialog dialog = builder.show();
            TextView tv_dialog_tip = dialog.findViewById(R.id.tv_dialog_info);
            tv_dialog_tip.setText(R.string.game1_hard_activity_tip);
            tv_dialog_tip.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MySharedPreferences.getSharedPreferences(Game1HardActivity.this).edit().putBoolean(getString(R.string.game1_hard_activity_tip_key), false).apply();
                    dialog.dismiss();
                }
            });
        }
    }

    private void initViews() {
        bestScore = getIntent().getIntExtra(SCORE_GAME1_GAME_FRAGMENT, 0);
        pb_activity_game1_hard = findViewById(R.id.pb_activity_game1_hard);
        tv_activity_game1_hard_score = findViewById(R.id.tv_activity_game1_hard_score);
        iv_activity_game1_hard_spell = findViewById(R.id.iv_activity_game1_hard_spell);
        iv_activity_game1_hard_heart1 = findViewById(R.id.iv_activity_game1_hard_heart1);
        iv_activity_game1_hard_heart2 = findViewById(R.id.iv_activity_game1_hard_heart2);
        iv_activity_game1_hard_lvl2 = findViewById(R.id.iv_activity_game1_hard_lvl2);
        iv_activity_game1_hard_lvl3 = findViewById(R.id.iv_activity_game1_hard_lvl3);
        iv_activity_game1_hard_lvl4 = findViewById(R.id.iv_activity_game1_hard_lvl4);
        tv_activity_game1_hard_mp1 = findViewById(R.id.tv_activity_game1_hard_mp1);
        tv_activity_game1_hard_mp1.setOnClickListener(this);
        tv_activity_game1_hard_mp2 = findViewById(R.id.tv_activity_game1_hard_mp2);
        tv_activity_game1_hard_mp2.setOnClickListener(this);
        tv_activity_game1_hard_mp3 = findViewById(R.id.tv_activity_game1_hard_mp3);
        tv_activity_game1_hard_mp3.setOnClickListener(this);
        tv_activity_game1_hard_mp4 = findViewById(R.id.tv_activity_game1_hard_mp4);
        tv_activity_game1_hard_mp4.setOnClickListener(this);
        tv_activity_game1_hard_mp5 = findViewById(R.id.tv_activity_game1_hard_mp5);
        tv_activity_game1_hard_mp5.setOnClickListener(this);
        tv_activity_game1_hard_mp6 = findViewById(R.id.tv_activity_game1_hard_mp6);
        tv_activity_game1_hard_mp6.setOnClickListener(this);
        tv_activity_game1_hard_mp7 = findViewById(R.id.tv_activity_game1_hard_mp7);
        tv_activity_game1_hard_mp7.setOnClickListener(this);
        tv_activity_game1_hard_mp8 = findViewById(R.id.tv_activity_game1_hard_mp8);
        tv_activity_game1_hard_mp8.setOnClickListener(this);
        ll_activity_game1_hard_heroes_row = findViewById(R.id.ll_activity_game1_hard_heroes_row);
        ll_activity_game1_hard_spell_row1 = findViewById(R.id.ll_activity_game1_hard_spell_row1);
        ll_activity_game1_hard_spell_row2 = findViewById(R.id.ll_activity_game1_hard_spell_row2);
        iv_activity_game1_hard_hero1 = findViewById(R.id.iv_activity_game1_hard_hero1);
        iv_activity_game1_hard_hero2 = findViewById(R.id.iv_activity_game1_hard_hero2);
        iv_activity_game1_hard_hero3 = findViewById(R.id.iv_activity_game1_hard_hero3);
        iv_activity_game1_hard_hero4 = findViewById(R.id.iv_activity_game1_hard_hero4);
        iv_activity_game1_hard_hero5 = findViewById(R.id.iv_activity_game1_hard_hero5);
        for (ImageView iv : new ImageView[]{iv_activity_game1_hard_hero1, iv_activity_game1_hard_hero2, iv_activity_game1_hard_hero3, iv_activity_game1_hard_hero4, iv_activity_game1_hard_hero5})
            iv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    checkRightHeroImageView(v);
                }
            });
        oldColor = tv_activity_game1_hard_mp4.getCurrentTextColor();
    }

    private void checkRightHeroImageView(View v) {
        if (right_hero_iv == v) {
            MediaPlayer.create(Game1HardActivity.this, R.raw.chat).start();
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_green_light));
            ll_activity_game1_hard_spell_row2.animate().alpha(1.0f).setDuration(1000L).withStartAction(new Runnable() {
                @Override
                public void run() {
                    ll_activity_game1_hard_spell_row2.setVisibility(View.VISIBLE);
                }
            }).start();
            ll_activity_game1_hard_spell_row1.animate().alpha(1.0f).setDuration(1000L).withStartAction(new Runnable() {
                @Override
                public void run() {
                    ll_activity_game1_hard_spell_row1.setVisibility(View.VISIBLE);
                }
            }).start();
        } else {
            right_hero_iv = (ImageView) v;
            v.setBackgroundColor(getResources().getColor(android.R.color.holo_red_light));
            loseLive();
            pb_activity_game1_hard.setVisibility(View.VISIBLE);
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    loadSpell();
                }
            }, 1000);
        }
    }


    private void loadSpell() {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                if (right_hero_iv != null)
                    right_hero_iv.setBackgroundColor(0);
                ll_activity_game1_hard_spell_row2.animate().alpha(0.0f).setDuration(100L).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ll_activity_game1_hard_spell_row2.setVisibility(View.INVISIBLE);
                    }
                }).start();
                ll_activity_game1_hard_spell_row1.animate().alpha(0.0f).setDuration(100L).withEndAction(new Runnable() {
                    @Override
                    public void run() {
                        ll_activity_game1_hard_spell_row1.setVisibility(View.INVISIBLE);
                    }
                }).start();
                pb_activity_game1_hard.setVisibility(View.VISIBLE);
            }
        });
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    final String[] listHeroes = getAssets().list("heroes");
                    hero = new Random().nextInt(listHeroes.length);
                    JSONObject obj = new JSONObject(Utils.readJSONFromAsset(Game1HardActivity.this, "heroes/" + listHeroes[hero] + "/" + "ru_abilities.json"));
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
                    setHeroes(listHeroes[hero]);
                    setImageSpell(String.format("file:///android_asset/heroes/%s/%s.webp", listHeroes[hero], String.valueOf(spell + 1)));

                } catch (IOException e) {
                    loadSpell();
                } catch (JSONException e) {
                    e.printStackTrace();
                } finally {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            pb_activity_game1_hard.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void setHeroes(final String rightHero) {
        appExecutors.diskIO().execute(new Runnable() {

            private HashSet<String> itemsHeroes;

            @Override
            public void run() {
                try {
                    final String[] listHeroes = getAssets().list("heroes");
                    itemsHeroes = new HashSet<>();
                    itemsHeroes.add(rightHero);
                    do {
                        String hero = listHeroes[new Random().nextInt(listHeroes.length)];
                        itemsHeroes.add(hero);
                    } while (itemsHeroes.size() != 5);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                appExecutors.mainThread().execute(new Runnable() {
                    @Override
                    public void run() {
                        int count = 0;
                        for (String itemsHero : itemsHeroes) {
                            if (count == 0) {
                                if (itemsHero.equals(rightHero))
                                    right_hero_iv = iv_activity_game1_hard_hero1;
                                Picasso.get().load(String.format("file:///android_asset/heroes/%s/mini.webp", itemsHero)).placeholder(R.drawable.spell_placeholder).error(R.drawable.spell_placeholder_error).into(iv_activity_game1_hard_hero1);
                            }
                            if (count == 1) {
                                if (itemsHero.equals(rightHero))
                                    right_hero_iv = iv_activity_game1_hard_hero2;
                                Picasso.get().load(String.format("file:///android_asset/heroes/%s/mini.webp", itemsHero)).placeholder(R.drawable.spell_placeholder).error(R.drawable.spell_placeholder_error).into(iv_activity_game1_hard_hero2);
                            }
                            if (count == 2) {
                                if (itemsHero.equals(rightHero))
                                    right_hero_iv = iv_activity_game1_hard_hero3;
                                Picasso.get().load(String.format("file:///android_asset/heroes/%s/mini.webp", itemsHero)).placeholder(R.drawable.spell_placeholder).error(R.drawable.spell_placeholder_error).into(iv_activity_game1_hard_hero3);
                            }
                            if (count == 3) {
                                if (itemsHero.equals(rightHero))
                                    right_hero_iv = iv_activity_game1_hard_hero4;
                                Picasso.get().load(String.format("file:///android_asset/heroes/%s/mini.webp", itemsHero)).placeholder(R.drawable.spell_placeholder).error(R.drawable.spell_placeholder_error).into(iv_activity_game1_hard_hero4);
                            }
                            if (count == 4) {
                                if (itemsHero.equals(rightHero))
                                    right_hero_iv = iv_activity_game1_hard_hero5;
                                Picasso.get().load(String.format("file:///android_asset/heroes/%s/mini.webp", itemsHero)).placeholder(R.drawable.spell_placeholder).error(R.drawable.spell_placeholder_error).into(iv_activity_game1_hard_hero5);
                            }
                            count++;
                        }
                    }
                });
            }
        });
    }

    private void setImageSpell(final String format) {
        appExecutors.mainThread().execute(new Runnable() {
            @Override
            public void run() {
                switch (new Random().nextInt(4)) {
                    case 0:
                        Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).transform(new SketchFilterTransformation(Game1HardActivity.this)).into(iv_activity_game1_hard_spell);
                        break;
                    case 1:
                        Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).transform(new ToonFilterTransformation(Game1HardActivity.this)).into(iv_activity_game1_hard_spell);
                        break;
                    case 2:
                        Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).transform(new PixelationFilterTransformation(Game1HardActivity.this)).into(iv_activity_game1_hard_spell);
                        break;
                    case 3:
                        Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).transform(new BlurTransformation(Game1HardActivity.this)).into(iv_activity_game1_hard_spell);
                        break;
                    case 4:
                        Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).transform(new InvertFilterTransformation(Game1HardActivity.this)).into(iv_activity_game1_hard_spell);
                        break;
                }
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
                } while (itemsMp.size() != 8);

                int count = 0;
                for (int i : itemsMp) {
                    if (count == 0)
                        prepareTextViewMp(tv_activity_game1_hard_mp1, i);
                    if (count == 1)
                        prepareTextViewMp(tv_activity_game1_hard_mp2, i);
                    if (count == 2)
                        prepareTextViewMp(tv_activity_game1_hard_mp3, i);
                    if (count == 3)
                        prepareTextViewMp(tv_activity_game1_hard_mp4, i);
                    if (count == 4)
                        prepareTextViewMp(tv_activity_game1_hard_mp5, i);
                    if (count == 5)
                        prepareTextViewMp(tv_activity_game1_hard_mp6, i);
                    if (count == 6)
                        prepareTextViewMp(tv_activity_game1_hard_mp7, i);
                    if (count == 7)
                        prepareTextViewMp(tv_activity_game1_hard_mp8, i);
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
                iv_activity_game1_hard_lvl2.setVisibility(View.GONE);
                iv_activity_game1_hard_lvl3.setVisibility(View.GONE);
                iv_activity_game1_hard_lvl4.setVisibility(View.GONE);
                switch (i) {
                    case 3:
                        iv_activity_game1_hard_lvl4.setVisibility(View.VISIBLE);
                    case 2:
                        iv_activity_game1_hard_lvl3.setVisibility(View.VISIBLE);
                    case 1:
                        iv_activity_game1_hard_lvl2.setVisibility(View.VISIBLE);
                        break;
                }
            }
        });
    }

    private void loseLive() {
        if (lives == 1) {
            if (bestScore < score) {
                if (score > COUNT_FOR_5_DAYS_PREMIUM) {
                    MediaPlayer.create(Game1HardActivity.this, R.raw.you_are_the_most_sucsesful).start();
                } else {
                    MediaPlayer.create(Game1HardActivity.this, R.raw.you_are_sucseded_where_other_did_not).start();
                }
            } else {
                MediaPlayer.create(Game1HardActivity.this, R.raw.critical_faulire).start();
            }
            Bundle bundle = new Bundle();
            bundle.putInt(SCORE_GAME1_GAME_FRAGMENT, score);
            Intent intent = new Intent();
            intent.putExtras(bundle);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            MediaPlayer.create(Game1HardActivity.this, R.raw.no).start();
            removeOneLive();
        }
    }

    private void removeOneLive() {
        lives = lives - 1;
        switch (lives) {
            case 2:
                iv_activity_game1_hard_heart1.setVisibility(View.INVISIBLE);
                break;
            case 1:
                iv_activity_game1_hard_heart2.setVisibility(View.INVISIBLE);
                break;
        }
    }

    @Override
    public void onClick(final View v) {
        if (((TextView) v).getText().equals(rightMP)) {
            MediaPlayer.create(Game1HardActivity.this, R.raw.yes).start();
            score = score + 2;
            tv_activity_game1_hard_score.setText(String.valueOf(score));
            ((TextView) v).setTextColor(getResources().getColor(android.R.color.holo_green_light));
            v.setClickable(false);
        } else {
            ((TextView) v).setTextColor(getResources().getColor(android.R.color.holo_red_light));
            v.setClickable(false);
            loseLive();
        }
        pb_activity_game1_hard.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loadSpell();
            }
        }, 1000);

    }

}
