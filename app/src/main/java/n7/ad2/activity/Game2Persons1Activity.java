package n7.ad2.activity;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.os.Handler;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.app.AppCompatActivity;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.util.TypedValue;
import android.view.Display;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.util.Random;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;

import static n7.ad2.fragment.GameFragment.SCORE_GAME2_GAME_FRAGMENT;

public class Game2Persons1Activity extends AppCompatActivity implements View.OnClickListener {

    private static long END_TIME = 0;
    private long time = 30;
    private ImageView iv_activity_game2_persons1_creep;
    private ImageView iv_activity_game2_persons1_creep2;
    private ImageView iv_activity_game2_persons1_creep3;
    private ImageView iv_activity_game2_persons1_creep4;
    private TextView tv_activity_game2_persons1_clicks;
    private int soundIdAxeHit;
    private int clicks = 0;
    private SoundPool soundPool;
    private TextView tv_activity_game2_persons1_timer;
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private TextView tv_activity_game2_persons1_equal;
    private TextView tv_activity_game2_persons1_final_score;
    private TextView tv_activity_game2_desc_remain;
    private TextView tv_activity_game2_desc_clicks;
    private TextView tv_fragment_game_title_game2;
    private ConstraintLayout constraintLayout;
    private TextView tv_activity_game2_persons1_desc_apm;
    private int width;
    private int height;

    public static int convertDpToPixel(Context ctx, int dp) {
        float density = ctx.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game2_persons1);

        initView();
        initConstraintAnimation();
        initSoundPoolPlayer();
        initDisplayWidthHeight();
        setCreepsInCenter();

