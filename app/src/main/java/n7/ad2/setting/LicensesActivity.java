package n7.ad2.setting;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;

import androidx.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import com.google.android.material.snackbar.Snackbar;
import androidx.appcompat.app.AlertDialog;
import android.view.View;

import n7.ad2.R;
import n7.ad2.databinding.ActivityLicenseBinding;
import n7.ad2.utils.BaseActivity;

import static n7.ad2.setting.SettingActivity.SUBSCRIPTION_PREF;
import static n7.ad2.splash.SplashViewModel.FREE_SUBSCRIPTION_DAYS;

public class LicensesActivity extends BaseActivity {

    ActivityLicenseBinding binding;
    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_license);

        binding.iv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MediaPlayer.create(getApplicationContext(), R.raw.does_this_unit_have_a_soul).start();
                final Snackbar snackbar = Snackbar.make(binding.getRoot(), "?..", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Yes, it does", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(FREE_SUBSCRIPTION_DAYS, 1).apply();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(SUBSCRIPTION_PREF, true).apply();
                        MediaPlayer.create(getApplicationContext(), R.raw.yes_it_does).start();
                        startBlinking();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
                return true;
            }
        });
    }

    private void startBlinking() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(
                        new ArgbEvaluator(),
                        0,
                        getResources().getColor(R.color.colorAccent_Dark),
                        getResources().getColor(R.color.colorAccent),
                        0
                );
                colorAnimation.setDuration(2000);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        binding.getRoot().setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                handler.postDelayed(this, 2000);
            }
        });
    }
}
