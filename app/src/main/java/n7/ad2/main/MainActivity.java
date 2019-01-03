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
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.SnackbarUtils;
import n7.ad2.activity.BaseActivity;
import n7.ad2.adapter.PlainTextAdapter;
import n7.ad2.databinding.ActivityMainBinding;
import n7.ad2.databinding.DialogRateBinding;
import n7.ad2.databinding.DialogUpdateBinding;
import n7.ad2.databinding.DrawerBinding;
import n7.ad2.fragment.GameFragment;
import n7.ad2.heroes.HeroesFragment;
import n7.ad2.fragment.ItemsFragment;
import n7.ad2.fragment.NewsFragment;
import n7.ad2.fragment.StreamsFragment;
import n7.ad2.fragment.TournamentsFragment;
import n7.ad2.utils.UnscrollableLinearLayoutManager;

import static n7.ad2.main.MainViewModel.LAST_DAY_WHEN_CHECK_UPDATE;
import static n7.ad2.main.MainViewModel.SHOULD_UPDATE_FROM_MARKET;
import static n7.ad2.splash.SplashActivityViewModel.CURRENT_DAY_IN_APP;

public class MainActivity extends BaseActivity {
    public static final int COUNTER_DIALOG_RATE = 20;
    public static final int COUNTER_DIALOG_DONATE = 40;
    public static final String RATE_ME_DIALOG_SHOWN = "REQUEST_FOR_RATE_SAW";
    public static final String OPEN_SUBSCRIPTION = "OPEN_SUBSCRIPTION";
    public static final String LAST_SELECTED_TAG = "LAST_SELECTED_TAG";
    public static final String DONATE_DIALOG_SHOWN = "DONATE_DIALOG_SHOWN";
    public static final String RATED_MY_APP = "RATED_MY_APP";
    public static final int MILLIS_FOR_EXIT = 2000;
    final static String TAG_HEROES = "TAG_HEROES";
    final static String TAG_ITEMS = "TAG_ITEMS";
    final static String TAG_NEWS = "TAG_NEWS";
    final static String TAG_TOURNAMENTS = "TAG_TOURNAMENTS";
    final static String TAG_STREAMS = "TAG_STREAMS";
    final static String TAG_MULTI_TWITCH = "TAG_MULTI_TWITCH";
    final static String TAG_GAMES = "TAG_GAMES";
    private final BroadcastReceiver connections = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
//            setToolbar();
//            if (Utils.isNetworkAvailable(context))
//                log("internet_connection_obtain");
//            else
//                log("internet_connection_lost");
        }
    };
    public ObservableInt selectedItemMenu = new ObservableInt(1);
    private int enterCounter = 0;
    private boolean doubleBackToExitPressedOnce = false;
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private ConstraintLayout constraintLayout;
    private Fragment currentFragment;
    private PlainTextAdapter adapter;
    private ActivityMainBinding bindingActivity;
    private DrawerBinding bindingDrawer;
    private boolean shouldUpdateFromMarket;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("on_Create");
        super.onCreate(savedInstanceState);
        shouldUpdateFromMarket = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(SHOULD_UPDATE_FROM_MARKET, true);

        MainViewModel viewModel = ViewModelProviders.of(this).get(MainViewModel.class);

        bindingActivity = DataBindingUtil.setContentView(this, R.layout.activity_main);
        bindingActivity.setViewModel(viewModel);

        bindingDrawer = DataBindingUtil.inflate(getLayoutInflater(), R.layout.drawer, null, false);
        bindingDrawer.setViewModel(viewModel);
        bindingDrawer.setActivity(this);

        setupToolbar();
        setupDrawer();

//        initN7Message();
        setupRecyclerView();

