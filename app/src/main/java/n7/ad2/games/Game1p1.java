package n7.ad2.games;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.text.TextUtilsCompat;
import android.support.v4.widget.TextViewCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.AnimationSet;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;
import android.view.animation.ScaleAnimation;
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


public class Game1p1 extends AppCompatActivity {

    public ObservableInt secondRemaining = new ObservableInt(30);
    public ObservableBoolean isLoading = new ObservableBoolean(true);
    public ObservableField<String> slot1 = new ObservableField<>();
    public ObservableField<String> slot2 = new ObservableField<>();
    public ObservableField<String> slot3 = new ObservableField<>();
    public ObservableField<String> slot4 = new ObservableField<>();
    public ObservableInt currentSpellLVL = new ObservableInt(1);
    public ObservableInt score = new ObservableInt(0);
    public ObservableBoolean lock = new ObservableBoolean(true);
    public ObservableField<String> endText = new ObservableField<>();
    private String rightAnswerString;
    private Executor diskIO;
    private int hero;
    private ActivityGame1p1Binding binding;
    private SoundPool soundPool;
    private int soundYes;
    private int soundNo;
    private int you_are_succeeded_where_other_did_not;
    private int you_are_the_most_successful;
    private int does_this_unit_have_a_soul;
    private int yes_it_does;

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
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
        soundYes = soundPool.load(this, R.raw.yes, 1);
        soundNo = soundPool.load(this, R.raw.no, 1);
        you_are_succeeded_where_other_did_not = soundPool.load(this, R.raw.you_are_succeeded_where_other_did_not, 1);
        you_are_the_most_successful = soundPool.load(this, R.raw.you_are_the_most_successful, 1);
        does_this_unit_have_a_soul = soundPool.load(this, R.raw.does_this_unit_have_a_soul, 1);
        yes_it_does = soundPool.load(this, R.raw.yes_it_does, 1);
    }

    private void startCountDownTimer() {
        new CountDownTimer(30000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondRemaining.set((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                secondRemaining.set(0);
                showResult();
            }
        }.start();
    }

    private void showResult() {
        lock.set(true);
        isLoading.set(false);
        setEndText();
        changeBackgroundColor();
        startConstraintAnimation();
    }

    private void startConstraintAnimation() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_game1p1_finish);

        TransitionSet transitionSet = new TransitionSet()
                .addTransition(new ChangeBounds().setInterpolator(new LinearInterpolator())
                        .addTarget(binding.linearLayout)
                        .addTarget(binding.tvActivityGame1Timer)
                        .addTarget(binding.tvActivityGame1Score)
                        .setDuration(1000))
                .addTransition(new Fade(Fade.OUT).setDuration(500)
                        .addTarget(binding.ivActivityGame1P1Spell)
                        .addTarget(binding.linearLayout2))
                .addTransition(new ChangeBounds().setInterpolator(new BounceInterpolator())
                        .addTarget(binding.textView)
                        .setStartDelay(1000)
                        .setDuration(1000));

        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), transitionSet);
        constraintSet.applyTo((ConstraintLayout) binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 6000);
    }

    private void changeBackgroundColor() {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getColorAccentTheme(), getResources().getColor(android.R.color.black));
        colorAnimation.setDuration(1000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                binding.getRoot().setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    private int getColorAccentTheme() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
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

    public void expandAnimation(View view) {
        AnimationSet animationSet = new AnimationSet(false);
        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 3f, 1f, 3f, view.getWidth() / 2, view.getHeight() / 2);
        animationSet.addAnimation(scaleAnimation);
        animationSet.addAnimation(alphaAnimation);
        animationSet.setDuration(200);
        view.startAnimation(animationSet);
    }

    public void check(View view) {
        lock.set(true);
        expandAnimation(view);
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
        }, 300);
    }

    private void playSound(boolean isRight) {
        if (isRight) {
            soundPool.play(soundYes, 0.6F, 0.6F, 0, 0, 1F);
        } else {
            soundPool.play(soundNo, 0.6F, 0.6F, 0, 0, 1F);
        }
    }

    private void setEndText() {
        switch (score.get()) {
            case -6:
                endText.set("SUCK MY DATA, HUMAN!");
                break;
            case -5:
                endText.set("If our lives are already written, it would take a courageous man to change the script.");
                break;
            case -4:
                endText.set("Stand in the ashes of a trillion dead souls and ask the ghosts if honor matters. Their silence is your answer.");
                break;
            case -3:
                endText.set("You are great in bed ;) …. You can sleep for days.");
                break;
            case -2:
                endText.set("You didn’t fall. The floor just needed a hug.");
                break;
            case -1:
                endText.set("SKELETON_KING PLAYER DETECTED.");
                break;
            case 0:
            default:
            case 1:
                endText.set("GG&WP!");
                break;
            case 2:
                endText.set("Are you a boy or a girl?");
                break;
            case 3:
                endText.set("sorry, you lose!");
            case 4:
                endText.set("you do not get older. you lvl up.");
                break;
            case 5:
                endText.set("DOES THIS UNIT HAVE A SOUL?");
                soundPool.play(does_this_unit_have_a_soul, 0.6F, 0.6F, 0, 0, 1F);
                break;
            case 6:
                endText.set("A hero need not speak. When he is gone, the world will speak for him.");
                break;
            case 7:
                endText.set("UGANDA FOREVER!");
                soundPool.play(you_are_the_most_successful, 0.6F, 0.6F, 0, 0, 1F);
                break;
            case 8:
                endText.set("You are nobody.. nobody's perfect.. therefore YOU ARE PERFECT!");
                break;
            case 9:
                endText.set("If anything’s possible, then is it possible that nothing’s possible?");
                break;
            case 10:
                endText.set("YOU SUCCEEDED WHERE OTHER DO NOT.");
                soundPool.play(you_are_succeeded_where_other_did_not, 0.6F, 0.6F, 0, 0, 1F);
                break;
        }
    }
}
