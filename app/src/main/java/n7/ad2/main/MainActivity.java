package n7.ad2.main;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.AssetFileDescriptor;
import android.content.res.AssetManager;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.graphics.Rect;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.BottomSheetDialog;
import android.support.design.widget.Snackbar;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import java.io.InputStream;
import java.util.concurrent.TimeUnit;

import n7.ad2.R;
import n7.ad2.databinding.ActivityMainBinding;
import n7.ad2.databinding.DialogPreDonateBinding;
import n7.ad2.databinding.DialogRateBinding;
import n7.ad2.databinding.DialogUpdateBinding;
import n7.ad2.databinding.DialogVideoAdBinding;
import n7.ad2.databinding.DrawerBinding;
import n7.ad2.games.GameFragment;
import n7.ad2.heroes.HeroesFragment;
import n7.ad2.items.ItemsFragment;
import n7.ad2.news.NewsFragment;
import n7.ad2.setting.SettingActivity;
import n7.ad2.streams.StreamsFragment;
import n7.ad2.streams.retrofit.Stream;
import n7.ad2.tournaments.TournamentsFragment;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.PlainAdapter;
import n7.ad2.utils.SnackbarUtils;
import n7.ad2.utils.UnscrollableLinearLayoutManager;

import static n7.ad2.main.MainViewModel.LAST_DAY_WHEN_CHECK_UPDATE;
import static n7.ad2.main.MainViewModel.SHOULD_UPDATE_FROM_MARKET;
import static n7.ad2.setting.SettingActivity.INTENT_SHOW_DIALOG_DONATE;
import static n7.ad2.setting.SettingActivity.SUBSCRIPTION_PREF;
import static n7.ad2.splash.SplashViewModel.CURRENT_DAY_IN_APP;
import static n7.ad2.splash.SplashViewModel.FREE_SUBSCRIPTION_DAYS;

public class MainActivity extends BaseActivity {

    public static final int COUNTER_DIALOG_RATE = 11;
    public static final int COUNTER_DIALOG_DONATE = 18;
    public static final String FREE_SUBSCRIPTION_COUNTER = "FREE_SUBSCRIPTION_COUNTER";

