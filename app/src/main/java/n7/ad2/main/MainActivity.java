package n7.ad2.main;

import android.app.DownloadManager;
import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.BroadcastReceiver;
import android.content.Context;
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
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.BuildConfig;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.activity.BaseActivity;
import n7.ad2.adapter.PlainTextAdapter;
import n7.ad2.databinding.ActivityMainBinding;
import n7.ad2.databinding.DrawerBinding;
import n7.ad2.fragment.GameFragment;
import n7.ad2.fragment.HeroesFragment;
import n7.ad2.fragment.ItemsFragment;
import n7.ad2.fragment.NewsFragment;
import n7.ad2.fragment.StreamsFragment;
import n7.ad2.fragment.TournamentsFragment;
import n7.ad2.setting.SettingActivity;
import n7.ad2.utils.UnscrollableLinearLayoutManager;
import n7.ad2.utils.Utils;
import n7.ad2.worker.UpdateAppWorker;

import static n7.ad2.worker.UpdateAppWorker.VERSION_CODE_APP;
import static n7.ad2.worker.UpdateAppWorker.VERSION_CODE_SERVER;

public class MainActivity extends BaseActivity {
    public static final int COUNTER_DIALOG_RATE = 20;
    public static final int COUNTER_DIALOG_DONATE = 40;
    public static final String RATE_ME_DIALOG_SHOWN = "REQUEST_FOR_RATE_SAW";
    public static final String OPEN_SUBSCRIPTION = "OPEN_SUBSCRIPTION";
    public static final String DONATE_DIALOG_SHOWN = "DONATE_DIALOG_SHOWN";
    public static final String RATED_MY_APP = "RATED_MY_APP";
    public static final int MILLIS_FOR_EXIT = 2000;
    final static String TV_DRAWER_HEROES_TAG = "FRAGMENT_HEROES";
    final static String TV_DRAWER_ITEMS_TAG = "FRAGMENT_ITEMS";
    final static String TV_DRAWER_NEWS_TAG = "FRAGMENT_NEWS";
    final static String TV_DRAWER_TOURNAMENTS_TAG = "FRAGMENT_TOURNAMENTS";
    final static String TV_DRAWER_STREAM_TAG = "FRAGMENT_STREAM";
    final static String TV_DRAWER_MULTI_STREAM_TAG = "TV_DRAWER_MULTI_STREAM_TAG";
    final static String TV_DRAWER_GAME_TAG = "FRAGMENT_GAME";
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
    TextView tv_user_nickname, tv_user_rang, tv_current_menu;
    View v_current_menu;
    ImageView iv_user_ava, iv_user_medal, iv_user_stars, iv_drawer_setting, iv_drawer_check_update;
    Button b_fragment_drawer_login;
    RecyclerView recyclerView;
    private int enterCounter = 0;
    private int oldColorDrawerMenu, angleOfUpdate = 0;
    private boolean doubleBackToExitPressedOnce = false, isUpdate = false;
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private ConstraintLayout constraintLayout;
    private Fragment currentFragment;
    private PlainTextAdapter adapter;
    private ActivityMainBinding bindingActivity;
    private DrawerBinding bindingDrawer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        log("on_Create");
        super.onCreate(savedInstanceState);

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
        checkUpdate();

        viewModel.logEvent.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                log(s);
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

//    private void initN7Message() {
//        final TextView tv_n7message = findViewById(R.id.n7message);
//        LiveData<String> data = N7MessageRoomDatabase.getDatabase(this).n7MessageDao().getMessage();
//        data.observe(this, new Observer<String>() {
//            @Override
//            public void onChanged(@Nullable String s) {
//                tv_n7message.setText(s);
//            }
//        });
//    }

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
//        if (enterCounter > COUNTER_DIALOG_RATE) checkIfNeedShowDialogRate();
//        if (enterCounter > COUNTER_DIALOG_DONATE) checkIfNeedShowDialogDonate();
    }

//    private void checkIfNeedShowDialogDonate() {
//        if (!sp.getBoolean(PREMIUM, false) &&
//                !sp.getString(IS_DAY_FOR_DONATE, "0").equals(MySharedPreferences.getTodayDate())) {
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

