package n7.ad2.activity;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.arch.lifecycle.Lifecycle;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.transition.ChangeBounds;
import android.transition.Fade;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.transition.TransitionSet;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AnticipateOvershootInterpolator;
import android.widget.ImageView;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.List;

import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;

import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.OneTimeWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.adapter.PlainTextAdapter;
import n7.ad2.db.heroes.Heroes;
import n7.ad2.db.items.Items;
import n7.ad2.purchaseUtils.IabBroadcastReceiver;
import n7.ad2.purchaseUtils.IabHelper;
import n7.ad2.purchaseUtils.IabResult;
import n7.ad2.purchaseUtils.Inventory;
import n7.ad2.utils.UnscrollableLinearLayoutManager;
import n7.ad2.viewModels.HeroesViewModel;
import n7.ad2.viewModels.ItemsViewModel;
import n7.ad2.worker.GamesWorker;
import n7.ad2.worker.SteamDbNewsWorker;
import n7.ad2.worker.SteamNewsWorker;

import static n7.ad2.MySharedPreferences.ANIMATION_DURATION;
import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.SETTINGS.SettingActivity.ONCE_PER_MONTH_SUBSCRIPTION;
import static n7.ad2.worker.GamesWorker.PAGE;
import static n7.ad2.worker.GamesWorker.UNIQUE_WORK;
import static n7.ad2.worker.SteamNewsWorker.DELETE_TABLE;

public class SplashActivity extends BaseActivity implements IabBroadcastReceiver.IabBroadcastListener {

    private RecyclerView recyclerView;
    private PlainTextAdapter adapter;
    private ConstraintLayout constraintLayout;
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private IabHelper mHelper;
    private BroadcastReceiver broadcastReceiver;
    private ImageView iv_activity_splash_logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MySharedPreferences.getSharedPreferences(this);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_1);

        initView();
        initSupportJsoupForOldDevices();
        initFirebaseAnalyics();
        initPurchaseHelper();
        initPlainTextAdapter();
        initConstraintAnimation();
        initAnimationOnSplashLogo();
        startSteamNewsWorker();
