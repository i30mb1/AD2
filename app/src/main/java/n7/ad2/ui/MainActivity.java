package n7.ad2.ui;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.LinearInterpolator;
import android.view.inputmethod.InputMethodManager;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AlertDialog;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ObservableArrayList;
import androidx.databinding.ObservableBoolean;
import androidx.databinding.ObservableInt;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.transition.ChangeBounds;
import androidx.transition.TransitionManager;
import androidx.transition.TransitionSet;

import com.google.android.material.snackbar.Snackbar;
import com.google.android.play.core.appupdate.AppUpdateInfo;
import com.google.android.play.core.appupdate.AppUpdateManager;
import com.google.android.play.core.appupdate.AppUpdateManagerFactory;
import com.google.android.play.core.install.InstallState;
import com.google.android.play.core.install.InstallStateUpdatedListener;
import com.google.android.play.core.install.model.AppUpdateType;
import com.google.android.play.core.install.model.InstallStatus;
import com.google.android.play.core.install.model.UpdateAvailability;
import com.google.android.play.core.tasks.OnCompleteListener;
import com.google.android.play.core.tasks.OnSuccessListener;
import com.google.android.play.core.tasks.Task;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import java.util.Arrays;

import n7.ad2.R;
import n7.ad2.databinding.ActivityMainBinding;
import n7.ad2.databinding.DialogRateBinding;
import n7.ad2.databinding.DialogUpdateBinding;
import n7.ad2.databinding.DrawerBinding;
import n7.ad2.games.GameFragment;
import n7.ad2.heroes.HeroesFragment;
import n7.ad2.items.ItemsFragment;
import n7.ad2.main.MainViewModel;
import n7.ad2.news.NewsFragment;
import n7.ad2.streams.StreamsFragment;
import n7.ad2.tournaments.TournamentsFragment;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.PlainAdapter;
import n7.ad2.utils.SnackbarUtils;
import n7.ad2.utils.UnscrollableLinearLayoutManager;

import static n7.ad2.main.MainViewModel.LAST_DAY_WHEN_CHECK_UPDATE;
import static n7.ad2.main.MainViewModel.SHOULD_UPDATE_FROM_MARKET;

public class MainActivity extends BaseActivity {

    public static final int COUNTER_DIALOG_RATE = 10;
    public static final int COUNTER_DIALOG_DONATE = 15;
    public static final String FREE_SUBSCRIPTION_COUNTER = "FREE_SUBSCRIPTION_COUNTER";