//    private void checkIfNeedShowDialogRate() {
//        if (!sp.getString(IS_DAY_FOR_RATE, "0").equals(NEVER) &&
//                !sp.getString(IS_DAY_FOR_RATE, "0").equals(MySharedPreferences.getTodayDate())) {
//            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
//            builder.setView(R.layout.dialog_rate_me);
//            final AlertDialog dialog = builder.show();
//            dialog.findViewById(R.id.dialog_rate_me_later).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    sp.edit().putString(IS_DAY_FOR_RATE, MySharedPreferences.getTodayDate()).apply();
//                }
//            });
//            dialog.findViewById(R.id.dialog_rate_me_never).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    sp.edit().putString(IS_DAY_FOR_RATE, NEVER).apply();
//                }
//            });
//            dialog.findViewById(R.id.dialog_rate_me_ok).setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View v) {
//                    dialog.dismiss();
//                    sp.edit().putString(IS_DAY_FOR_RATE, NEVER).apply();
//                    Calendar calendar = Calendar.getInstance();
//                    Long inMemoryTime = sp.getLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis());
//                    Long currentTime = calendar.getTimeInMillis();
//                    Long latestTime = (inMemoryTime > currentTime ? inMemoryTime : currentTime);
//                    calendar.setTimeInMillis(latestTime);
//                    calendar.add(Calendar.DATE, 5);
//                    sp.edit().putLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis()).apply();
//                    log("5_day_premium_granted");
//                    checkIsPremium();
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
//        }
//    }

    private void checkUpdate() {
//        if (!sp.getString(IS_DAY_FOR_UPDATE_KEY, "0").equals(MySharedPreferences.getTodayDate())) {
//        }
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
//                    if (!currentFragment.getTag().equals(TV_DRAWER_HEROES_TAG))
//                        ft.replace(R.id.container, new HeroesFragment(), TV_DRAWER_HEROES_TAG).commit();
//                    log("set_" + TV_DRAWER_HEROES_TAG);
//                    break;
//                case R.id.tv_drawer_items:
//                    if (!currentFragment.getTag().equals(TV_DRAWER_ITEMS_TAG))
//                        ft.replace(R.id.container, new ItemsFragment(), TV_DRAWER_ITEMS_TAG).commit();
//                    log("set_" + TV_DRAWER_ITEMS_TAG);
//                    break;
//                case R.id.tv_drawer_news:
//                    if (!currentFragment.getTag().equals(TV_DRAWER_NEWS_TAG))
//                        ft.replace(R.id.container, new NewsFragment(), TV_DRAWER_NEWS_TAG).commit();
//                    log("set_" + TV_DRAWER_NEWS_TAG);
//                    break;
//                case R.id.tv_drawer_tournaments:
//                    if (!currentFragment.getTag().equals(TV_DRAWER_TOURNAMENTS_TAG))
//                        ft.replace(R.id.container, new TournamentsFragment(), TV_DRAWER_TOURNAMENTS_TAG).commit();
//                    log("set_" + TV_DRAWER_TOURNAMENTS_TAG);
//                    break;
//                case R.id.tv_drawer_game:
//                    if (!currentFragment.getTag().equals(TV_DRAWER_GAME_TAG))
//                        ft.replace(R.id.container, new GameFragment(), TV_DRAWER_GAME_TAG).commit();
//                    log("set_" + TV_DRAWER_GAME_TAG);
//                    break;
//                case R.id.tv_drawer_stream:
//                    if (!currentFragment.getTag().equals(TV_DRAWER_STREAM_TAG))
//                        ft.replace(R.id.container, new StreamsFragment(), TV_DRAWER_STREAM_TAG).commit();
//                    log("set_" + TV_DRAWER_STREAM_TAG);
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
//                if (!isUpdate) {
//                    isUpdate = true;
//                    startRotate();
//                    startWorkUpdate(true);
//                }
//            }
//        });
    }

    private void startRotate() {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            @Override
            public void run() {
                if (isUpdate) {
                    angleOfUpdate += 2;
                    iv_drawer_check_update.setRotation(angleOfUpdate * 2);
                    handler.postDelayed(this, 10);
                }
            }
        });
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
//                    if (currentFragment.getTag() != null && currentFragment.getTag().equals(TV_DRAWER_MULTI_STREAM_TAG)) {
//                        ft.replace(R.id.container, new StreamsFragment(), TV_DRAWER_STREAM_TAG).commit();
//                    } else {
//                        ft.replace(R.id.container, new MultiStreamsFragment(), TV_DRAWER_MULTI_STREAM_TAG).commit();
//                    }
//                } else {
//                    showSnackBarPremium();
//                }

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showSnackBarPremium() {
        Snackbar.make(recyclerView, R.string.all_only_for_subscribers, Snackbar.LENGTH_LONG).setAction(R.string.all_buy, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        }).show();
    }

    @Override
    protected void onPause() {
        super.onPause();
//        unregisterReceiver(connections);
//        sensorTranslationUpdater.unregisterSensorManager();
//        sp.edit().putInt(MySharedPreferences.LAST_FRAGMENT_SELECTED_KEY, MySharedPreferences.LAST_FRAGMENT_SELECTED).apply();
        log("on_Pause");
    }

    @Override
    protected void onStop() {
        log("on_Stop");
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
//        regReceiver();
//        sensorTranslationUpdater.registerSensorManager();
//        UpdateUserRank();
//        incCountEnter();
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("setToolbarName");
        registerReceiver(connections, filter);
        //можно затригерить ресивер этой командой
//        getActivity().sendBroadcast(new Intent("setToolbarName"));
    }

    private void setToolbarName(int nameToolbar) {
        if (Utils.isNetworkAvailable(getBaseContext())) {
            SpannableStringBuilder str = new SpannableStringBuilder(getString(R.string.app_name) + "/" + getString(nameToolbar));
            str.setSpan(new ForegroundColorSpan(getColorAccentTheme()), 0, 4, SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE);
            setTitle(str);
        } else {
            setTitle(getString(R.string.app_name) + "/" + getString(nameToolbar));
        }
    }

    private int getColorAccentTheme() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
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
