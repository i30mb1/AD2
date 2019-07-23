package n7.ad2.games;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;
import android.view.animation.LinearInterpolator;

import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Random;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.ad2.R;
import n7.ad2.databinding.ActivityGame3p1Binding;
import n7.ad2.utils.Utils;

public class Game3p1 extends AppCompatActivity {

    public ObservableBoolean isLoading = new ObservableBoolean(false);
    public ObservableInt realCostItem = new ObservableInt();
    public ObservableInt currentScore = new ObservableInt(0);
    public ObservableInt fakeCostItem = new ObservableInt(0);
    public ObservableBoolean lock = new ObservableBoolean(true);
    int[] diff = new int[]{25, -25, 100, -100, 50, -50};
    private ActivityGame3p1Binding binding;
    private Executor diskIO = Executors.newSingleThreadExecutor();
    private SoundPool soundPool;
    private int right;
    private int nice;
    private int wakeUp;
    private int negative;
    private int goodInstincts;
    private int perfectly_commander;
    private int iGuessNot;
    private int freeAndClear;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game3p1);
        binding.setActivity(this);

        loadPlayer();
        startGame();

    }

    private void startGame() {
        diskIO.execute(new Runnable() {

            @Override
            public void run() {
                try {
                    isLoading.set(true);
                    final String[] listItems = getAssets().list("items");

                    int randomItem = new Random().nextInt(listItems.length);

                    String nameItem = listItems[randomItem];

                    JSONObject objectItem = new JSONObject(Utils.readJSONFromAsset(Game3p1.this, "items/" + nameItem + "/" + "ru_description.json"));

                    if (objectItem.has("cost")) {
                        realCostItem.set(Integer.valueOf(objectItem.getString("cost")));
                    }

                    setImageItem("file:///android_asset/items/" + nameItem + "/full.webp");

                    calculateFakeCostItem();

                } catch (IOException e) {
                    startGame();
                } catch (JSONException e) {
                    startGame();
                } catch (NullPointerException e) {
                    startGame();
                } catch (NumberFormatException e) {
                    startGame();
                } finally {
                    isLoading.set(false);
                    lock.set(false);
                }
            }
        });
    }

    private void endGame(int idLayout) {
        isLoading.set(false);
        lock.set(true);
        if (currentScore.get() < 2) {
            soundPool.play(negative, 1, 1, 0, 0, 1);
        } else if (currentScore.get() > 5) {
            soundPool.play(wakeUp, 1, 1, 0, 0, 1);
        } else {
            soundPool.play(iGuessNot, 1, 1, 0, 0, 1);
        }
        changeBackgroundColor();
        startAnimation(idLayout);
    }

    private void startAnimation(int idLayout) {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, idLayout);

        TransitionSet transitionSet = new TransitionSet()
                .addTransition(new ChangeBounds()
                        .setInterpolator(new LinearInterpolator())
                        .addTarget(binding.bActivityGame3p1No)
                        .addTarget(binding.bActivityGame3p1Yes)
                        .addTarget(binding.tvActivityGame3p1SuggestedCost)
                        .setDuration(1000))
                .addTransition(new ChangeBounds()
                        .setInterpolator(new BounceInterpolator())
                        .addTarget(binding.tvActivityGame3p1RealCost)
                        .addTarget(binding.ivGold)
                        .setDuration(1000)
                        .setStartDelay(1000));

        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), transitionSet);

        constraintSet.applyTo((ConstraintLayout) binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 3000);

    }

    public void clickYes(View view) {
        lock.set(true);
        if (fakeCostItem.get() == realCostItem.get()) {
            currentScore.set(currentScore.get() + 1);
            playSound();
            startGame();
        } else {
            endGame(R.layout.activity_game3p1_final);
        }
    }

    private void playSound() {
        switch (currentScore.get()) {
            case 10:
            case 9:
                soundPool.play(freeAndClear, 1, 1, 0, 0, 1);
                break;
            case 8:
            case 7:
                soundPool.play(perfectly_commander, 1, 1, 0, 0, 1);
                break;
            case 6:
            case 5:
                soundPool.play(goodInstincts, 1, 1, 0, 0, 1);
                break;
            case 4:
            case 3:
                soundPool.play(nice, 1, 1, 0, 0, 1);
                break;
            default:
            case 2:
            case 1:
                soundPool.play(right, 1, 1, 0, 0, 1);
                break;
        }
    }

    private void loadPlayer() {
        soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 0);

        right = soundPool.load(this, R.raw.right, 1);
        freeAndClear = soundPool.load(this, R.raw.free_and_clear, 1);
        iGuessNot = soundPool.load(this, R.raw.i_guess_not, 1);
        goodInstincts = soundPool.load(this, R.raw.good_instincts, 1);
        nice = soundPool.load(this, R.raw.nice, 1);
        perfectly_commander = soundPool.load(this, R.raw.perfectly_commander, 1);
        wakeUp = soundPool.load(this, R.raw.wake_up_commander, 1);
        negative = soundPool.load(this, R.raw.negative, 1);
    }

    public void clickNo(View view) {
        lock.set(true);
        if (fakeCostItem.get() == realCostItem.get()) {
            binding.tvActivityGame3p1SuggestedCost.setText(String.valueOf(fakeCostItem.get()));
            endGame(R.layout.activity_game3p1_final2);
        } else {
            currentScore.set(currentScore.get() + 1);
            playSound();
            startGame();
        }
    }

    private void calculateFakeCostItem() {
        if (new Random().nextBoolean()) {
            fakeCostItem.set(realCostItem.get() + diff[new Random().nextInt(diff.length)]);
        } else {
            fakeCostItem.set(realCostItem.get());
        }
    }

    private void setImageItem(final String path) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Picasso.get().load(path).resize(300, 200).into(binding.ivActivityGame3p1Item);
            }
        });
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
}
