package n7.ad2.games;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.AutoTransition;
import android.support.transition.TransitionManager;
import android.support.v7.app.AppCompatActivity;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import java.util.Random;

import n7.ad2.R;
import n7.ad2.databinding.ActivityGame2p2Binding;

public class Game2p2 extends AppCompatActivity {


    public ObservableInt clicksPlayer1 = new ObservableInt();
    public ObservableInt clicksPlayer2 = new ObservableInt();
    public ObservableInt secondRemains = new ObservableInt();
    public ObservableField<String> endTextPlayer1 = new ObservableField<>();
    public ObservableField<String> endTextPlayer2 = new ObservableField<>();
    private int soundIdAxeHit;
    private SoundPool soundPool;
    private int width;
    private int heightPlayer1;
    private int heightPlayer2;
    private ActivityGame2p2Binding binding;
    private int sizeInPx;

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_game2p2);
        binding.setActivity(this);
        binding.executePendingBindings();

        initSoundPoolPlayer();
        initDisplayWidthHeight();
        setCreepsInCenter();

        startPulsing(binding.ivCreep1Player1, 800L);
        startPulsing(binding.ivCreep1Player2, 750L);
        startTimer();
    }

    private void initSoundPoolPlayer() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIdAxeHit = soundPool.load(this, R.raw.axe_hit, 1);
    }

    private void setCreepsInCenter() {
        for (View view : new View[]{binding.ivCreep1Player2, binding.ivCreep2Player2}) {
            ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            absParams.topMargin = heightPlayer1 / 2;
            absParams.leftMargin = width / 2;
            view.setLayoutParams(absParams);
        }

        for (View view : new View[]{binding.ivCreep1Player1, binding.ivCreep2Player1}) {
            ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            absParams.topMargin = (heightPlayer2 / 2) + heightPlayer1;
            absParams.leftMargin = width / 2;
            view.setLayoutParams(absParams);
        }
    }

    public void incClicksPlayer1() {
        clicksPlayer1.set(clicksPlayer1.get() + 1);
    }

    public void incClicksPlayer2() {
        clicksPlayer2.set(clicksPlayer2.get() + 1);
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
                setResult();
                changeBackgroundColor();
                startAnimation();
            }
        }.start();
    }

    private void startAnimation() {
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_game2p2_finish);

        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), new AutoTransition());

        constraintSet.applyTo((ConstraintLayout) binding.getRoot());

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 6000);
    }

    private void setResult() {
        int scorePlayer1 = clicksPlayer1.get();
        int scorePlayer2 = clicksPlayer2.get();
        if (scorePlayer1 > scorePlayer2) {
            endTextPlayer1.set(getString(R.string.game_you_win));
            binding.tvFinalPlayer1.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            endTextPlayer2.set(getString(R.string.game_you_lose));
            binding.tvFinalPlayer2.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            endTextPlayer2.set(getString(R.string.game_you_win));
            binding.tvFinalPlayer2.setTextColor(getResources().getColor(android.R.color.holo_green_light));
            endTextPlayer1.set(getString(R.string.game_you_lose));
            binding.tvFinalPlayer1.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
        if (scorePlayer1 == scorePlayer2) {
            endTextPlayer1.set("GG&WP");
            endTextPlayer2.set("GG&WP");
        }
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

    public void moveCreepPlayer1(View view) {
        incClicksPlayer1();
        setMarginsPlayer1(view);
        playSoundClick();
        if (clicksPlayer1.get() > 10) {
            binding.ivCreep2Player1.setVisibility(View.VISIBLE);
            startPulsing(binding.ivCreep2Player1, 700L);
        }
    }

    public void moveCreepPlayer2(View view) {
        incClicksPlayer2();
        setMarginsPlayer2(view);
        playSoundClick();
        if (clicksPlayer2.get() > 10) {
            binding.ivCreep2Player2.setVisibility(View.VISIBLE);
            startPulsing(binding.ivCreep2Player2, 650);
        }
    }

    private void setMarginsPlayer1(View view) {
        Random r = new Random();
        ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        absParams.topMargin = r.nextInt(heightPlayer1) + heightPlayer1 + sizeInPx / 2;
        absParams.leftMargin = r.nextInt(width) + sizeInPx / 2;
        view.setLayoutParams(absParams);
    }

    private void setMarginsPlayer2(View view) {
        Random r = new Random();
        ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
        absParams.topMargin = r.nextInt(heightPlayer2);
        absParams.leftMargin = r.nextInt(width) + sizeInPx / 2;
        view.setLayoutParams(absParams);
    }


    private void initDisplayWidthHeight() {
        int sizeInDp = 80;
        sizeInPx = convertDpToPixel(this, sizeInDp);
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x - sizeInPx;
        heightPlayer1 = (size.y - sizeInPx) / 2;
        heightPlayer2 = (size.y - sizeInPx) / 2;
    }

    private void playSoundClick() {
        soundPool.play(soundIdAxeHit, 0.6F, 0.6F, 0, 0, 0.5F);
    }

}