        startPulsing(iv_activity_game2_persons1_creep,800L);
        startTimer();
    }

    private void initSoundPoolPlayer() {
        soundPool = new SoundPool(2, AudioManager.STREAM_MUSIC, 0);
        soundIdAxeHit = soundPool.load(this, R.raw.axe_hit, 1);
    }

    private void initView() {
        tv_activity_game2_persons1_desc_apm = findViewById(R.id.tv_activity_game2_persons1_desc_apm);
        tv_fragment_game_title_game2 = findViewById(R.id.tv_fragment_game_title_game2);
        tv_activity_game2_desc_clicks = findViewById(R.id.tv_activity_game2_desc_clicks);
        tv_activity_game2_desc_remain = findViewById(R.id.tv_activity_game2_desc_remain);
        tv_activity_game2_persons1_equal = findViewById(R.id.tv_activity_game2_persons1_equal);
        tv_activity_game2_persons1_final_score = findViewById(R.id.tv_activity_game2_persons1_final_score);
        tv_activity_game2_persons1_timer = findViewById(R.id.tv_activity_game2_persons1_timer);
        tv_activity_game2_persons1_clicks = findViewById(R.id.tv_activity_game2_persons1_clicks);
        iv_activity_game2_persons1_creep = findViewById(R.id.iv_activity_game2_persons1_creep);
        iv_activity_game2_persons1_creep2 = findViewById(R.id.iv_activity_game2_persons1_creep2);
        iv_activity_game2_persons1_creep3 = findViewById(R.id.iv_activity_game2_persons1_creep3);
        iv_activity_game2_persons1_creep4 = findViewById(R.id.iv_activity_game2_persons1_creep4);
        iv_activity_game2_persons1_creep4.setOnClickListener(this);
        iv_activity_game2_persons1_creep3.setOnClickListener(this);
        iv_activity_game2_persons1_creep2.setOnClickListener(this);
        iv_activity_game2_persons1_creep.setOnClickListener(this);
    }

    private void setCreepsInCenter() {
        for (View view : new View[]{iv_activity_game2_persons1_creep, iv_activity_game2_persons1_creep2, iv_activity_game2_persons1_creep3, iv_activity_game2_persons1_creep4}) {
            ConstraintLayout.LayoutParams absParams = (ConstraintLayout.LayoutParams) view.getLayoutParams();
            absParams.topMargin = height/2;
            absParams.leftMargin = width/2;
            view.setLayoutParams(absParams);
        }
    }

    private void incClicks() {
        clicks++;
        tv_activity_game2_persons1_clicks.setText(String.valueOf(clicks));
    }

    private void startTimer() {
        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                time--;
                tv_activity_game2_persons1_timer.setText(String.valueOf(time));
                if (time != END_TIME) {
                    handler.postDelayed(this, 1000);
                }
                if (time == END_TIME) {
                    changeBackgroundColor();
                    startConstraintAnimation();
                    tv_activity_game2_persons1_final_score.setText(currentAPM());
                    passResultAndFinish(handler);
                    saveResult();
                }
            }
        }, 1000);
    }

    private void saveResult() {
        MySharedPreferences.getSharedPreferences(this).edit().putString(SCORE_GAME2_GAME_FRAGMENT, currentAPM()).apply();
    }

    private void passResultAndFinish(Handler handler) {
        Bundle bundle = new Bundle();
        bundle.putString(SCORE_GAME2_GAME_FRAGMENT, currentAPM());
        Intent intent = new Intent();
        intent.putExtras(bundle);
        setResult(Activity.RESULT_OK, intent);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                finish();
            }
        }, 5000);
    }

    private String currentAPM() {
        String apm = new DecimalFormat("#0").format(clicks * 2);
        return apm;
    }

    private void changeBackgroundColor() {
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), getColorAccentTheme(), getResources().getColor(android.R.color.black));
        colorAnimation.setDuration(1000);
        colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animator) {
                constraintLayout.setBackgroundColor((int) animator.getAnimatedValue());
            }
        });
        colorAnimation.start();
    }

    private int getColorAccentTheme() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
        return typedValue.data;
    }

    private void initConstraintAnimation() {
        constraintLayout = findViewById(R.id.root);
        //содержит информацию о всех состояних view
        constraintSetOrigin.clone(constraintLayout);
        constraintSetNew.clone(this, R.layout.activity_game2_persons1_0);
    }

    private void startConstraintAnimation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            TransitionSet transitionSet = new TransitionSet();

            Transition changeBound = new ChangeBounds().setDuration(1000)
                    .addTarget(tv_activity_game2_persons1_clicks)
                    .addTarget(tv_activity_game2_persons1_timer)
                    .addTarget(tv_fragment_game_title_game2)
                    .addTarget(tv_activity_game2_desc_remain)
                    .addTarget(tv_activity_game2_desc_clicks)
                    .addTarget(iv_activity_game2_persons1_creep);

            Transition fadeInAPM = new Fade(Fade.IN).setDuration(1000).addTarget(tv_activity_game2_persons1_desc_apm);

            Transition fadeInEqual = new Fade(Fade.IN).setDuration(1000).setStartDelay(2000).addTarget(tv_activity_game2_persons1_equal);

            Transition fadeInScore = new Fade(Fade.IN).setDuration(1000).setStartDelay(3000).addTarget(tv_activity_game2_persons1_final_score);

            transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);

            transitionSet.addTransition(fadeInAPM).addTransition(fadeInEqual).addTransition(fadeInScore).addTransition(changeBound);

            TransitionManager.beginDelayedTransition(constraintLayout, transitionSet);
            constraintSetNew.applyTo(constraintLayout);
        }
    }

    private void startPulsing(View view,long duration) {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 0.9f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 0.9f));
        scaleDown.setDuration(duration);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    private void moveCreep(View view) {
        setMargins(view);
        playSoundClick();
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


    @Override
    public void onClick(View v) {
        incClicks();
        moveCreep(v);
        if (clicks == 10) {
            startPulsing(iv_activity_game2_persons1_creep2,700L);
            iv_activity_game2_persons1_creep2.setVisibility(View.VISIBLE);
        }
        if (clicks == 20) {
            startPulsing(iv_activity_game2_persons1_creep3,750L);
            iv_activity_game2_persons1_creep3.setVisibility(View.VISIBLE);
            startPulsing(iv_activity_game2_persons1_creep4,650L);
            iv_activity_game2_persons1_creep4.setVisibility(View.VISIBLE);
        }
    }
}