//        setLastFragment();

        viewModel.snackbarMessage.observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(@StringRes Integer redId) {
                SnackbarUtils.showSnackbar(bindingActivity.getRoot(), getString(redId));
            }
        });
        viewModel.logEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                log(s);
            }
        });
        viewModel.showDialogUpdate.observe(this, new Observer<Void>() {
            @Override
            public void onChanged(@Nullable Void aVoid) {
                showDialogUpdate();
            }
        });
    }

    private void showDialogUpdate() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        DialogUpdateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_update, null, false);
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        dialog.show();

        dialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                int currentDay = PreferenceManager.getDefaultSharedPreferences(MainActivity.this).getInt(CURRENT_DAY_IN_APP, 0);
                PreferenceManager.getDefaultSharedPreferences(MainActivity.this).edit().putInt(LAST_DAY_WHEN_CHECK_UPDATE, currentDay).apply();
            }
        });

    }

    private void setupToolbar() {
        setSupportActionBar(bindingActivity.toolbar);
    }


    @Override
    protected void onStart() {
        super.onStart();
        log("on_Start");
    }

    private void setupRecyclerView() {
        boolean shouldDisplayLog = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.setting_log_key), true);
        if (shouldDisplayLog) {
            adapter = new PlainTextAdapter();
            bindingDrawer.rv.setAdapter(adapter);
            bindingDrawer.rv.setLayoutManager(new UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

//    private void setLastFragment() {
//        switch (MySharedPreferences.LAST_FRAGMENT_SELECTED) {
//            default:
//            case 1:
//                setColorMenu(tv_drawer_heroes);
//                setFragment(tv_drawer_heroes.getId());
//                break;
//            case 2:
//                setColorMenu(tv_drawer_items);
//                setFragment(tv_drawer_items.getId());
//                break;
//            case 3:
//                setColorMenu(tv_drawer_news);
//                setFragment(tv_drawer_news.getId());
//                break;
//            case 4:
//                setColorMenu(tv_drawer_games);
//                setFragment(tv_drawer_games.getId());
//                break;
//            case 5:
//                setColorMenu(tv_drawer_stream);
//                setFragment(tv_drawer_stream.getId());
//                break;
//            case 6:
//                setColorMenu(tv_drawer_game);
//                setFragment(tv_drawer_game.getId());
//                break;
//        }
//    }

    public void log(String text) {
        if (adapter != null && adapter.getItemCount() > 0) {
            adapter.add(text);
            bindingDrawer.rv.scrollToPosition(adapter.getItemCount() - 1);
        }
    }

    public void setFragmentHeroes() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bindingActivity.container.getId(), new HeroesFragment()).commit();
        bindingActivity.toolbar.setTitle(R.string.heroes);
        selectedItemMenu.set(1);
    }

    public void setFragmentItems() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bindingActivity.container.getId(), new ItemsFragment()).commit();
        bindingActivity.toolbar.setTitle(R.string.items);
        selectedItemMenu.set(2);
    }

    public void setFragmentNews() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bindingActivity.container.getId(), new NewsFragment()).commit();
        bindingActivity.toolbar.setTitle(R.string.news);
        selectedItemMenu.set(3);
    }

    public void setFragmentTournaments() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bindingActivity.container.getId(), new TournamentsFragment()).commit();
        bindingActivity.toolbar.setTitle(R.string.tournaments);
        selectedItemMenu.set(4);
    }

    public void setFragmentStreams() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bindingActivity.container.getId(), new StreamsFragment()).commit();
        bindingActivity.toolbar.setTitle(R.string.streams);
        selectedItemMenu.set(5);
    }

    public void setFragmentGames() {
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.add(bindingActivity.container.getId(), new GameFragment()).commit();
        bindingActivity.toolbar.setTitle(R.string.games);
        selectedItemMenu.set(6);
    }


//    private void initConstraintAnimation() {
//        constraintSetOrigin.clone(constraintLayout);
//        constraintSetNew.clone(this, R.layout.fragment_navigation_drawer_0);
//        currentSet = constraintSetOrigin;
//
//        iv_drawer_setting.setOnLongClickListener(new View.OnLongClickListener() {
//            @TargetApi(Build.VERSION_CODES.KITKAT)
//            @Override
//            public boolean onLongClick(View view) {
//                currentSet = (currentSet == constraintSetOrigin ? constraintSetNew : constraintSetOrigin);
//                Transition transition = new ChangeBounds().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
//                TransitionManager.beginDelayedTransition(constraintLayout, transition);
//                currentSet.applyTo(constraintLayout);
//                if (currentSet == constraintSetNew) {
//                    findViewById(R.id.drawer).animate().alpha(0.0f).setDuration(1000).start();
//                    iv_drawer_setting.animate().alpha(0.0f).setDuration(1000).start();
//                } else {
//                    findViewById(R.id.drawer).animate().alpha(1.0f).setDuration(1000).start();
//                    iv_drawer_setting.animate().alpha(1.0f).setDuration(1000).start();
//                }
//                return true;
//            }
//        });
//    }

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
        if (enterCounter > COUNTER_DIALOG_RATE) checkIfNeedShowDialogRate();
//        if (enterCounter > COUNTER_DIALOG_DONATE) checkIfNeedShowDialogDonate();
    }

