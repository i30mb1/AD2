package n7.ad2.splash;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.transition.ChangeBounds;
import android.support.transition.Fade;
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;

import n7.ad2.R;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.PlainAdapter;
import n7.ad2.databinding.ActivitySplashNormalBinding;
import n7.ad2.main.MainActivity;
import n7.ad2.utils.UnscrollableLinearLayoutManager;

public class SplashActivity extends BaseActivity {

    public static final long ANIMATION_DURATION = 300L;
    private ActivitySplashNormalBinding binding;
    private SplashViewModel viewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
//        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        PreferenceManager.setDefaultValues(this, R.xml.setting, false);

        viewModel = ViewModelProviders.of(this).get(SplashViewModel.class);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_splash_normal);
        binding.setViewModel(viewModel);
        binding.executePendingBindings();

        setupRecyclerView();
        setupAnimationOnSplashLogo();

        startPulsing();

        viewModel.startMainActivity.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    startNewActivityWithAnimation();
                }
            }
        });
    }

    private void setupAnimationOnSplashLogo() {
        binding.ivActivitySplash.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewActivityWithAnimation();
            }
        });
    }

    private void startNewActivityWithAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                startNewActivity();
            }
        }, ANIMATION_DURATION);
        startConstraintAnimation();
    }

    private void startNewActivity() {
        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startPulsing() {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(binding.ivActivitySplash,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.1f));
        scaleDown.setDuration(1000L);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    private void startConstraintAnimation() {
        //содержит информацию о всех состояних view
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(this, R.layout.activity_splash_invisible);

        TransitionSet transitionSet = new TransitionSet();
//        Transition slide = new Slide(Gravity.START); //появление из любого края активити
//        Transition explode = new Explode(); //почти как Slide но может выбигать из любой точки
//        Transition changeImageTransform = new ChangeImageTransform();//анимирует матричный переход изображений внутри ImageView
        Transition fade = new Fade().setDuration(ANIMATION_DURATION).addTarget(binding.ivActivitySplash);
        Transition changeBoundsIV = new ChangeBounds().setDuration(ANIMATION_DURATION).setInterpolator(new AnticipateOvershootInterpolator(2.0f)).addTarget(binding.ivActivitySplash);
        transitionSet.addTransition(fade).addTransition(changeBoundsIV);
        transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot(), transitionSet);// вызываем метод, говорящий о том, что мы хотим анимировать следующие изменения внутри constraintLayout
        constraintSet.applyTo((ConstraintLayout) binding.getRoot());// применяем изменения
    }

    private void setupRecyclerView() {
        PlainAdapter adapter = viewModel.getAdapter();
        binding.rvActivitySplash.setAdapter(adapter);
        binding.rvActivitySplash.setLayoutManager(new UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
    }

}
