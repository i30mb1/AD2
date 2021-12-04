//package n7.ad2.games.internal.games;
//
//import android.animation.ArgbEvaluator;
//import android.animation.ValueAnimator;
//import android.media.AudioManager;
//import android.media.SoundPool;
//import android.os.Bundle;
//import android.os.CountDownTimer;
//import android.os.Handler;
//import android.util.TypedValue;
//import android.view.View;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.view.WindowManager;
//import android.view.animation.AlphaAnimation;
//import android.view.animation.AnimationSet;
//import android.view.animation.BounceInterpolator;
//import android.view.animation.LinearInterpolator;
//import android.view.animation.ScaleAnimation;
//import android.widget.ImageView;
//import android.widget.TextView;
//
//import androidx.appcompat.app.AppCompatActivity;
//import androidx.constraintlayout.widget.ConstraintLayout;
//import androidx.constraintlayout.widget.ConstraintSet;
//import androidx.databinding.DataBindingUtil;
//import androidx.databinding.ObservableBoolean;
//import androidx.databinding.ObservableField;
//import androidx.databinding.ObservableInt;
//import androidx.transition.ChangeBounds;
//import androidx.transition.Fade;
//import androidx.transition.TransitionManager;
//import androidx.transition.TransitionSet;
//
//import org.json.JSONArray;
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.IOException;
//import java.util.LinkedList;
//import java.util.Random;
//import java.util.concurrent.Executor;
//import java.util.concurrent.Executors;
//
//import n7.ad2.R;
//import n7.ad2.databinding.ActivityGame1p2Binding;
//import n7.ad2.utils.Utils;
//
//public class Game1p2 extends AppCompatActivity {
//
//    public ObservableInt secondRemaining = new ObservableInt(30);
//    public ObservableBoolean isLoadingPlayer1 = new ObservableBoolean(true);
//    public ObservableBoolean isLoadingPlayer2 = new ObservableBoolean(true);
//    public ObservableField<String> slot1 = new ObservableField<>();
//    public ObservableField<String> slot2 = new ObservableField<>();
//    public ObservableField<String> slot3 = new ObservableField<>();
//    public ObservableField<String> slot4 = new ObservableField<>();
//    public ObservableField<String> slot1_2 = new ObservableField<>();
//    public ObservableField<String> slot2_2 = new ObservableField<>();
//    public ObservableField<String> slot3_2 = new ObservableField<>();
//    public ObservableField<String> slot4_2 = new ObservableField<>();
//    public ObservableInt currentSpellLVL = new ObservableInt(1);
//    public ObservableInt currentSpellLVL_2 = new ObservableInt(1);
//    public ObservableInt scorePlayer1 = new ObservableInt(0);
//    public ObservableInt scorePlayer2 = new ObservableInt(0);
//    public ObservableBoolean lockPlayer1 = new ObservableBoolean(true);
//    public ObservableBoolean lockPlayer2 = new ObservableBoolean(true);
//    public ObservableField<String> endTextPlayer1 = new ObservableField<>();
//    public ObservableField<String> endTextPlayer2 = new ObservableField<>();
//    private String rightAnswerStringPlayer1;
//    private String rightAnswerStringPlayer2;
//    private Executor diskIO;
//    private Executor diskIO2;
//    private int hero;
//    private ActivityGame1p2Binding binding;
//    private SoundPool soundPool;
//    private int soundYes;
//    private int soundNo;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        super.onCreate(savedInstanceState);
//
//        diskIO = Executors.newSingleThreadExecutor();
//        diskIO2 = Executors.newSingleThreadExecutor();
//        binding = DataBindingUtil.setContentView(this, R.layout.activity_game1p2);
//        binding.setActivity(this);
//        binding.executePendingBindings();
//
//        loadPlayer();
//        startCountDownTimer();
//        startGamePlayer1();
//        startGamePlayer2();
//    }
//
//    private void loadPlayer() {
//        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);
//        soundYes = soundPool.load(this, R.raw.yes, 1);
//        soundNo = soundPool.load(this, R.raw.no, 1);
//    }
//
//    private void startCountDownTimer() {
//        new CountDownTimer(20000, 100) {
//            @Override
//            public void onTick(long millisUntilFinished) {
//                secondRemaining.set((int) (millisUntilFinished / 1000));
//            }
//
//            @Override
//            public void onFinish() {
//                secondRemaining.set(0);
//                showResult();
//            }
//        }.start();
//    }
//
//    private void showResult() {
//        lockPlayer1.set(true);
//        changeBackgroundColor();
//        calculateResult();
//        startConstraintAnimation();
//    }
//
//    private void calculateResult() {
//        if (scorePlayer1.get() > scorePlayer2.get()) {
//            endTextPlayer1.set(getString(R.string.game_you_win));
//            binding.tvStatePlayer1.setTextColor(getResources().getColor(android.R.color.holo_green_light));
//            endTextPlayer2.set(getString(R.string.game_you_lose));
//            binding.tvStatePlayer2.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//        } else {
//            endTextPlayer2.set(getString(R.string.game_you_win));
//            binding.tvStatePlayer2.setTextColor(getResources().getColor(android.R.color.holo_green_light));
//            endTextPlayer1.set(getString(R.string.game_you_lose));
//            binding.tvStatePlayer1.setTextColor(getResources().getColor(android.R.color.holo_red_light));
//        }
//        if (scorePlayer2.get() == scorePlayer1.get()) {
//            endTextPlayer2.set("GG&WP");
//            endTextPlayer1.set("GG&WP");
//        }
//    }
//
//    private void startConstraintAnimation() {
//        isLoadingPlayer1.set(false);
//        isLoadingPlayer2.set(false);
//
//        ConstraintSet constraintSet = new ConstraintSet();
//        constraintSet.clone(this, R.layout.activity_game1p2_finish);
//
//        TransitionSet transitionSet = new TransitionSet()
//                .addTransition(new ChangeBounds().setInterpolator(new LinearInterpolator())
//                        .addTarget(binding.linearLayout11)
//                        .addTarget(binding.linearLayout12)
//                        .addTarget(binding.tvActivityGame1Timer)
//                        .addTarget(binding.tvActivityGame1Score)
//                        .addTarget(binding.tvActivityGame1Score2)
//                        .setDuration(1000))
//                .addTransition(new Fade(Fade.OUT).setDuration(500)
//                        .addTarget(binding.ivActivityGame1P1Spell)
//                        .addTarget(binding.ivActivityGame1P1Spell2)
//                        .addTarget(binding.linearLayout22)
//                        .addTarget(binding.linearLayout21))
//                .addTransition(new ChangeBounds().setInterpolator(new BounceInterpolator())
//                        .addTarget(binding.tvStatePlayer1)
//                        .addTarget(binding.tvStatePlayer2)
//                        .setStartDelay(1500)
//                        .setDuration(1000));
//
//        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), transitionSet);
//        constraintSet.applyTo((ConstraintLayout) binding.getRoot());
//
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                finish();
//            }
//        }, 6000);
//    }
//
//    private void changeBackgroundColor() {
//        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getColorAccentTheme(), getResources().getColor(android.R.color.black));
//        colorAnimation.setDuration(1000);
//        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
//
//            @Override
//            public void onAnimationUpdate(ValueAnimator animator) {
//                binding.getRoot().setBackgroundColor((int) animator.getAnimatedValue());
//            }
//        });
//        colorAnimation.start();
//    }
//
//    private int getColorAccentTheme() {
//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//        return typedValue.data;
//    }
//
//    private void startGamePlayer1() {
//        diskIO.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    isLoadingPlayer1.set(true);
//                    final String[] listHeroes = getAssets().list("heroes");
//                    hero = new Random().nextInt(listHeroes.length);
//                    JSONArray spells = new JSONObject(Utils.readJSONFromAsset(Game1p2.this, "heroes/" + listHeroes[hero] + "/" + "ru_abilities.json")).getJSONArray("abilities");
//
//                    final int randomSpell = new Random().nextInt(spells.length());
//                    //загружаем ману для способности и уровень способности
//                    String spellManaCostString = spells.getJSONObject(randomSpell).get("mana").toString();
//                    if (spellManaCostString.equals("0") || spellManaCostString.contains("(") || spellManaCostString.contains("+") || spellManaCostString.contains("%")) {
//                        throw new IOException();
//                    }
//
//                    int spellLVL = new Random().nextInt(spellManaCostString.split("/").length);
//                    String spellManaPoints = spellManaCostString.split("/")[spellLVL];
//
//                    setLVL(spellLVL);
//                    setMpPlayer1(spellManaPoints);
//                    setImageSpell(String.format("file:///android_asset/heroes/%s/%s.webp", listHeroes[hero], String.valueOf(randomSpell + 1)), binding.ivActivityGame1P1Spell);
//
//                } catch (IOException e) {
//                    startGamePlayer1();
//                } catch (JSONException e) {
//                    startGamePlayer1();
//                } finally {
//                    isLoadingPlayer1.set(false);
//                    lockPlayer1.set(false);
//                }
//            }
//        });
//    }
//
//    private void startGamePlayer2() {
//        diskIO2.execute(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    isLoadingPlayer2.set(true);
//                    final String[] listHeroes = getAssets().list("heroes");
//                    hero = new Random().nextInt(listHeroes.length);
//                    JSONArray spells = new JSONObject(Utils.readJSONFromAsset(Game1p2.this, "heroes/" + listHeroes[hero] + "/" + "ru_abilities.json")).getJSONArray("abilities");
//
//                    final int randomSpell = new Random().nextInt(spells.length());
//                    //загружаем ману для способности и уровень способности
//                    String spellManaCostString = spells.getJSONObject(randomSpell).get("mana").toString();
//                    if (spellManaCostString.equals("0") || spellManaCostString.contains("(") || spellManaCostString.contains("+") || spellManaCostString.contains("%")) {
//                        throw new IOException();
//                    }
//
//                    int spellLVL = new Random().nextInt(spellManaCostString.split("/").length);
//                    String spellManaPoints = spellManaCostString.split("/")[spellLVL];
//
//                    setLVLPlayer2(spellLVL);
//                    setMpPlayer2(spellManaPoints);
//                    setImageSpell(String.format("file:///android_asset/heroes/%s/%s.webp", listHeroes[hero], String.valueOf(randomSpell + 1)), binding.ivActivityGame1P1Spell2);
//
//                } catch (IOException e) {
//                    startGamePlayer2();
//                } catch (JSONException e) {
//                    startGamePlayer2();
//                } finally {
//                    isLoadingPlayer2.set(false);
//                    lockPlayer2.set(false);
//                }
//            }
//        });
//    }
//
//    private void setImageSpell(final String format, final ImageView view) {
//        runOnUiThread(new Runnable() {
//            @Override
//            public void run() {
//            }
//        });
//    }
//
//    private void setMpPlayer1(final String manaPoints) {
//        diskIO.execute(new Runnable() {
//            @Override
//            public void run() {
//                int[] numbersDiff = new int[]{-30, -25, -20, -15, -10, -5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
//                rightAnswerStringPlayer1 = manaPoints;
//                int rightAnswerInt = Integer.valueOf(manaPoints);
//                LinkedList<Integer> listWithValues = new LinkedList<>();
//                listWithValues.add(rightAnswerInt);
//                do {
//                    int minus = rightAnswerInt + numbersDiff[new Random().nextInt(numbersDiff.length)];
//                    if (minus > 0) listWithValues.add(minus);
//                } while (listWithValues.size() != 4);
//
//                slot1.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//                slot2.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//                slot3.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//                slot4.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//            }
//        });
//    }
//
//    private void setMpPlayer2(final String manaPoints) {
//        diskIO.execute(new Runnable() {
//            @Override
//            public void run() {
//                int[] numbersDiff = new int[]{-30, -25, -20, -15, -10, -5, 5, 10, 15, 20, 25, 30, 35, 40, 45, 50};
//                rightAnswerStringPlayer2 = manaPoints;
//                int rightAnswerInt = Integer.valueOf(manaPoints);
//                LinkedList<Integer> listWithValues = new LinkedList<>();
//                listWithValues.add(rightAnswerInt);
//                do {
//                    int minus = rightAnswerInt + numbersDiff[new Random().nextInt(numbersDiff.length)];
//                    if (minus > 0) listWithValues.add(minus);
//                } while (listWithValues.size() != 4);
//
//                slot1_2.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//                slot2_2.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//                slot3_2.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//                slot4_2.set("" + listWithValues.remove(new Random().nextInt(listWithValues.size())));
//            }
//        });
//    }
//
//    private void setLVL(final int i) {
//        currentSpellLVL.set(i);
//    }
//
//    private void setLVLPlayer2(final int i) {
//        currentSpellLVL_2.set(i);
//    }
//
//    public void expandAnimation(View view) {
//        AnimationSet animationSet = new AnimationSet(false);
//        AlphaAnimation alphaAnimation = new AlphaAnimation(1f, 0f);
//        ScaleAnimation scaleAnimation = new ScaleAnimation(1f, 3f, 1f, 3f, view.getWidth() / 2, view.getHeight() / 2);
//        animationSet.addAnimation(scaleAnimation);
//        animationSet.addAnimation(alphaAnimation);
//        animationSet.setDuration(200);
//        view.startAnimation(animationSet);
//    }
//
//    public void checkPlayer1(View view) {
//        lockPlayer1.set(true);
////        expandAnimation(view);
//        boolean isRight;
//        if (((TextView) view).getText().equals(rightAnswerStringPlayer1)) {
//            ((TextView) view).setTextColor(getResources().getColor(android.R.color.holo_green_light));
//            isRight = true;
//            scorePlayer1.set(scorePlayer1.get() + 1);
//        } else {
//            ((TextView) view).setTextColor(getResources().getColor(android.R.color.holo_red_light));
//            scorePlayer1.set(scorePlayer1.get() - 1);
//            isRight = false;
//        }
//        playSound(isRight);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startGamePlayer1();
//            }
//        }, 300);
//    }
//
//    public void checkPlayer2(View view) {
//        lockPlayer2.set(true);
////        expandAnimation(view);
//        boolean isRight;
//        if (((TextView) view).getText().equals(rightAnswerStringPlayer2)) {
//            ((TextView) view).setTextColor(getResources().getColor(android.R.color.holo_green_light));
//            isRight = true;
//            scorePlayer2.set(scorePlayer2.get() + 1);
//        } else {
//            ((TextView) view).setTextColor(getResources().getColor(android.R.color.holo_red_light));
//            scorePlayer2.set(scorePlayer2.get() - 1);
//            isRight = false;
//        }
//        playSound(isRight);
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                startGamePlayer2();
//            }
//        }, 300);
//    }
//
//
//    private void playSound(boolean isRight) {
//        if (isRight) {
//            soundPool.play(soundYes, 0.6F, 0.6F, 0, 0, 1F);
//        } else {
//            soundPool.play(soundNo, 0.6F, 0.6F, 0, 0, 1F);
//        }
//    }
//}