    public static final String FIREBASE_DIALOG_DONATE_SAW = "DIALOG_DONATE_SAW";
    public static final String FIREBASE_DIALOG_PRE_DONATE_SAW = "DIALOG_PRE_DONATE_SAW";
    public static final String FIREBASE_DIALOG_RATE_SAW = "DIALOG_RATE_SHOW";
    public static final String FIREBASE_DIALOG_RATE_CLICK = "DIALOG_RATE_CLICK";
    public static final String LAST_ITEM = "LAST_ITEM";
    public static final int MILLIS_FOR_EXIT = 2000;
    public static final String GITHUB_LAST_APK_URL = "https://github.com/i30mb1/AD2/blob/master/app-release.apk?raw=true";
    public static final String ADMOB_ID = "ca-app-pub-5742225922710304/8697652489";
    public static final String ADMOB_ID_FAKE = "ca-app-pub-3940256099942544/5224354917";
    public static final String ADMOB_ID_BACK = "ca-app-pub-5742225922710304/6328775876";
    public static final String ADMOB_ID_BACK_FAKE = "ca-app-pub-3940256099942544/1033173712";
    public static final String LOG_ON_RECEIVE = "log";
    public static final String DIALOG_RATE_SHOW = "DIALOG_RATE_SHOW";
    public static final String DIALOG_VIDEO_AD_SAW = "DIALOG_VIDEO_AD_SAW";
    public static final int ACTION_BEFORE_SHOW_ADVERTISEMENT = 3;
    public static final String EASTER_EGG_ACTIVATED = "EASTER_EGG_ACTIVATED";
    private static final String DIALOG_PRE_DONATE_LAST_DAY = "DIALOG_PRE_DONATE_LAST_DAY";
    private static final int MY_REQUEST_CODE_UPDATE = 17;
    public ObservableInt observableLastItem = new ObservableInt(1);
    public ObservableBoolean rewardedVideoLoaded = new ObservableBoolean(false);
    public ObservableInt freeSubscriptionCounter = new ObservableInt(0);
    public ObservableBoolean subscription = new ObservableBoolean(false);
    ObservableArrayList<Float> movementListX = new ObservableArrayList<>();
    ObservableArrayList<Float> movementListY = new ObservableArrayList<>();
    private boolean easter_egg_value = false;
    private int timeCounter = -1;
    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintSet constraintSetHidden = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private PlainAdapter adapter;
    private ActivityMainBinding binding;
    private DrawerBinding drawer;
    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String string = intent.getStringExtra(LOG_ON_RECEIVE);
            log(string);
        }
    };
    private boolean shouldUpdateFromMarket = true;
    private MainViewModel viewModel;
    private int currentDay;
    private boolean shouldDisplayLog;
    private AppUpdateManager appUpdateManager;
    InstallStateUpdatedListener UpdateListener = new InstallStateUpdatedListener() {
        @Override
        public void onStateUpdate(InstallState installState) {
            if (installState.installStatus() == InstallStatus.DOWNLOADED) {
                popupSnackbarForCompleteUpdate();
                if (appUpdateManager != null) appUpdateManager.unregisterListener(UpdateListener);
            }
        }
    };
    private boolean modeSecretActivity = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        currentDay = PreferenceManager.getDefaultSharedPreferences(this).getInt(getString(R.string.setting_current_day), 0);
        viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        binding.setViewModel(viewModel);

        drawer = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer, null, false);
        drawer.setViewModel(viewModel);
        movementListX.addAll(Arrays.asList(new Float[10]));
        movementListY.addAll(Arrays.asList(new Float[10]));
        drawer.setArrayX(movementListX);
        drawer.setArrayY(movementListY);
        drawer.setActivity(this);

        setupRecyclerView();
        log("on_Create");
        setupToolbar();
        setupDrawer();
        setupListeners();
        setupSecretActivity();
        setLastFragment();

    }

    private void setupListeners() {
        viewModel.snackbarMessage.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@StringRes Integer redId) {
                SnackbarUtils.showSnackbar(binding.getRoot(), getString(redId));
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
        dialog.getWindow().getAttributes().windowAnimations = R.style.MyMaterialAlertDialogTheme;
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
        setSupportActionBar(binding.toolbar);
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("on_Start");

    }

    private void setupRecyclerView() {
        shouldDisplayLog = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.setting_log_key), true);
        if (shouldDisplayLog) {
            adapter = viewModel.getAdapter();
            drawer.rvDrawer.setAdapter(adapter);
            drawer.rvDrawer.setLayoutManager(new UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void log(String text) {
        if (adapter != null) {
            adapter.add(text);
            adapter.notifyDataSetChanged();
//            adapter.notifyItemChanged(adapter.getItemCount());
            drawer.rvDrawer.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public void setFragment(int fragmentID, boolean closeDrawer) {
        observableLastItem.set(fragmentID);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        switch (fragmentID) {
            default:
            case 1:
                ft.replace(binding.container.getId(), new HeroesFragment()).commit();
                break;
            case 2:
                ft.replace(binding.container.getId(), new ItemsFragment()).commit();
                break;
            case 3:
                ft.replace(binding.container.getId(), new NewsFragment()).commit();
                break;
            case 4:
                ft.replace(binding.container.getId(), new TournamentsFragment()).commit();
                break;
            case 5:
                ft.replace(binding.container.getId(), new StreamsFragment()).commit();
                break;
            case 6:
                ft.replace(binding.container.getId(), new GameFragment()).commit();
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
        constraintSetOrigin.clone((ConstraintLayout) drawer.getRoot());
        constraintSetHidden.clone(this, R.layout.drawer_hidden);
        currentSet = constraintSetOrigin;
    }

    public boolean toggleSecretActivity(View view) {
        currentSet = (currentSet == constraintSetOrigin ? constraintSetHidden : constraintSetOrigin);
        TransitionSet transitionSet = new TransitionSet()
                .setDuration(500)
                .addTransition(new ChangeBounds().setInterpolator(new LinearInterpolator()));
        //после этого метода все изменения внутри ViewGroup будут анимированы
        TransitionManager.beginDelayedTransition((ViewGroup) drawer.getRoot(), transitionSet);
//        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), new AutoTransition());
        //применяет все изменения находящиеся в currentSet с анимациями из transitionSet
        currentSet.applyTo((ConstraintLayout) drawer.getRoot());
        TransitionManager.beginDelayedTransition((ViewGroup) binding.getRoot());
        if (currentSet == constraintSetOrigin) {
            modeSecretActivity = false;
            binding.getRoot().setVisibility(View.VISIBLE);
        } else {
            modeSecretActivity = true;
            binding.getRoot().setVisibility(View.INVISIBLE);
        }
        return true;
    }

    public void loadNewVersion() {
        if (shouldUpdateFromMarket) {
            checkInstallUpdate();
        } else {
            loadNewVersionFromGitHub();
        }
    }

    private void loadNewVersionFlexible() {
        // Creates instance of the manager.
        if (appUpdateManager == null) appUpdateManager = AppUpdateManagerFactory.create(this);
        // Returns an intent object that you use to check for an update.
        Task<AppUpdateInfo> appUpdateInfoTask = appUpdateManager.getAppUpdateInfo();
        // Checks that the platform will allow the specified type of update.
        appUpdateInfoTask.addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {

                if (appUpdateInfo.updateAvailability() == UpdateAvailability.UPDATE_AVAILABLE
                        // For a flexible update, use AppUpdateType.FLEXIBLE
                        && appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.FLEXIBLE)) {
                    // Request the update.
                    try {
                        appUpdateManager.startUpdateFlowForResult(
                                // Pass the intent that is returned by 'getAppUpdateInfo()'.
                                appUpdateInfo,
                                // Or 'AppUpdateType.FLEXIBLE' for flexible updates.
                                AppUpdateType.FLEXIBLE,
                                // The current activity making the update request.
                                MainActivity.this,
                                // Include a request code to later monitor this update request.
                                MY_REQUEST_CODE_UPDATE);
                    } catch (IntentSender.SendIntentException e) {
                        log("load_version_flexible = failed");
                        loadNewVersionFromMarket();
                    }
                }
            }
        });
        appUpdateInfoTask.addOnCompleteListener(new OnCompleteListener<AppUpdateInfo>() {
            @Override
            public void onComplete(Task<AppUpdateInfo> task) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MY_REQUEST_CODE_UPDATE) {
            if (resultCode == RESULT_OK) {
                if (appUpdateManager != null) appUpdateManager.registerListener(UpdateListener);
            } else {
                log("Update flow failed! Result code: " + resultCode);
            }
        }
    }

    /* Displays the snackbar notification and call to action. */
    private void popupSnackbarForCompleteUpdate() {
        Snackbar snackbar = Snackbar.make(binding.getRoot(), R.string.main_activity_update_me, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.main_activity_okay, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (appUpdateManager != null) appUpdateManager.completeUpdate();
            }
        });
        snackbar.setActionTextColor(getResources().getColor(R.color.red_500));
        snackbar.show();
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
        try {
            log("Loading latest version from GitHub");
            DownloadManager.Request request = new DownloadManager.Request(Uri.parse(GITHUB_LAST_APK_URL));
            request.setDescription(getString(R.string.all_new_version));
            request.setTitle(getString(R.string.app_name));
            request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
            request.setMimeType("application/vnd.android.package-archive");
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, getString(R.string.app_name));
            DownloadManager manager = (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
            manager.enqueue(request);
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    private void incCountEnter() {
        timeCounter++;
        if (timeCounter > COUNTER_DIALOG_RATE) showDialogRate();
//        if (timeCounter > COUNTER_DIALOG_DONATE) showPreDialogDonate();
//        if (timeCounter % ACTION_BEFORE_SHOW_ADVERTISEMENT == 0) ShowInterstitialAd();
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
            dialog.getWindow().getAttributes().windowAnimations = R.style.MyMaterialAlertDialogTheme;
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

    public void openAppStore() {
        try {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
        } catch (android.content.ActivityNotFoundException a) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
        }

        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
        firebaseAnalytics.logEvent(FIREBASE_DIALOG_RATE_CLICK, null);
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(binding.toolbar.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (!shouldDisplayLog) return super.dispatchTouchEvent(ev);
        // событие
        int action = ev.getActionMasked();
        // индекс косания
        int index = ev.getActionIndex();
        // id косания
        int id = ev.getPointerId(index);

        switch (action) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_POINTER_DOWN:
                movementListX.set(id, ev.getX(index));
                movementListY.set(id, ev.getY(index));
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_POINTER_UP:
            case MotionEvent.ACTION_CANCEL:
                movementListX.set(id, 0f);
                movementListY.set(id, 0f);
                break;
            case MotionEvent.ACTION_MOVE:
                // число косаний
                int count = ev.getPointerCount();
//                log("fingers = " +count);
                for (int i = 0; i < count; i++) {
                    index = i;
                    id = ev.getPointerId(index);
                    movementListX.set(id, ev.getX(index));
                    movementListY.set(id, ev.getY(index));
                }
                if (count > 9) {
//                    showDialogCongratulations();
                }
                break;
        }
        drawer.setArrayX(movementListX);
        drawer.setArrayY(movementListY);
        return super.dispatchTouchEvent(ev);
    }

    private void setupDrawer() {
        SlidingRootNav drawer = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(binding.toolbar)
                .withDragDistance(110)
                .withRootViewScale(0.65f)
                .withRootViewElevation(8)
                .withRootViewYTranslation(0)
                .withContentClickableWhenMenuOpened(true)
                .addDragStateListener(new DragStateListener() {
                    @Override
                    public void onDragStart() {
                        hideKeyboard();
                    }

                    @Override
                    public void onDragEnd(boolean isMenuOpened) {

                    }
                })
                .withMenuView(this.drawer.getRoot())
                .inject();
        drawer.openMenu();
    }

    @Override
    protected void onPause() {
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
        if (broadcastReceiver != null) unregisterReceiver(broadcastReceiver);
        log("on_Destroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        log("on_Resume");
        incCountEnter();
        regReceiverLog();
//        checkInstallUpdate();
        super.onResume();
    }

    private void checkInstallUpdate() {
        if (appUpdateManager == null) appUpdateManager = AppUpdateManagerFactory.create(this);
        appUpdateManager.getAppUpdateInfo().addOnSuccessListener(new OnSuccessListener<AppUpdateInfo>() {
            @Override
            public void onSuccess(AppUpdateInfo appUpdateInfo) {
                // If the update is downloaded but not installed,
                // notify the user to complete the update.
                switch (appUpdateInfo.installStatus()) {
                    case InstallStatus.DOWNLOADED:
                        popupSnackbarForCompleteUpdate();
                        break;
                    default:
                    case InstallStatus.UNKNOWN:
                        loadNewVersionFlexible();
                        break;
                }
            }
        });
    }


    private void regReceiverLog() {
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
            toggleSecretActivity(binding.getRoot());
            return;
        }
        doubleBackToExitPressedOnce = true;
        Snackbar.make(binding.getRoot(), R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show();

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
