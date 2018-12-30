package n7.ad2.activity;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.view.View;
import android.widget.ImageView;

import java.util.Calendar;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;

import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.MySharedPreferences.SHOW_PREMIUM_SWITCH;


public class LicensesActivity extends BaseActivity {

    private Handler handler;
    private ImageView iv_activity_open_source_licenses;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_open_source_licenses);
        iv_activity_open_source_licenses = findViewById(R.id.iv_activity_open_source_licenses);
        iv_activity_open_source_licenses.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                MediaPlayer.create(getApplicationContext(), R.raw.does_this_unit_have_a_soul).start();
                final Snackbar snackbar = Snackbar.make(iv_activity_open_source_licenses, "?..", Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction("Yes, it does", new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        SHOW_PREMIUM_SWITCH = true;
//                        MySharedPreferences.getSharedPreferences(LicensesActivity.this).edit().putBoolean(PREMIUM, true).apply();
//                        Calendar calendar = Calendar.getInstance();
//                        calendar.add(Calendar.YEAR, 1);
//                        MySharedPreferences.getSharedPreferences(LicensesActivity.this).edit().putLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis()).apply();
                        MediaPlayer.create(getApplicationContext(), R.raw.yes_it_does).start();
                        startBlinkBackground();
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
                return true;
            }
        });
    }

    private void startBlinkBackground() {
        handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), 0, getResources().getColor(R.color.colorAccent_Dark), getResources().getColor(R.color.colorAccent), 0);
                colorAnimation.setDuration(2000);
                colorAnimation.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animator) {
                        findViewById(R.id.root).setBackgroundColor((int) animator.getAnimatedValue());
                    }
                });
                colorAnimation.start();
                handler.postDelayed(this, 2000);
            }
        });
    }
}
