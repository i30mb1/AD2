package n7.ad2.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.design.widget.Snackbar;
import android.view.View;

import java.util.Calendar;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.databinding.ActivityLicenseBinding;

public class LicensesActivity extends BaseActivity {

    private Handler handler;
    ActivityLicenseBinding binding;

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
                        Calendar calendar = Calendar.getInstance();
                        calendar.add(Calendar.YEAR, 1);
                        MySharedPreferences.getSharedPreferences(getApplicationContext()).edit().putLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis()).apply();
                        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(MySharedPreferences.SUBSCRIPTION, true).apply();
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
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), 0, getResources().getColor(R.color.colorAccent_Dark), getResources().getColor(R.color.colorAccent), 0);
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