    public static final String FIREBASE_DIALOG_DONATE_SAW = "DIALOG_DONATE_SAW";
    public static final String FIREBASE_DIALOG_PRE_DONATE_SAW = "DIALOG_PRE_DONATE_SAW";
    public static final String FIREBASE_DIALOG_RATE_SAW = "DIALOG_RATE_SHOW";
    public static final String FIREBASE_DIALOG_RATE_CLICK = "DIALOG_RATE_CLICK";
    public static final String LAST_ITEM = "LAST_ITEM";
    public static final int MILLIS_FOR_EXIT = 2000;
    public static final String GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app/release/app-release.apk?raw=true";
    public static final String ADMOB_ID = "ca-app-pub-5742225922710304/8697652489";
    public static final String ADMOB_ID_FAKE = "ca-app-pub-3940256099942544/5224354917";
    public static final String ADMOB_ID_BACK = "ca-app-pub-5742225922710304/6328775876";
    public static final String ADMOB_ID_BACK_FAKE = "ca-app-pub-3940256099942544/1033173712";
    public static final String LOG_ON_RECEIVE = "log";
    public static final String DIALOG_RATE_SHOW = "DIALOG_RATE_SHOW";
    public static final String DIALOG_VIDEO_AD_SAW = "DIALOG_VIDEO_AD_SAW";
    public static final int ACTION_BEFORE_SHOW_ADVERTISEMENT = 3;
    private static final String DIALOG_PRE_DONATE_LAST_DAY = "DIALOG_PRE_DONATE_LAST_DAY";
    public ObservableInt observableLastItem = new ObservableInt(1);
    public ObservableBoolean rewardedVideoLoaded = new ObservableBoolean(false);
    public ObservableInt freeSubscriptionCounter = new ObservableInt(0);
    public ObservableBoolean subscription = new ObservableBoolean(false);
    private int timeCounter = -1;
    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintSet constraintSetHidden = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private PlainAdapter adapter;
    private ActivityMainBinding bindingActivity;
    private DrawerBinding bindingDrawer;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getStringExtra(LOG_ON_RECEIVE);
            log(string);
        }
    };
    private boolean shouldUpdateFromMarket;
    private MainViewModel viewModel;
    private RewardedVideoAd rewardedVideoAd;
    private int currentDay;
    private InterstitialAd interstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        updateFreeSubscriptionCounter();
        currentDay = PreferenceManager.getDefaultSharedPreferences(this).getInt(CURRENT_DAY_IN_APP, 0);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindingActivity.setViewModel(viewModel);

        bindingDrawer = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer, null, false);
        bindingDrawer.setViewModel(viewModel);
        bindingDrawer.setActivity(this);

        setupRecyclerView();
        log("on_Create");
        setupToolbar();
        setupDrawer();
        setupListeners();
        setupSecretActivity();
        setupAD();

        setLastFragment();

    }

    private void updateFreeSubscriptionCounter() {
        freeSubscriptionCounter.set(PreferenceManager.getDefaultSharedPreferences(this).getInt(FREE_SUBSCRIPTION_COUNTER, 0));
    }

    public void activateSubscription(View view) {
        if (freeSubscriptionCounter.get() <= 0) {
            Snackbar.make(bindingActivity.getRoot(), R.string.zero_free_count_subscription, Snackbar.LENGTH_SHORT).show();
            freeSubscriptionCounter.set(0);
        } else {
            activateFreeSubscription();
        }
    }

    public void showVideoAD(View view) {
        int lastDayTipsForVideoAD = PreferenceManager.getDefaultSharedPreferences(this).getInt(DIALOG_VIDEO_AD_SAW, 0);

        if (lastDayTipsForVideoAD == currentDay) {
            if (rewardedVideoAd != null) rewardedVideoAd.show();
        } else {
            showDialogBeforeVideoAD();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogBeforeVideoAD() {
//        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        DialogVideoAdBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_video_ad, null, false);
//        builder.setView(binding.getRoot());
//
//        final AlertDialog dialog = builder.create();
//        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
//        dialog.show();
//
//        binding.bDialogVideoAd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                dialog.dismiss();
//                showVideoAD(v);
//            }
//        });

        final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        DialogVideoAdBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_video_ad, null, false);
        bottomSheetDialog.setContentView(binding.getRoot());
        binding.bDialogVideoAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                bottomSheetDialog.dismiss();
                showVideoAD(v);
            }
        });
        bottomSheetDialog.show();
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(DIALOG_VIDEO_AD_SAW, currentDay).apply();
    }

    public void setupAD() {
        subscription.set(PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(SUBSCRIPTION_PREF, false));
        if (subscription.get()) {
            log("AD_disabled");
        } else {
            log("AD_enabled");
//            setupVideoAD();
            setupInterstitialAD();
        }
    }

    private void setupVideoAD() {
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                log("rewarded_video_loaded");
                rewardedVideoLoaded.set(true);
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {
                rewardedVideoLoaded.set(false);
            }

            @Override
            public void onRewardedVideoAdClosed() {
                rewardedVideoLoaded.set(false);
                loadVideoAD();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                log("free_subscription_+1_usage");
                plusOneFreeSubscriptionCounter();
                updateFreeSubscriptionCounter();
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {
                log("failed_to_load_rewarded_video");
            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        loadVideoAD();
    }

    private void activateFreeSubscription() {
        freeSubscriptionCounter.set(freeSubscriptionCounter.get() - 1);
        subscription.set(true);
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean(SUBSCRIPTION_PREF, true).apply();
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putInt(FREE_SUBSCRIPTION_COUNTER, freeSubscriptionCounter.get()).apply();
        log("free_subscription_-1_usage");
        log("free_subscription_+10_min");
        OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(ADRewardWorker.class)
                .setInitialDelay(10, TimeUnit.MINUTES).build();
        WorkManager.getInstance().enqueue(worker);
    }

    private void plusOneFreeSubscriptionCounter() {
        PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putInt(FREE_SUBSCRIPTION_COUNTER, (freeSubscriptionCounter.get() + 1)).apply();
    }

    private void loadVideoAD() {
        if (rewardedVideoAd != null && !rewardedVideoAd.isLoaded()) {
            rewardedVideoAd.loadAd(ADMOB_ID, new AdRequest.Builder().build());
        }
    }

    private void setupInterstitialAD() {
        interstitialAd = new InterstitialAd(this);
        interstitialAd.setAdUnitId(ADMOB_ID_BACK);
        interstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                super.onAdClosed();
                loadInterstitialAD();
            }
        });
        loadInterstitialAD();
    }

    private void loadInterstitialAD() {
        if (interstitialAd != null) {
            interstitialAd.loadAd(new AdRequest.Builder().build());
        }
    }

    private void ShowInterstitialAd() {
        if (!subscription.get()) {
            if (interstitialAd != null && interstitialAd.isLoaded()) {
                interstitialAd.show();
            }
        }
    }

    private void setupListeners() {
        viewModel.snackbarMessage.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@StringRes Integer redId) {
                SnackbarUtils.showSnackbar(bindingActivity.getRoot(), getString(redId));
            }
        });
        viewModel.showDialogUpdate.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                showDialogUpdate();
            }
        });
    }

    public void setLastFragment() {
        int lastItem = PreferenceManager.getDefaultSharedPreferences(this).getInt(LAST_ITEM, 1);
        observableLastItem.set(lastItem);
        setFragment(lastItem, false);
    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        final DialogUpdateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_update, null, false);
        builder.setView(binding.getRoot());

        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();
        binding.bDialogUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                loadNewVersion();
            }
        });

        shouldUpdateFromMarket = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SHOULD_UPDATE_FROM_MARKET, true);
        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putInt(LAST_DAY_WHEN_CHECK_UPDATE, currentDay).apply();
            }
        });

    }

    private void setupToolbar() {
        setSupportActionBar(bindingActivity.toolbarActivityMain);
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("on_Start");

    }

    private void setupRecyclerView() {
        boolean shouldDisplayLog = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.setting_log_key), true);
        if (shouldDisplayLog) {
            adapter = viewModel.getAdapter();
            bindingDrawer.rvDrawer.setAdapter(adapter);
            bindingDrawer.rvDrawer.setLayoutManager(new UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void log(String text) {
        if (adapter != null) {
            adapter.add(text);
            adapter.notifyDataSetChanged();
//            adapter.notifyItemChanged(adapter.getItemCount());
            bindingDrawer.rvDrawer.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public void setFragment(int fragmentID, boolean closeDrawer) {
        observableLastItem.set(fragmentID);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (fragmentID) {
            default:
            case 1:
                ft.replace(bindingActivity.containerActivityMain.getId(), new HeroesFragment()).commit();
                break;
            case 2:
                ft.replace(bindingActivity.containerActivityMain.getId(), new ItemsFragment()).commit();
                break;
            case 3:
                ft.replace(bindingActivity.containerActivityMain.getId(), new NewsFragment()).commit();
                break;
            case 4:
                ft.replace(bindingActivity.containerActivityMain.getId(), new TournamentsFragment()).commit();
                break;
            case 5:
                ft.replace(bindingActivity.containerActivityMain.getId(), new StreamsFragment()).commit();
                break;
            case 6:
                ft.replace(bindingActivity.containerActivityMain.getId(), new GameFragment()).commit();
                break;
        }
//        if (closeDrawer)
//            new Handler().postDelayed(new Runnable() {
//                @Override
//                public void run() {
//                    drawer.closeMenu();
//                }
//            }, 50);
    }

    private void setupSecretActivity() {
        constraintSetOrigin.clone((ConstraintLayout) bindingDrawer.getRoot());
        constraintSetHidden.clone(this, R.layout.drawer_hidden);
        currentSet = constraintSetOrigin;
    }

    public boolean toggleSecretActivity(View view) {
        currentSet = (currentSet == constraintSetOrigin ? constraintSetHidden : constraintSetOrigin);
        TransitionSet transitionSet = new TransitionSet()
                .setDuration(500)
                .addTransition(new ChangeBounds().setInterpolator(new LinearInterpolator()));
        //после этого метода все изменения внутри ViewGroup будут анимированы
        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), transitionSet);
//        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), new AutoTransition());
        //применяет все изменения находящиеся в currentSet с анимациями из transitionSet
        currentSet.applyTo((ConstraintLayout) bindingDrawer.getRoot());
        if (currentSet == constraintSetOrigin) {
            bindingActivity.getRoot().animate().alpha(1.0f).setDuration(500).start();
        } else {
            bindingActivity.getRoot().animate().alpha(0.0f).setDuration(500).start();
        }
        return true;
    }

    public void loadNewVersion() {
        if (shouldUpdateFromMarket) {
            loadNewVersionFromMarket();
        } else {
            loadNewVersionFromGitHub();
        }
    }

    private void loadNewVersionFromMarket() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException a) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void loadNewVersionFromGitHub() {
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(GITHUB_LAST_APK_URL));
        request.setDescription(getString(R.string.all_new_version));
        request.setTitle(getString(R.string.app_name));
        request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.app_name));
        DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        manager.enqueue(request);
    }

    private void incCountEnter() {
        timeCounter++;
        if (timeCounter > COUNTER_DIALOG_RATE) showDialogRate();
        if (timeCounter > COUNTER_DIALOG_DONATE) showPreDialogDonate();
        if (timeCounter % ACTION_BEFORE_SHOW_ADVERTISEMENT == 0) ShowInterstitialAd();
    }

    @SuppressWarnings("ConstantConditions")
    void showPreDialogDonate() {
        if (!subscription.get()) {
            int lastDayDialog = PreferenceManager.getDefaultSharedPreferences(this).getInt(DIALOG_PRE_DONATE_LAST_DAY, 0);
            if (currentDay > lastDayDialog) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                DialogPreDonateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_pre_donate, null, false);
                builder.setView(binding.getRoot());
                binding.setActivity(this);

                final AlertDialog dialog = builder.create();
                dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
                dialog.show();

                binding.dialogPreDonate.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        startSettingWithDonate();
                    }
                });

                PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(DIALOG_PRE_DONATE_LAST_DAY, currentDay + 2).apply();
                FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
                firebaseAnalytics.logEvent(FIREBASE_DIALOG_PRE_DONATE_SAW, null);
            }
        }

    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogRate() {
        boolean showDialogRate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DIALOG_RATE_SHOW, true);
        if (showDialogRate && !subscription.get()) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DialogRateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_rate, null, false);
            binding.setActivity(this);
            builder.setView(binding.getRoot());

            final AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();

            binding.bDialogRateYes.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    openAppStore();
                }
            });

            binding.bDialogRateNo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(DIALOG_RATE_SHOW, false).apply();
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.logEvent(FIREBASE_DIALOG_RATE_SAW, null);
        }
    }

    public void startSettingWithDonate() {
        Intent intent = new Intent(MainActivity.this, SettingActivity.class);
        intent.putExtra(INTENT_SHOW_DIALOG_DONATE, true);
        startActivity(intent);
    }

    public void openAppStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException a) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }

        log("free_subscription = +2 days");
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putInt(FREE_SUBSCRIPTION_DAYS, 2).apply();
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(SUBSCRIPTION_PREF, true).apply();

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        firebaseAnalytics.logEvent(FIREBASE_DIALOG_RATE_CLICK, null);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(bindingActivity.toolbarActivityMain.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        float[] x = new float[10];
        float[] y = new float[10];
        boolean[] touched = new boolean[10];

        // событие
        int action = ev.getActionMasked();
        // индекс косания
        int index = ev.getActionIndex();
        // id косания
        int id = ev.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                touched[id] = true;
                x[id] = ev.getX(index);
                y[id] = ev.getY(index);
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                touched[id] = false;
                x[id] = ev.getX(index);
                y[id] = ev.getY(index);
                break;
            case MotionEvent.ACTION_MOVE:
                // число косаний
                int count = ev.getPointerCount();
                for (int i = 0; i < count; i++) {
                    index = i;
                    id = ev.getPointerId(index);
                    x[id] = ev.getX(index);
                    y[id] = ev.getY(index);
                }
                break;
        }
        return false;
    }

    private void setupDrawer() {
        SlidingRootNav drawer = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(bindingActivity.toolbarActivityMain)
                .withDragDistance(110)
                .withRootViewScale(0.65f)
                .withRootViewElevation(8)
                .withRootViewYTranslation(0)
                .withContentClickableWhenMenuOpened(false)
                .addDragStateListener(new DragStateListener() {
                    @Override
                    public void onDragStart() {
                        hideKeyboard();
                    }

                    @Override
                    public void onDragEnd(boolean isMenuOpened) {

                    }
                })
                .withMenuView(bindingDrawer.getRoot())
                .inject();
        drawer.openMenu();
    }

    @Override
    protected void onPause() {
        if (rewardedVideoAd != null) rewardedVideoAd.pause(this);
        log("on_Pause");
        super.onPause();
    }

    @Override
    protected void onStop() {
        log("on_Stop");
        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(LAST_ITEM, observableLastItem.get()).apply();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        if (rewardedVideoAd != null) rewardedVideoAd.destroy(this);
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
        log("on_Destroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        if (rewardedVideoAd != null) rewardedVideoAd.resume(this);
        log("on_Resume");
        incCountEnter();
        regReceiver();
        super.onResume();
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(LOG_ON_RECEIVE);
        registerReceiver(broadcastReceiver, filter);
        //можно затригерить ресивер этой командой
//        getActivity().sendBroadcast(new Intent("setToolbarName"));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        if (currentSet == constraintSetHidden) {
            toggleSecretActivity(bindingActivity.getRoot());
            return;
        }
        doubleBackToExitPressedOnce = true;
        Snackbar.make(bindingActivity.getRoot(), R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, MILLIS_FOR_EXIT);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();
    }
}
