package n7.ad2.main;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.databinding.DataBindingUtil;
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
import android.support.transition.Transition;
import android.support.transition.TransitionManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.SnackbarUtils;
import n7.ad2.activity.BaseActivity;
import n7.ad2.adapter.PlainAdapter;
import n7.ad2.databinding.ActivityMainBinding;
import n7.ad2.databinding.DialogRateBinding;
import n7.ad2.databinding.DialogUpdateBinding;
import n7.ad2.databinding.DrawerBinding;
import n7.ad2.fragment.GameFragment;
import n7.ad2.items.ItemsFragment;
import n7.ad2.news.NewsFragment;
import n7.ad2.streams.StreamsFragment;
import n7.ad2.fragment.TournamentsFragment;
import n7.ad2.heroes.HeroesFragment;
import n7.ad2.utils.UnscrollableLinearLayoutManager;

import static n7.ad2.main.MainViewModel.LAST_DAY_WHEN_CHECK_UPDATE;
import static n7.ad2.main.MainViewModel.SHOULD_UPDATE_FROM_MARKET;
import static n7.ad2.splash.SplashViewModel.CURRENT_DAY_IN_APP;

public class MainActivity extends BaseActivity {

    public static final int COUNTER_DIALOG_RATE = 20;
    public static final int COUNTER_DIALOG_DONATE = 40;

    public static final String FIREBASE_DIALOG_RATE_SAW = "FIREBASE_DIALOG_RATE_SAW";
    public static final String FIREBASE_DIALOG_RATE_CLICK = "FIREBASE_DIALOG_RATE_CLICK";
    public static final String FIREBASE_DIALOG_PRE_DONATE_SAW = "FIREBASE_DIALOG_PRE_DONATE_SAW";

    public static final String LAST_ITEM = "LAST_ITEM";
    public static final int MILLIS_FOR_EXIT = 2000;
    public ObservableInt observableLastItem = new ObservableInt(1);
    private int enterCounter = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintSet constraintSetHidden = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private PlainAdapter adapter;
    private ActivityMainBinding bindingActivity;
    private DrawerBinding bindingDrawer;
    private boolean shouldUpdateFromMarket;
    private MainViewModel viewModel;
    private SlidingRootNav drawable;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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

        setLastFragment();

