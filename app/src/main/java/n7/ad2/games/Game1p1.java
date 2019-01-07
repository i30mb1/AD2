package n7.ad2.games;

import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.view.View;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.ad2.R;
import n7.ad2.databinding.ActivityGame1p1Binding;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.Utils;


public class Game1p1 extends BaseActivity {

    public ObservableInt secondRemaining = new ObservableInt(30);
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    public ObservableField<String> slot1 = new ObservableField<>();
    public ObservableField<String> slot2 = new ObservableField<>();
    public ObservableField<String> slot3 = new ObservableField<>();
    public ObservableField<String> slot4 = new ObservableField<>();
    public ObservableInt currentSpellLVL = new ObservableInt(1);
    public ObservableInt score = new ObservableInt(0);
    public ObservableBoolean lock = new ObservableBoolean(true);
    private String rightAnswerString;
    private Executor diskIO;
    private int hero;
    private ActivityGame1p1Binding binding;
    private SoundPool soundPool;
    private int soundYes;
    private int soundNo;
    private int more1;
    private int more2;
    private int more3;
    private int more4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        diskIO = Executors.newSingleThreadExecutor();
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game1p1);
        binding.setActivity(this);
        binding.executePendingBindings();

        loadPlayer();
        startCountDownTimer();
        startGame();
    }

    private void loadPlayer() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundYes = soundPool.load(this, R.raw.yes, 1);
        soundNo = soundPool.load(this, R.raw.no, 1);
        more1 = soundPool.load(this, R.raw.you_are_sucseded_where_other_did_not, 1);
        more2 = soundPool.load(this, R.raw.you_are_the_most_sucsesful, 1);
        more3 = soundPool.load(this, R.raw.does_this_unit_have_a_soul, 1);
        more4 = soundPool.load(this, R.raw.yes_it_does, 1);
    }

    private void startCountDownTimer() {
        new CountDownTimer(30000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondRemaining.set((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                secondRemaining.set(0);
            }
        }.start();
    }

    private void startGame() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    isLoading.set(true);
                    final String[] listHeroes = getAssets().list("heroes");
                    hero = new Random().nextInt(listHeroes.length);
                    JSONArray spells = new JSONObject(Utils.readJSONFromAsset(Game1p1.this, "heroes/" + listHeroes[hero] + "/" + "ru_abilities.json")).getJSONArray("abilities");

                    final int randomSpell = new Random().nextInt(spells.length());
                    //загружаем ману для способности и уровень способности
                    String spellManaCostString = spells.getJSONObject(randomSpell).get("mana").toString();
                    if (spellManaCostString.equals("0") || spellManaCostString.contains("(") || spellManaCostString.contains("+") || spellManaCostString.contains("%")) {
                        throw new IOException();
                    }

                    int spellLVL = new Random().nextInt(spellManaCostString.split("/").length);
                    String spellManaPoints = spellManaCostString.split("/")[spellLVL];

                    setLVL(spellLVL);
                    setMp(spellManaPoints);
                    setImageSpell(String.format("file:///android_asset/heroes/%s/%s.webp", listHeroes[hero], String.valueOf(randomSpell + 1)));

                } catch (IOException e) {
                    startGame();
                } catch (JSONException e) {
                    startGame();
                } finally {
                    isLoading.set(false);
                    lock.set(false);
                }
            }
        });
    }

    private void setImageSpell(final String format) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(format).error(R.drawable.spell_placeholder_error).placeholder(R.drawable.spell_placeholder).into(binding.ivActivityGame1P1Spell);
            }
        });
    }

    private void setMp(final String manaPoints) {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                int[] numbersDiff = new int[]{-30, -25, -20, -15, -10, -5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
                rightAnswerString = manaPoints;
                int rightAnswerInt = Integer.valueOf(manaPoints);
                LinkedList<Integer> listWithValues = new LinkedList<>();
                listWithValues.add(rightAnswerInt);
                do {
                    int minus = rightAnswerInt + numbersDiff[new Random().nextInt(numbersDiff.length)];
                    if (minus > 0) listWithValues.add(minus);
                } while (listWithValues.size() != 4);

                slot1.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
                slot2.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
                slot3.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
                slot4.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
            }
        });
    }

    private void setLVL(final int i) {
        currentSpellLVL.set(i);
    }

    public void check(View view) {
        lock.set(true);
        boolean isRight;
        if (((TextView) view).getText().equals(rightAnswerString)) {
            ((TextView) view).setTextColor(getResources().getColor(android.R.color.holo_green_light));
            isRight = true;
            score.set(score.get() + 1);
        } else {
            ((TextView) view).setTextColor(getResources().getColor(android.R.color.holo_red_light));
            score.set(score.get() - 1);
            isRight = false;
        }
        playSound(isRight);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startGame();
            }
        }, 500);
    }

    private void playSound(boolean isRight) {
        int hz = score.get();
        if (score.get() > 9) soundPool.play(more4, 0.6F, 0.6F, 0, 0, 1F);
        if (score.get() == 8) soundPool.play(more3, 0.6F, 0.6F, 0, 0, 1F);
        if (score.get() == 6) soundPool.play(more2, 0.6F, 0.6F, 0, 0, 1F);
        if (score.get() == 4) soundPool.play(more1, 0.6F, 0.6F, 0, 0, 1F);
        if (hz < 9 && hz != 8 && hz != 6 && hz != 4) {
            if (isRight) {
                soundPool.play(soundYes, 0.6F, 0.6F, 0, 0, 1F);
            } else {
                soundPool.play(soundNo, 0.6F, 0.6F, 0, 0, 1F);
            }
        }
    }


}
