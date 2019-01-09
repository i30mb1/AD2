package n7.ad2.main;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.transition.ChangeBounds;
import android.support.transition.TransitionManager;
import android.support.transition.TransitionSet;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import java.util.concurrent.TimeUnit;

import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
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

    public static final int COUNTER_DIALOG_RATE = 20;
    public static final int COUNTER_DIALOG_DONATE = 30;

    public static final String FIREBASE_DIALOG_DONATE_SAW = "DIALOG_DONATE_SAW";
    public static final String FIREBASE_DIALOG_PRE_DONATE_SAW = "DIALOG_PRE_DONATE_SAW";
    public static final String FIREBASE_DIALOG_RATE_SAW = "DIALOG_RATE_SAW";
    public static final String FIREBASE_DIALOG_RATE_CLICK = "DIALOG_RATE_CLICK";
    public static final String LAST_ITEM = "LAST_ITEM";
    public static final int MILLIS_FOR_EXIT = 2000;
    public static final String GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app/release/app-release.apk?raw=true";
    public static final String ADMOB_ID = "ca-app-pub-5742225922710304/8697652489";
    public static final String ADMOB_ID_FAKE = "ca-app-pub-3940256099942544/5224354917";
    public static final String LOG_ON_RECEIVE = "log";
    public static final String DIALOG_RATE_SAW = "DIALOG_RATE_SAW";
    public static final String DIALOG_VIDEO_AD_SAW = "DIALOG_VIDEO_AD_SAW";
    private static final String DIALOG_PRE_DONATE_LAST_DAY = "DIALOG_PRE_DONATE_LAST_DAY";
    public ObservableInt observableLastItem = new ObservableInt(1);
    public ObservableBoolean freeSubscriptionButtonVisibility = new ObservableBoolean(false);
    private int enterCounter = 0;
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
    private SlidingRootNav drawer;
    private RewardedVideoAd rewardedVideoAd;
    private boolean subscription;
    private int currentDay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setupRewardedVideoAd();
        currentDay = PreferenceManager.getDefaultSharedPreferences(this).getInt(CURRENT_DAY_IN_APP, 0);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindingActivity.setViewModel(viewModel);

        bindingDrawer = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer, null, false);
        bindingDrawer.setViewModel(viewModel);
        bindingDrawer.setActivity(this);

        setupToolbar();
        setupDrawer();
        setupRecyclerView();
        setupListeners();
        setupSecretActivity();

        setLastFragment();

        log("on_Create");
    }

    public void subscriptionButtonState() {
        subscription = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(SUBSCRIPTION_PREF, false);
        if (subscription) {
            freeSubscriptionButtonVisibility.set(false);
            return;
        }
        if (rewardedVideoAd != null && rewardedVideoAd.isLoaded()) {
            freeSubscriptionButtonVisibility.set(true);
        } else {
            freeSubscriptionButtonVisibility.set(false);
            loadVideoAD();
        }
    }

    public void showVideoAD(View view) {
        int lastDayVideoAD = PreferenceManager.getDefaultSharedPreferences(this).getInt(DIALOG_VIDEO_AD_SAW, 0);

        if (lastDayVideoAD == currentDay) {
            rewardedVideoAd.show();
        } else {
            showDialogForVideoAD();
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogForVideoAD() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        DialogVideoAdBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_video_ad, null, false);
        builder.setView(binding.getRoot());

        final AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        binding.bDialogVideoAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                showVideoAD(v);
            }
        });

        PreferenceManager.getDefaultSharedPreferences(this).edit().putInt(DIALOG_VIDEO_AD_SAW, currentDay).apply();
    }

    private void loadVideoAD() {
        if (rewardedVideoAd != null)
            rewardedVideoAd.loadAd(ADMOB_ID_FAKE, new AdRequest.Builder().build());
    }

    public void setupRewardedVideoAd() {
        subscription = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getBoolean(SUBSCRIPTION_PREF, false);
        if (subscription) return;
        rewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
        rewardedVideoAd.setRewardedVideoAdListener(new RewardedVideoAdListener() {
            @Override
            public void onRewardedVideoAdLoaded() {
                subscriptionButtonState();
            }

            @Override
            public void onRewardedVideoAdOpened() {

            }

            @Override
            public void onRewardedVideoStarted() {

            }

            @Override
            public void onRewardedVideoAdClosed() {
//                loadVideoAD();
            }

            @Override
            public void onRewarded(RewardItem rewardItem) {
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putBoolean(SUBSCRIPTION_PREF, true).apply();
                log("free_subscription = +10 min");
                OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(ADRewardWorker.class)
                        .setInitialDelay(rewardItem.getAmount(), TimeUnit.MINUTES).build();
//                        .setInitialDelay(30, TimeUnit.SECONDS).build();
                WorkManager.getInstance().enqueue(worker);
            }

            @Override
            public void onRewardedVideoAdLeftApplication() {

            }

            @Override
            public void onRewardedVideoAdFailedToLoad(int i) {

            }

            @Override
            public void onRewardedVideoCompleted() {

            }
        });
        loadVideoAD();
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

        DialogUpdateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_update, null, false);
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

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
        enterCounter++;
        if (enterCounter > COUNTER_DIALOG_RATE) showDialogRate();
        if (enterCounter > COUNTER_DIALOG_DONATE) showPreDialogDonate();
    }

    @SuppressWarnings("ConstantConditions")
    void showPreDialogDonate() {
        if (!subscription) {
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
                enterCounter = 0;
            }
        } else {
            enterCounter = 0;
        }

    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogRate() {
        boolean showDialogRate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(DIALOG_RATE_SAW, true);
        if (showDialogRate && !subscription) {
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
            PreferenceManager.getDefaultSharedPreferences(this).edit().putBoolean(DIALOG_RATE_SAW, false).apply();
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.logEvent(FIREBASE_DIALOG_RATE_SAW, null);
        }
        if (subscription) {
            enterCounter = 0;
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

    private void setupDrawer() {
        drawer = new SlidingRootNavBuilder(this)
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
        subscriptionButtonState();
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

}