        log("on_Create");
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
                int currentDay = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getInt(CURRENT_DAY_IN_APP, 0);
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
//                    drawable.closeMenu();
//                }
//            }, 50);
    }

    private void setupSecretActivity() {
        constraintSetOrigin.clone((ConstraintLayout) bindingDrawer.getRoot());
        constraintSetHidden.clone(this, R.layout.drawer_hidden);
        currentSet = constraintSetOrigin;
    }

    private void toggleSecretActivity() {
        currentSet = (currentSet == constraintSetOrigin ? constraintSetHidden : constraintSetOrigin);
        Transition transition = new ChangeBounds().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
        TransitionManager.beginDelayedTransition((ViewGroup) bindingDrawer.getRoot(), transition);
        currentSet.applyTo((ConstraintLayout) bindingDrawer.getRoot());
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
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(MySharedPreferences.GITHUB_LAST_APK_URL));
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
//        if (enterCounter > COUNTER_DIALOG_RATE) checkIfNeedShowDialogRate();
//        if (enterCounter > COUNTER_DIALOG_DONATE) checkIfNeedShowDialogDonate();
    }

    private void showPreDialogDonate() {
//        if (!sp.getBoolean(SUBSCRIPTION, false) && !sp.getString(IS_DAY_FOR_DONATE, "0").equals(MySharedPreferences.getTodayDate())) {
//            sp.edit().putString(IS_DAY_FOR_DONATE, MySharedPreferences.getTodayDate()).apply();
//
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setView(R.layout.dialog_donate_me);
//            final AlertDialog dialog = builder.show();
//            dialog.findViewById(R.id.dialog_donate_me_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                    intent.putExtra(INTENT_SHOW_DIALOG_DONATE, true);
//                    startActivity(intent);
//                    dialog.dismiss();
//                }
//            });
//
//            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
//            firebaseAnalytics.logEvent(FIREBASE_DIALOG_DONATE_SAW, null);
//        }
    }

    @SuppressWarnings("ConstantConditions")
    private void showDialogRate() {
        boolean showDialogRate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.dialog_rate_key), true);
        if (showDialogRate) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DialogRateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_rate, null, false);
            binding.setActivity(this);
            builder.setView(binding.getRoot());
            final AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();

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
            imm.hideSoftInputFromWindow(findViewById(R.id.toolbar).getWindowToken(), 0);
        }
    }

    private void setupDrawer() {
        drawable = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(bindingActivity.toolbarActivityMain)
                .withDragDistance(110)
                .withRootViewScale(0.65f)
                .withRootViewElevation(8)
                .withRootViewYTranslation(0)
                .withContentClickableWhenMenuOpened(false)
                .addDragStateListener(new DragStateListener() {
                    @Override
                    public void onDragStart() {
//                        hideKeyboard();
                    }

                    @Override
                    public void onDragEnd(boolean isMenuOpened) {

                    }
                })
                .withMenuView(bindingDrawer.getRoot())
                .inject();
        drawable.openMenu();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_fragment_streams_open_by_name:
//                AlertDialog.Builder builder = new AlertDialog.Builder(this);
//                builder.setView(R.layout.dialog_open_by_name);
//                final AlertDialog dialog = builder.show();
//                final ImageButton ib_dialog_open_by_name = dialog.findViewById(R.id.ib_dialog_open_by_name);
//                final EditText et_dialog_open_by_name = dialog.findViewById(R.id.et_dialog_open_by_name);
//                final LinearLayout ll_dialog_open_by_name = dialog.findViewById(R.id.ll_dialog_open_by_name);
//
//                final LinkedList<String> list = new LinkedList<>();
//                String streamTyped = sp.getString(TWITCH_STREAMS_TYPED, "[]");
//                String arraysStreams[] = streamTyped.substring(1, streamTyped.length() - 1).split(", ");
//                list.addAll(Arrays.asList(arraysStreams));
//                for (final String i : list) {
//                    TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_list_stream_typed, null);
//                    tv.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (et_dialog_open_by_name != null)
//                                et_dialog_open_by_name.setText(i);
//                        }
//                    });
//                    tv.setText(i);
//                    if (ll_dialog_open_by_name != null)
//                        ll_dialog_open_by_name.addView(tv);
//                }
//
//                if (ib_dialog_open_by_name != null)
//                    ib_dialog_open_by_name.setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View v) {
//                            if (et_dialog_open_by_name != null && et_dialog_open_by_name.getText().length() > 0) {
//                                if (!list.contains(et_dialog_open_by_name.getText().toString().trim()))
//                                    list.addFirst(et_dialog_open_by_name.getText().toString().trim());
//                                while (list.size() > 4) {
//                                    list.pollLast();
//                                }
//                                sp.edit().putString(TWITCH_STREAMS_TYPED, Arrays.toString(list.toArray())).apply();
//                                Intent intent = new Intent(MainActivity.this, TwitchGameActivity.class);
//                                intent.putExtra(CHANNEL_NAME, et_dialog_open_by_name.getText().toString().trim());
//                                intent.putExtra(CHANNEL_TITLE, "");
//                                startActivity(intent);
//                                dialog.dismiss();
//                            }
//                        }
//                    });
                break;
            case R.id.menu_fragment_streams_open_multitab:
//                if (sp.getBoolean(SUBSCRIPTION, false)) {
//                    fragmentManager = getSupportFragmentManager();
//                    currentFragment = fragmentManager.findFragmentById(R.id.container);
//                    FragmentTransaction ft = fragmentManager.beginTransaction();
//                    if (currentFragment.getTag() != null && currentFragment.getTag().equals(TAG_MULTI_TWITCH)) {
//                        ft.replace(R.id.container, new StreamsFragment(), TAG_STREAMS).commit();
//                    } else {
//                        ft.replace(R.id.container, new MultiStreamsFragment(), TAG_MULTI_TWITCH).commit();
//                    }
//                } else {
//                    showSnackBarPremium();
//                }

                break;
        }
        return super.onOptionsItemSelected(item);
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
        log("on_Destroy");
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        log("on_Resume");
        incCountEnter();
    }

//    private void regReceiver() {
//        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
//        filter.addAction("setToolbarName");
//        registerReceiver(connections, filter);
//        //можно затригерить ресивер этой командой
////        getActivity().sendBroadcast(new Intent("setToolbarName"));
//    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
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