//    private void checkIfNeedShowDialogDonate() {
//        if (!sp.getBoolean(PREMIUM, false) && !sp.getString(IS_DAY_FOR_DONATE, "0").equals(MySharedPreferences.getTodayDate())) {
//            sp.edit().putString(IS_DAY_FOR_DONATE, MySharedPreferences.getTodayDate()).apply();
//
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setView(R.layout.dialog_donate_me);
//            final AlertDialog dialog = builder.show();
//            dialog.findViewById(R.id.dialog_donate_me_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
//                    intent.putExtra(OPEN_SUBSCRIPTION, true);
//                    startActivity(intent);
//                    dialog.dismiss();
//                }
//            });
//
//            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
//            firebaseAnalytics.logEvent(DONATE_DIALOG_SHOWN, null);
//        }
//    }

    private void checkIfNeedShowDialogRate() {
        boolean showDialogRate = PreferenceManager.getDefaultSharedPreferences(this).getBoolean(getString(R.string.dialog_with_request_for_rate), true);
        if (showDialogRate) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            DialogRateBinding binding = DataBindingUtil.inflate(getLayoutInflater(), R.layout.dialog_rate, null, false);
            builder.setView(binding.getRoot());
            final AlertDialog dialog = builder.create();
            dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
            dialog.show();

//            dialog.findViewById(R.id.b).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    try {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
//                    } catch (android.content.ActivityNotFoundException a) {
//                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
//                    }
//                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
//                    firebaseAnalytics.logEvent(RATED_MY_APP, null);
//                }
//            });
//            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
//            firebaseAnalytics.logEvent(RATE_ME_DIALOG_SHOWN, null);
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.toolbar).getWindowToken(), 0);
        }
    }

//    private void replaceFragment(int id) {
//        hideLoading();
//        fragmentManager = getSupportFragmentManager();
//        currentFragment = fragmentManager.findFragmentById(R.id.container);
//        FragmentTransaction ft = fragmentManager.beginTransaction();
//        if (currentFragment.getTag() != null) {
//            switch (id) {
//                default:
//                case R.id.tv_drawer_heroes:
//                    if (!currentFragment.getTag().equals(TAG_HEROES))
//                        ft.replace(R.id.container, new HeroesFragment(), TAG_HEROES).commit();
//                    log("set_" + TAG_HEROES);
//                    break;
//                case R.id.tv_drawer_items:
//                    if (!currentFragment.getTag().equals(TAG_ITEMS))
//                        ft.replace(R.id.container, new ItemsFragment(), TAG_ITEMS).commit();
//                    log("set_" + TAG_ITEMS);
//                    break;
//                case R.id.tv_drawer_news:
//                    if (!currentFragment.getTag().equals(TAG_NEWS))
//                        ft.replace(R.id.container, new NewsFragment(), TAG_NEWS).commit();
//                    log("set_" + TAG_NEWS);
//                    break;
//                case R.id.tv_drawer_tournaments:
//                    if (!currentFragment.getTag().equals(TAG_TOURNAMENTS))
//                        ft.replace(R.id.container, new TournamentsFragment(), TAG_TOURNAMENTS).commit();
//                    log("set_" + TAG_TOURNAMENTS);
//                    break;
//                case R.id.tv_drawer_game:
//                    if (!currentFragment.getTag().equals(TAG_GAMES))
//                        ft.replace(R.id.container, new GameFragment(), TAG_GAMES).commit();
//                    log("set_" + TAG_GAMES);
//                    break;
//                case R.id.tv_drawer_stream:
//                    if (!currentFragment.getTag().equals(TAG_STREAMS))
//                        ft.replace(R.id.container, new StreamsFragment(), TAG_STREAMS).commit();
//                    log("set_" + TAG_STREAMS);
//                    break;
//            }
//        }
//        setToolbar();
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                slidingRootNav.closeMenu();
//            }
//        }, 50);
//    }

    private void setupDrawer() {
        SlidingRootNav slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(bindingActivity.toolbar)
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
        slidingRootNav.openMenu();
//
//        iv_drawer_check_update.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (!updating) {
//                    updating = true;
//                    startRotate();
//                    startWorkUpdate(true);
//                }
//            }
//        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_activity_main, menu);
        return true;
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
//                if (sp.getBoolean(PREMIUM, false)) {
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
//        String lastSelectedTag = getSupportFragmentManager().findFragmentById(bindingActivity.container.getId()).getTag();
//        PreferenceManager.getDefaultSharedPreferences(this).edit().putString(LAST_SELECTED_TAG, lastSelectedTag).apply();
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

    private void regReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("setToolbarName");
        registerReceiver(connections, filter);
        //можно затригерить ресивер этой командой
//        getActivity().sendBroadcast(new Intent("setToolbarName"));
    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            finish();
            return;
        }
        doubleBackToExitPressedOnce = true;
        Snackbar.make(constraintLayout, R.string.main_press_again_to_exit, Snackbar.LENGTH_SHORT).show();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                doubleBackToExitPressedOnce = false;
            }
        }, MILLIS_FOR_EXIT);
    }
}
