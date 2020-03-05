package n7.ad2.games;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableInt;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.transition.AutoTransition;
import androidx.transition.TransitionManager;
import androidx.appcompat.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;

import java.util.Random;

import n7.ad2.R;
import n7.ad2.databinding.ActivityGame2p1Binding;

public class Game2p1 extends AppCompatActivity {

    public ObservableInt clicks = new ObservableInt();
    public ObservableInt secondRemains = new ObservableInt();
    private int soundIdAxeHit;
    private SoundPool soundPool;
    private int width;
    private int height;
    private ActivityGame2p1Binding binding;

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game2p1);
        binding.setActivity(this);
        binding.executePendingBindings();

        initSoundPoolPlayer();
        initDisplayWidthHeight();
        setCreepsInCenter();

        startPulsing(binding.ivActivityGame2p1Creep1, 800L);
        startTimer();
    }

    private void initSoundPoolPlayer() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIdAxeHit = soundPool.load(this, R.raw.axe_hit, 1);
    }

    private void setCreepsInCenter() {
        for (View view : new View[]{binding.ivActivityGame2p1Creep1, binding.ivActivityGame2p1Creep2, binding.ivActivityGame2p1Creep3}) {
            ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            absParams.topMargin = height / 2;
            absParams.leftMargin = width / 2;
            view.setLayoutParams(absParams);
        }
    }

    public void incClicks() {
        clicks.set(clicks.get() + 1);
    }

    private void startTimer() {
        new CountDownTimer(30000, 100) {
            @Override
            public void onTick(long millisUntilFinished) {
                secondRemains.set((int) (millisUntilFinished / 1000));
            }

            @Override
            public void onFinish() {
                secondRemains.set(0);
                changeBackgroundColor();
                showResult();
            }
        }.start();
    }

    private void showResult() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_game2p1_finish);

        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), new AutoTransition());

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

    private void startPulsing(View view, long duration) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f));
        scaleDown.setDuration(duration);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    public void moveCreep(View view) {
        incClicks();
        setMargins(view);
        playSoundClick();
        if (clicks.get() > 5) {
            binding.ivActivityGame2p1Creep2.setVisibility(View.VISIBLE);
            startPulsing(binding.ivActivityGame2p1Creep2, 750L);
        }
        if (clicks.get() > 15) {
            binding.ivActivityGame2p1Creep3.setVisibility(View.VISIBLE);
            startPulsing(binding.ivActivityGame2p1Creep3, 700L);
        }
    }

    private void setMargins(View view) {
        Random r = new Random();
        ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        absParams.topMargin = r.nextInt(height);
        absParams.leftMargin = r.nextInt(width);
        view.setLayoutParams(absParams);
    }

    private void initDisplayWidthHeight() {
        int sizeInDp = 75;
        int dp50inPx = convertDpToPixel(this, sizeInDp);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x - dp50inPx;
        height = size.y - dp50inPx;
    }

    private void playSoundClick() {
        soundPool.play(soundIdAxeHit, 0.6F, 0.6F, 0, 0, 0.5F);
    }

}