//        startGamesWorker();
        startPulsing();
        startDelayFinish();

    }

    private void initFirebaseAnalyics() {
        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
    }

    private void startDelayFinish() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                if (getLifecycle().getCurrentState() == Lifecycle.State.RESUMED) {
                    Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                    startActivity(intent);
                    finish();
                }
            }
        }, 6000);

    }

    private void initView() {
        iv_activity_splash_logo = findViewById(R.id.iv_activity_splash_logo);
        switch (PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.setting_theme_key), THEME_GRAY)) {
            default:
            case THEME_GRAY:
                iv_activity_splash_logo.setImageResource(R.drawable.icon_gray);
                break;
            case THEME_WHITE:
                iv_activity_splash_logo.setImageResource(R.drawable.icon_white);
                break;
            case THEME_DARK:
                iv_activity_splash_logo.setImageResource(R.drawable.icon_dark);
                break;
        }
    }

    private void initPurchaseHelper() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyr808/wY0nXtrh7WEp7ZXqkdZTcwgIaEfKvqKtjkR0KJm/O0B6mLAOn2lNYr8j/mrxprxUk1eLHJtUH2NGzCJzs40AdG5XL/2X34wQpKfLgHj95yybkOPQH7jtHdG15+jwkmT4X80icUpKfoEbHOLzwzQyUn97IdR4X2pQpbI4v5Xy6oYvBLtvcAZoYXUoTlUua+8N8ZGLFqTNlWeBk7rToC7jtXKEMDWYucjoMs2uyrZ2x04+/KFwakDH12MMsNmT1Xuo5Vx6gwZ9QflT6cMd5y0xQlXlGoWeeFUIEIMfyV4s1BSPs3LiYSgNrYLLJ24pznoPtW26Ro4xRpOV4cyQIDAQAB";
        mHelper = new IabHelper(this, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    broadcastReceiver = new IabBroadcastReceiver(SplashActivity.this);
                    IntentFilter intentFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
                    registerReceiver(broadcastReceiver, intentFilter);
                    checkInventory();
                }
            }
        });

    }

    @Override
    protected void onPause() {
        super.onPause();
        if (broadcastReceiver != null)
            try {
                unregisterReceiver(broadcastReceiver);
            } catch (Exception e) {
                e.printStackTrace();
            }
    }

    private void checkInventory() {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (mHelper == null) return;
                    if (result.isFailure()) return;
                    if (inv.hasPurchase(ONCE_PER_MONTH_SUBSCRIPTION)) {
                        MySharedPreferences.getSharedPreferences(SplashActivity.this).edit().putBoolean(PREMIUM, true).apply();
                    } else {
                        Calendar calendar = Calendar.getInstance();
                        Long inMemoryTime = MySharedPreferences.getSharedPreferences(SplashActivity.this).getLong(MySharedPreferences.DATE_END_PREMIUM, 0);
                        Long currentTime = calendar.getTimeInMillis();
                        if (inMemoryTime > currentTime) {
                            MySharedPreferences.getSharedPreferences(SplashActivity.this).edit().putBoolean(MySharedPreferences.PREMIUM, true).apply();
                        } else {
                            MySharedPreferences.getSharedPreferences(SplashActivity.this).edit().putBoolean(MySharedPreferences.PREMIUM, false).apply();
                        }
                    }
                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }


    private void initSupportJsoupForOldDevices() {
        try {
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            SSLEngine engine = sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException e) {
            e.printStackTrace();
        } catch (GooglePlayServicesNotAvailableException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        }

    }

    private void startGamesWorker() {
        Data data = new Data.Builder().putInt(PAGE, 0).putBoolean(GamesWorker.DELETE_TABLE, true).build();
        OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(GamesWorker.class).setInputData(data).build();
        WorkManager.getInstance().beginUniqueWork(UNIQUE_WORK, ExistingWorkPolicy.KEEP, worker).enqueue();

        WorkManager.getInstance().getStatusesForUniqueWork(GamesWorker.UNIQUE_WORK).observe(this, new Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                if (workStatuses != null && workStatuses.size() > 0) {
                    if (workStatuses.get(0).getState() == State.RUNNING) {
                        log("work_games_running");
                    } else if (workStatuses.get(0).getState() == State.ENQUEUED) {
                        log("work_games_enqueued");
                    } else if (workStatuses.get(0).getState() == State.SUCCEEDED) {
                        log("work_games_succeeded");
                    } else if (workStatuses.get(0).getState() == State.FAILED) {
                        log("work_games_failed");
                    }
                }
            }
        });
    }

    private void startSteamNewsWorker() {
        Data data = new Data.Builder().putBoolean(DELETE_TABLE, true).build();
        OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(SteamDbNewsWorker.class).setInputData(data).build();
        WorkManager.getInstance().beginUniqueWork(SteamNewsWorker.UNIQUE_WORK, ExistingWorkPolicy.KEEP, worker).enqueue();

        WorkManager.getInstance().getStatusesForUniqueWork(SteamDbNewsWorker.UNIQUE_WORK).observe(this, new Observer<List<WorkStatus>>() {
            @Override
            public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                if (workStatuses != null && workStatuses.size() > 0) {
                    if (workStatuses.get(0).getState() == State.RUNNING) {
                        log("work_news_running");
                    } else if (workStatuses.get(0).getState() == State.ENQUEUED) {
                        log("work_news_enqueued");
                    } else if (workStatuses.get(0).getState() == State.SUCCEEDED) {
                        log("work_news_succeeded");
                    } else if (workStatuses.get(0).getState() == State.FAILED) {
                        log("work_news_failed");
                    }
                }
            }
        });
    }

    private void initAnimationOnSplashLogo() {
        iv_activity_splash_logo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                currentSet = (currentSet == constraintSetNew ? constraintSetOrigin : constraintSetNew);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                }, ANIMATION_DURATION);
                startConstraintAnimation();
            }
        });
    }

    private void startPulsing() {
        ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(iv_activity_splash_logo,
                PropertyValuesHolder.ofFloat(View.SCALE_X, 1.1f),
                PropertyValuesHolder.ofFloat(View.SCALE_Y, 1.1f));
        scaleDown.setDuration(1000L);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);
        scaleDown.start();
    }

    private void initConstraintAnimation() {
        constraintLayout = findViewById(R.id.root);
        //содержит информацию о всех состояних view
        constraintSetOrigin.clone(constraintLayout);
        constraintSetNew.clone(this, R.layout.activity_splash_0);
    }

    private void startConstraintAnimation() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            if (currentSet == constraintSetNew) {
                TransitionSet transitionSet = null;
                transitionSet = new TransitionSet();
//        Transition slide = new Slide(Gravity.LEFT); //появление из любого края активити
//        Transition explode = new Explode(); //почти как Slide но может выбигать из любой точки
//            Transition changeImageTransform = new ChangeImageTransform();//анимирует матричный переход изображений внутри ImageView
                Transition fade = new Fade().setDuration(ANIMATION_DURATION).addTarget(iv_activity_splash_logo);
                Transition changeBoundsIV = new ChangeBounds().setDuration(ANIMATION_DURATION).setInterpolator(new AnticipateOvershootInterpolator(2.0f)).addTarget(iv_activity_splash_logo);
                Transition changeBoundsRV = new ChangeBounds().setDuration(ANIMATION_DURATION).addTarget(R.id.rv_activity_splash);
                transitionSet.addTransition(fade).addTransition(changeBoundsRV).addTransition(changeBoundsIV);
                transitionSet.setOrdering(TransitionSet.ORDERING_TOGETHER);
                TransitionManager.beginDelayedTransition(constraintLayout, transitionSet);// вызываем метод, говорящий о том, что мы хотим анимировать следующие изменения внутри constraintLayout
                currentSet.applyTo(constraintLayout);// применяем изменения
            } else {
//            TransitionManager.beginDelayedTransition(constraintLayout);
//            currentSet.applyTo(constraintLayout);
            }
    }

    private void initPlainTextAdapter() {
        recyclerView = findViewById(R.id.rv_activity_splash);
        HeroesViewModel heroesViewModel = ViewModelProviders.of(this).get(HeroesViewModel.class);
        adapter = new PlainTextAdapter(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        heroesViewModel.getHeroes().observe(this, new Observer<List<Heroes>>() {
            @Override
            public void onChanged(@Nullable List<Heroes> heroes) {
                if (heroes != null)
                    for (Heroes hero : heroes) {
                        log(hero.getCodeName());
                    }
            }
        });
        ItemsViewModel itemsViewModel = ViewModelProviders.of(this).get(ItemsViewModel.class);
        itemsViewModel.getItems().observe(this, new Observer<List<Items>>() {
            @Override
            public void onChanged(@Nullable List<Items> items) {
                if (items != null)
                    for (Items item : items) {
                        log(item.getCodeName());
                    }
            }
        });
    }

    private void log(String item) {
        if (adapter != null) {
            adapter.add(item);
            recyclerView.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    @Override
    public void receivedBroadcast() {
//        checkInventory();
    }

}
