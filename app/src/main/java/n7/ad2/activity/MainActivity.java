package n7.ad2.activity;

import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.SpannableStringBuilder;
import android.text.style.ForegroundColorSpan;
import android.transition.ChangeBounds;
import android.transition.Transition;
import android.transition.TransitionManager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.TextView;

import com.google.firebase.analytics.FirebaseAnalytics;
import com.schibsted.spain.parallaxlayerlayout.ParallaxLayerLayout;
import com.schibsted.spain.parallaxlayerlayout.SensorTranslationUpdater;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.yarolegovich.slidingrootnav.SlidingRootNav;
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder;
import com.yarolegovich.slidingrootnav.callback.DragStateListener;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.LinkedList;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.AppExecutors;
import n7.ad2.BuildConfig;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.SETTINGS.SettingActivity;
import n7.ad2.adapter.PlainTextAdapter;
import n7.ad2.db.n7message.N7MessageRoomDatabase;
import n7.ad2.fragment.GameFragment;
import n7.ad2.fragment.HeroesFragment;
import n7.ad2.fragment.ItemsFragment;
import n7.ad2.fragment.MultiStreamsFragment;
import n7.ad2.fragment.NewsFragment;
import n7.ad2.fragment.StreamsFragment;
import n7.ad2.fragment.TournamentsFragment;
import n7.ad2.utils.Constants;
import n7.ad2.utils.UnscrollableLinearLayoutManager;
import n7.ad2.utils.Utils;
import n7.ad2.worker.UpdateAppWorker;

import static n7.ad2.MySharedPreferences.DAY_FOR_UPDATE;
import static n7.ad2.MySharedPreferences.IS_DAY_FOR_DONATE;
import static n7.ad2.MySharedPreferences.IS_DAY_FOR_RATE;
import static n7.ad2.MySharedPreferences.IS_DAY_FOR_UPDATE_KEY;
import static n7.ad2.MySharedPreferences.NEVER;
import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.MySharedPreferences.SHOW_PREMIUM_SWITCH;
import static n7.ad2.MySharedPreferences.TWITCH_STREAMS_TYPED;
import static n7.ad2.MySharedPreferences.USER_NAME;
import static n7.ad2.MySharedPreferences.USER_PLACE;
import static n7.ad2.activity.LogInActivity.USER_ID;
import static n7.ad2.activity.TwitchGameActivity.CHANNEL_NAME;
import static n7.ad2.activity.TwitchGameActivity.CHANNEL_TITLE;
import static n7.ad2.worker.UpdateAppWorker.VERSION_CODE_APP;
import static n7.ad2.worker.UpdateAppWorker.VERSION_CODE_SERVER;

public class MainActivity extends BaseActivity implements View.OnClickListener {
    public static final int COUNTER_DIALOG_RATE = 20;
    public static final int COUNTER_DIALOG_DONATE = 40;
    public static final String RATE_ME_DIALOG_SHOWN = "REQUEST_FOR_RATE_SAW";
    public static final String OPEN_SUBSCRIPTION = "OPEN_SUBSCRIPTION";
    public static final String DONATE_DIALOG_SHOWN = "DONATE_DIALOG_SHOWN";
    public static final String RATED_MY_APP = "RATED_MY_APP";
    final static String TV_DRAWER_HEROES_TAG = "FRAGMENT_HEROES";
    final static String TV_DRAWER_ITEMS_TAG = "FRAGMENT_ITEMS";
    final static String TV_DRAWER_NEWS_TAG = "FRAGMENT_NEWS";
    final static String TV_DRAWER_TOURNAMENTS_TAG = "FRAGMENT_TOURNAMENTS";
    final static String TV_DRAWER_STREAM_TAG = "FRAGMENT_STREAM";
    final static String TV_DRAWER_MULTI_STREAM_TAG = "TV_DRAWER_MULTI_STREAM_TAG";
    final static String TV_DRAWER_GAME_TAG = "FRAGMENT_GAME";
    public SharedPreferences sp;
    TextView tv_user_nickname, tv_user_rang, tv_current_menu;
    View v_current_menu;
    ImageView iv_user_ava, iv_user_medal, iv_user_stars, iv_drawer_setting, iv_drawer_check_update;
    Button b_fragment_drawer_login;
    SensorTranslationUpdater sensorTranslationUpdater;
    RecyclerView recyclerView;
    private int enterCounter = 0;
    private int oldColorDrawerMenu, angleOfUpdate = 0;
    private SlidingRootNav slidingRootNav;
    private boolean doubleBackToExitPressedOnce = false, isUpdate = false;
    private AppExecutors appExecutors;
    private ConstraintSet constraintSetNew = new ConstraintSet();
    private ConstraintSet constraintSetOrigin = new ConstraintSet();
    private ConstraintSet currentSet;
    private ConstraintLayout constraintLayout;
    private TextView tv_drawer_heroes;
    private TextView tv_drawer_items;
    private TextView tv_drawer_news;
    private TextView tv_drawer_games;
    private TextView tv_drawer_game;
    private TextView tv_drawer_stream;
    private View v_drawer_heroes;
    private View v_drawer_news;
    private View v_drawer_items;
    private View v_drawer_games;
    private View v_drawer_game;
    private View v_drawer_stream;
    private FragmentManager fragmentManager;
    private Fragment currentFragment;
    private PlainTextAdapter adapter;
    private boolean showLog = true;
    private final BroadcastReceiver connections = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            setToolbar();
            if (Utils.isNetworkAvailable(context))
                log("internet_connection_obtain");
            else
                log("internet_connection_lost");
        }
    };
    private Switch switch_premium;
    private ProgressBar progressBar;
    private ImageView iv_legion;
    private TextView tv_legion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        sp = MySharedPreferences.getSharedPreferences(this);
        appExecutors = new AppExecutors();

        initNewDrawer();
        initN7Message();
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT)
            initConstraintAnimation();
        initParallaxLayerLayout();
        initSystemInformationRecycler();

        setLastFragment();
        checkUpdate();

        log("on_Create");
    }

    private void hideLoading() {
        progressBar.setVisibility(View.GONE);
        iv_legion.setVisibility(View.GONE);
        tv_legion.setVisibility(View.GONE);
    }

    private void checkIsPremium() {
        appExecutors.diskIO().execute(new Runnable() {
            @Override
            public void run() {
                Calendar calendar = Calendar.getInstance();
                Long inMemoryTime = MySharedPreferences.getSharedPreferences(MainActivity.this).getLong(MySharedPreferences.DATE_END_PREMIUM, 0);
                Long currentTime = calendar.getTimeInMillis();
                if (inMemoryTime > currentTime) {
                    MySharedPreferences.getSharedPreferences(MainActivity.this).edit().putBoolean(MySharedPreferences.PREMIUM, true).commit();
                } else {
                    MySharedPreferences.getSharedPreferences(MainActivity.this).edit().putBoolean(MySharedPreferences.PREMIUM, false).commit();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        log("on_Start");
    }

    private void initSystemInformationRecycler() {
        showLog = sp.getBoolean(getString(R.string.setting_show_log_key), true);
        if (showLog) {
            recyclerView = findViewById(R.id.rv_drawer);
            adapter = new PlainTextAdapter(this);
            recyclerView.setAdapter(adapter);
            recyclerView.setLayoutManager(new UnscrollableLinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        }
    }

    public void log(final String item) {
        if (adapter != null && showLog) {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    adapter.add(item);
                    adapter.notifyDataSetChanged();
                    recyclerView.scrollToPosition(adapter.getItemCount() - 1);
                }
            });

        }
    }

    private void initN7Message() {
        switch_premium = findViewById(R.id.switch_premium);
        switch_premium.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    sp.edit().putBoolean(MySharedPreferences.PREMIUM, true).apply();
                    Calendar calendar = Calendar.getInstance();
                    calendar.add(Calendar.YEAR, 1);
                    MySharedPreferences.getSharedPreferences(MainActivity.this).edit().putLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis()).apply();
                } else {
                    sp.edit().putBoolean(MySharedPreferences.PREMIUM, false).apply();
                    MySharedPreferences.getSharedPreferences(MainActivity.this).edit().putLong(MySharedPreferences.DATE_END_PREMIUM, 0).apply();
                }
            }
        });
        final TextView tv_n7message = findViewById(R.id.n7message);
        LiveData<String> data = N7MessageRoomDatabase.getDatabase(this).n7MessageDao().getMessage();
        data.observe(this, new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                tv_n7message.setText(s);
            }
        });
    }

    private void setLastFragment() {
        switch (MySharedPreferences.LAST_FRAGMENT_SELECTED) {
            default:
            case 1:
                setColorMenu(tv_drawer_heroes);
                setFragment(tv_drawer_heroes.getId());
                break;
            case 2:
                setColorMenu(tv_drawer_items);
                setFragment(tv_drawer_items.getId());
                break;
            case 3:
                setColorMenu(tv_drawer_news);
                setFragment(tv_drawer_news.getId());
                break;
            case 4:
                setColorMenu(tv_drawer_games);
                setFragment(tv_drawer_games.getId());
                break;
            case 5:
                setColorMenu(tv_drawer_stream);
                setFragment(tv_drawer_stream.getId());
                break;
            case 6:
                setColorMenu(tv_drawer_game);
                setFragment(tv_drawer_game.getId());
                break;
        }
    }

    private void setFragment(int id) {
        hideLoading();
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.details);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        switch (id) {
            default:
            case R.id.tv_drawer_heroes:
                ft.add(R.id.details, new HeroesFragment(), TV_DRAWER_HEROES_TAG).commit();
                break;
            case R.id.tv_drawer_items:
                ft.add(R.id.details, new ItemsFragment(), TV_DRAWER_ITEMS_TAG).commit();
                break;
            case R.id.tv_drawer_news:
                ft.add(R.id.details, new NewsFragment(), TV_DRAWER_NEWS_TAG).commit();
                break;
            case R.id.tv_drawer_tournaments:
                ft.add(R.id.details, new TournamentsFragment(), TV_DRAWER_TOURNAMENTS_TAG).commit();
                break;
            case R.id.tv_drawer_game:
                ft.add(R.id.details, new GameFragment(), TV_DRAWER_GAME_TAG).commit();
                break;
            case R.id.tv_drawer_stream:
                ft.add(R.id.details, new StreamsFragment(), TV_DRAWER_STREAM_TAG).commit();
                break;
        }
        setToolbar();
    }

    private void initConstraintAnimation() {
        constraintSetOrigin.clone(constraintLayout);
        constraintSetNew.clone(this, R.layout.fragment_navigation_drawer_0);
        currentSet = constraintSetOrigin;

        iv_drawer_setting.setOnLongClickListener(new View.OnLongClickListener() {
            @TargetApi(Build.VERSION_CODES.KITKAT)
            @Override
            public boolean onLongClick(View view) {
                currentSet = (currentSet == constraintSetOrigin ? constraintSetNew : constraintSetOrigin);
                Transition transition = new ChangeBounds().setInterpolator(new AccelerateDecelerateInterpolator()).setDuration(1000);
                TransitionManager.beginDelayedTransition(constraintLayout, transition);
                currentSet.applyTo(constraintLayout);
                if (SHOW_PREMIUM_SWITCH) switch_premium.setVisibility(View.VISIBLE);
                switch_premium.setChecked(sp.getBoolean(MySharedPreferences.PREMIUM, false));
                if (currentSet == constraintSetNew) {
                    findViewById(R.id.dl_activity_main).animate().alpha(0.0f).setDuration(1000).start();
                    iv_drawer_setting.animate().alpha(0.0f).setDuration(1000).start();
                } else {
                    findViewById(R.id.dl_activity_main).animate().alpha(1.0f).setDuration(1000).start();
                    iv_drawer_setting.animate().alpha(1.0f).setDuration(1000).start();
                }
                return true;
            }
        });
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

    private void initParallaxLayerLayout() {
        ParallaxLayerLayout parallaxLayerLayout = findViewById(R.id.parallax);
        sensorTranslationUpdater = new SensorTranslationUpdater(this);
        parallaxLayerLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                UpdateUserRank();
//                startActivity(new Intent(MainActivity.this, DotabuffActivity.class));
            }
        });
        parallaxLayerLayout.setTranslationUpdater(sensorTranslationUpdater);
    }

    private void updateAllFreeCountersForToday() {
        sp.edit().putInt(MySharedPreferences.RESPONSE_COUNT_KEY, MySharedPreferences.FREE_COUNT).commit();
        sp.edit().putString(IS_DAY_FOR_UPDATE_KEY, MySharedPreferences.getTodayDate()).commit();
    }

    private void incCountEnter() {
        enterCounter++;
        if (enterCounter > COUNTER_DIALOG_RATE) checkIfNeedShowDialogRate();
        if (enterCounter > COUNTER_DIALOG_DONATE) checkIfNeedShowDialogDonate();
    }

    private void checkIfNeedShowDialogDonate() {
        if (!sp.getBoolean(PREMIUM, false)&&
                !sp.getString(IS_DAY_FOR_DONATE,"0").equals(MySharedPreferences.getTodayDate())) {
            sp.edit().putString(IS_DAY_FOR_DONATE, MySharedPreferences.getTodayDate()).apply();

            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_donate_me);
            final AlertDialog dialog = builder.show();
            dialog.findViewById(R.id.dialog_donate_me_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                    intent.putExtra(OPEN_SUBSCRIPTION, true);
                    startActivity(intent);
                    dialog.dismiss();
                }
            });

            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.logEvent(DONATE_DIALOG_SHOWN, null);
        }
    }

    private void checkIfNeedShowDialogRate() {
        if (!sp.getString(IS_DAY_FOR_RATE, "0").equals(NEVER) &&
                !sp.getString(IS_DAY_FOR_RATE, "0").equals(MySharedPreferences.getTodayDate())) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setView(R.layout.dialog_rate_me);
            final AlertDialog dialog = builder.show();
            dialog.findViewById(R.id.dialog_rate_me_later).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    sp.edit().putString(IS_DAY_FOR_RATE, MySharedPreferences.getTodayDate()).apply();
                }
            });
            dialog.findViewById(R.id.dialog_rate_me_never).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    sp.edit().putString(IS_DAY_FOR_RATE, NEVER).apply();
                }
            });
            dialog.findViewById(R.id.dialog_rate_me_ok).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    sp.edit().putString(IS_DAY_FOR_RATE, NEVER).apply();
                    Calendar calendar = Calendar.getInstance();
                    Long inMemoryTime = sp.getLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis());
                    Long currentTime = calendar.getTimeInMillis();
                    Long latestTime = (inMemoryTime > currentTime ? inMemoryTime : currentTime);
                    calendar.setTimeInMillis(latestTime);
                    calendar.add(Calendar.DATE, 5);
                    sp.edit().putLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis()).apply();
                    log("5_day_premium_granted");
                    checkIsPremium();
                    try {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName())));
                    } catch (android.content.ActivityNotFoundException a) {
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName())));
                    }
                    FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(MainActivity.this);
                    firebaseAnalytics.logEvent(RATED_MY_APP, null);
                }
            });
            FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(this);
            firebaseAnalytics.logEvent(RATE_ME_DIALOG_SHOWN, null);
        }
    }

    private void checkUpdate() {
        startWorkUpdate(false);
        if (!sp.getString(IS_DAY_FOR_UPDATE_KEY, "0").equals(MySharedPreferences.getTodayDate())) {
            appExecutors.diskIO().execute(new Runnable() {
                @Override
                public void run() {
                    updateAllFreeCountersForToday();
                }
            });
        }
    }

    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(findViewById(R.id.toolbar).getWindowToken(), 0);
        }
    }

    @Override
    public void onClick(View v) {
        replaceFragment(v.getId());
        setColorMenu((TextView) v);
    }

    private void setColorMenu(TextView v) {
        if (tv_current_menu != null) {
            //возвращаем изначальный цвет
            tv_current_menu.setTextColor(oldColorDrawerMenu);
        }
        //сохраняем цвет темы и выбранное меню
        tv_current_menu = v;
        oldColorDrawerMenu = tv_current_menu.getCurrentTextColor();
        //применяем цвет темы
        tv_current_menu.setTextColor(getColorAccentTheme());
        setViewColor();
    }

    //        }
    //            }
    //                drawable.setColorFilter(new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_IN));
    //            if (drawable != null) {
    //        for (Drawable drawable : textView.getCompoundDrawables()) {
//    private void setTextViewDrawableColor(TextView textView, int color) {

//    }

    private void setViewColor() {
        if (v_current_menu != null) {
            v_current_menu.setBackgroundColor(getResources().getColor(R.color.textColorSecondary));
        }
        switch (MySharedPreferences.LAST_FRAGMENT_SELECTED) {
            default:
            case 1:
                v_drawer_heroes.setBackgroundColor(getColorAccentTheme());
                v_current_menu = v_drawer_heroes;
                break;
            case 2:
                v_drawer_items.setBackgroundColor(getColorAccentTheme());
                v_current_menu = v_drawer_items;
                break;
            case 3:
                v_drawer_news.setBackgroundColor(getColorAccentTheme());
                v_current_menu = v_drawer_news;
                break;
            case 4:
                v_drawer_games.setBackgroundColor(getColorAccentTheme());
                v_current_menu = v_drawer_games;
                break;
            case 5:
                v_drawer_stream.setBackgroundColor(getColorAccentTheme());
                v_current_menu = v_drawer_stream;
                break;
            case 6:
                v_drawer_game.setBackgroundColor(getColorAccentTheme());
                v_current_menu = v_drawer_game;
                break;

        }
    }

    private int getColorAccentTheme() {
        TypedValue typedValue = new TypedValue();
        getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    private void replaceFragment(int id) {
        hideLoading();
        fragmentManager = getSupportFragmentManager();
        currentFragment = fragmentManager.findFragmentById(R.id.details);
        FragmentTransaction ft = fragmentManager.beginTransaction();
        if (currentFragment.getTag() != null) {
            switch (id) {
                default:
                case R.id.tv_drawer_heroes:
                    if (!currentFragment.getTag().equals(TV_DRAWER_HEROES_TAG))
                        ft.replace(R.id.details, new HeroesFragment(), TV_DRAWER_HEROES_TAG).commit();
                    log("set_" + TV_DRAWER_HEROES_TAG);
                    break;
                case R.id.tv_drawer_items:
                    if (!currentFragment.getTag().equals(TV_DRAWER_ITEMS_TAG))
                        ft.replace(R.id.details, new ItemsFragment(), TV_DRAWER_ITEMS_TAG).commit();
                    log("set_" + TV_DRAWER_ITEMS_TAG);
                    break;
                case R.id.tv_drawer_news:
                    if (!currentFragment.getTag().equals(TV_DRAWER_NEWS_TAG))
                        ft.replace(R.id.details, new NewsFragment(), TV_DRAWER_NEWS_TAG).commit();
                    log("set_" + TV_DRAWER_NEWS_TAG);
                    break;
                case R.id.tv_drawer_tournaments:
                    if (!currentFragment.getTag().equals(TV_DRAWER_TOURNAMENTS_TAG))
                        ft.replace(R.id.details, new TournamentsFragment(), TV_DRAWER_TOURNAMENTS_TAG).commit();
                    log("set_" + TV_DRAWER_TOURNAMENTS_TAG);
                    break;
                case R.id.tv_drawer_game:
                    if (!currentFragment.getTag().equals(TV_DRAWER_GAME_TAG))
                        ft.replace(R.id.details, new GameFragment(), TV_DRAWER_GAME_TAG).commit();
                    log("set_" + TV_DRAWER_GAME_TAG);
                    break;
                case R.id.tv_drawer_stream:
                    if (!currentFragment.getTag().equals(TV_DRAWER_STREAM_TAG))
                        ft.replace(R.id.details, new StreamsFragment(), TV_DRAWER_STREAM_TAG).commit();
                    log("set_" + TV_DRAWER_STREAM_TAG);
                    break;
            }
        }
        setToolbar();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                slidingRootNav.closeMenu();
            }
        }, 50);
    }

    private void initNewDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        slidingRootNav = new SlidingRootNavBuilder(this)
                .withToolbarMenuToggle(toolbar)
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
                .withMenuLayout(R.layout.fragment_navigation_drawer)
                .inject();

        slidingRootNav.openMenu();
        progressBar = findViewById(R.id.pb);
        iv_legion = findViewById(R.id.iv_legion);
        tv_legion = findViewById(R.id.tv_legion);
        constraintLayout = findViewById(R.id.root);
        iv_user_ava = findViewById(R.id.iv_user_ava);
        iv_user_medal = findViewById(R.id.iv_user_medal);
        iv_user_stars = findViewById(R.id.iv_user_stars);
        tv_user_nickname = findViewById(R.id.tv_user_nickname);
        tv_user_rang = findViewById(R.id.tv_user_rang);
        iv_drawer_setting = findViewById(R.id.iv_drawer_setting);
        iv_drawer_check_update = findViewById(R.id.iv_drawer_check_update);
        b_fragment_drawer_login = findViewById(R.id.b_fragment_drawer_login);
        tv_drawer_heroes = findViewById(R.id.tv_drawer_heroes);
        tv_drawer_items = findViewById(R.id.tv_drawer_items);
        tv_drawer_news = findViewById(R.id.tv_drawer_news);
        tv_drawer_games = findViewById(R.id.tv_drawer_tournaments);
        tv_drawer_game = findViewById(R.id.tv_drawer_game);
        tv_drawer_stream = findViewById(R.id.tv_drawer_stream);
        v_drawer_heroes = findViewById(R.id.v_drawer_heroes);
        v_drawer_news = findViewById(R.id.v_drawer_news);
        v_drawer_items = findViewById(R.id.v_drawer_items);
        v_drawer_games = findViewById(R.id.v_drawer_games);
        v_drawer_game = findViewById(R.id.v_drawer_game);
        v_drawer_stream = findViewById(R.id.v_drawer_stream);
        tv_drawer_heroes.setOnClickListener(this);
        tv_drawer_items.setOnClickListener(this);
        tv_drawer_news.setOnClickListener(this);
        tv_drawer_games.setOnClickListener(this);
        tv_drawer_game.setOnClickListener(this);
        tv_drawer_stream.setOnClickListener(this);

        b_fragment_drawer_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, LogInActivity.class), LogInActivity.REQUEST_CODE_LOG_IN);
            }
        });

        iv_drawer_setting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SettingActivity.class));
            }
        });

        iv_drawer_check_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isUpdate) {
                    isUpdate = true;
                    startRotate();
                    startWorkUpdate(true);
                }
            }
        });
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

    private void startWorkUpdate(final boolean isUserInitiative) {
        Data data = new Data.Builder()
                .putDouble(VERSION_CODE_APP, Double.valueOf(BuildConfig.VERSION_NAME))
                .build();
        OneTimeWorkRequest updateAppWorker = new OneTimeWorkRequest.Builder(UpdateAppWorker.class)
                .setInputData(data)
                .build();

        log("work_update_enqueued");
        WorkManager.getInstance().enqueue(updateAppWorker);

        WorkManager.getInstance().getStatusById(updateAppWorker.getId()).observe(MainActivity.this, new Observer<WorkStatus>() {
            @Override
            public void onChanged(@Nullable WorkStatus workStatus) {
                if (workStatus != null && workStatus.getState().equals(State.SUCCEEDED)) {
                    log("work_update_succeeded");
                    isUpdate = false;
                    final boolean fromMarket = workStatus.getOutputData().getBoolean(UpdateAppWorker.FROM_MARKET, true);
                    String message = workStatus.getOutputData().getString(UpdateAppWorker.MESSAGE);
                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setView(R.layout.dialog_update);
                    final AlertDialog dialog = builder.show();
                    dialog.findViewById(R.id.b_dialog_update_no).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            dialog.cancel();
                        }
                    });
                    dialog.findViewById(R.id.b_dialog_update_yes).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (fromMarket)
                                loadNewVersionFromMarket();
                            else
                                loadNewVersionFromGitHub();
                            dialog.cancel();
                        }
                    });
                } else if (isUserInitiative && workStatus != null && workStatus.getState().equals(State.FAILED)) {
                    log("work_update_failed");
                    isUpdate = false;
                    if (Utils.isNetworkAvailable(MainActivity.this)) {
                        log("app_version=" + workStatus.getOutputData().getString(VERSION_CODE_APP));
                        log("server_version=" + workStatus.getOutputData().getString(VERSION_CODE_SERVER));
                        Snackbar.make(iv_drawer_setting, R.string.update_it_is_no_new_version, Snackbar.LENGTH_SHORT).show();
                    } else {
                        Snackbar.make(iv_drawer_setting, R.string.all_error_internet, Snackbar.LENGTH_SHORT).show();
                    }
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
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setView(R.layout.dialog_open_by_name);
                final AlertDialog dialog = builder.show();
                final ImageButton ib_dialog_open_by_name = dialog.findViewById(R.id.ib_dialog_open_by_name);
                final EditText et_dialog_open_by_name = dialog.findViewById(R.id.et_dialog_open_by_name);
                final LinearLayout ll_dialog_open_by_name = dialog.findViewById(R.id.ll_dialog_open_by_name);

                final LinkedList<String> list = new LinkedList<>();
                String streamTyped = sp.getString(TWITCH_STREAMS_TYPED, "[]");
                String arraysStreams[] = streamTyped.substring(1, streamTyped.length() - 1).split(", ");
                list.addAll(Arrays.asList(arraysStreams));
                for (final String i : list) {
                    TextView tv = (TextView) getLayoutInflater().inflate(R.layout.item_list_stream_typed, null);
                    tv.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (et_dialog_open_by_name != null)
                                et_dialog_open_by_name.setText(i);
                        }
                    });
                    tv.setText(i);
                    if (ll_dialog_open_by_name != null)
                        ll_dialog_open_by_name.addView(tv);
                }

                if (ib_dialog_open_by_name != null)
                    ib_dialog_open_by_name.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (et_dialog_open_by_name != null && et_dialog_open_by_name.getText().length() > 0) {
                                if (!list.contains(et_dialog_open_by_name.getText().toString().trim()))
                                    list.addFirst(et_dialog_open_by_name.getText().toString().trim());
                                while (list.size() > 4) {
                                    list.pollLast();
                                }
                                sp.edit().putString(TWITCH_STREAMS_TYPED, Arrays.toString(list.toArray())).apply();
                                Intent intent = new Intent(MainActivity.this, TwitchGameActivity.class);
                                intent.putExtra(CHANNEL_NAME, et_dialog_open_by_name.getText().toString().trim());
                                intent.putExtra(CHANNEL_TITLE, "");
                                startActivity(intent);
                                dialog.dismiss();
                            }
                        }
                    });
                break;
            case R.id.menu_fragment_streams_open_multitab:
                if (sp.getBoolean(PREMIUM, false)) {
                    fragmentManager = getSupportFragmentManager();
                    currentFragment = fragmentManager.findFragmentById(R.id.details);
                    FragmentTransaction ft = fragmentManager.beginTransaction();
                    if (currentFragment.getTag() != null && currentFragment.getTag().equals(TV_DRAWER_MULTI_STREAM_TAG)) {
                        ft.replace(R.id.details, new StreamsFragment(), TV_DRAWER_STREAM_TAG).commit();
                    } else {
                        ft.replace(R.id.details, new MultiStreamsFragment(), TV_DRAWER_MULTI_STREAM_TAG).commit();
                    }
                } else {
                    showSnackBarPremium();
                }

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
        unregisterReceiver(connections);
        sensorTranslationUpdater.unregisterSensorManager();
        sp.edit().putInt(MySharedPreferences.LAST_FRAGMENT_SELECTED_KEY, MySharedPreferences.LAST_FRAGMENT_SELECTED).apply();
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
        regReceiver();
        sensorTranslationUpdater.registerSensorManager();
        UpdateUserRank();
        incCountEnter();
    }

    private void regReceiver() {
        IntentFilter filter = new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE");
        filter.addAction("setToolbarName");
        registerReceiver(connections, filter);
        //можно затригерить ресивер этой командой
//        getActivity().sendBroadcast(new Intent("setToolbarName"));
    }

    private void setToolbar() {
        switch (MySharedPreferences.LAST_FRAGMENT_SELECTED) {
            default:
            case 1:
                setToolbarName(R.string.drawer_heroes);
                break;
            case 2:
                setToolbarName(R.string.drawer_items);
                break;
            case 3:
                setToolbarName(R.string.drawer_news);
                break;
            case 4:
                setToolbarName(R.string.drawer_games);
                break;
            case 5:
                setToolbarName(R.string.drawer_stream);
                break;
            case 6:
                setToolbarName(R.string.drawer_game);
                break;
        }
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
        }, Constants.MILLIS_FOR_EXIT);
    }

    private void UpdateUserRank() {
        if (sp.getBoolean(getString(R.string.setting_account_key), false)) {
            if (sp.getString(USER_ID, "").equals("")) {
                findViewById(R.id.b_fragment_drawer_login).setVisibility(View.VISIBLE);
                findViewById(R.id.parallax).setVisibility(View.GONE);
            } else {
                findViewById(R.id.b_fragment_drawer_login).setVisibility(View.GONE);
                findViewById(R.id.parallax).setVisibility(View.VISIBLE);
//                & !sp.getString(DAY_FOR_UPDATE, "0").equals(MySharedPreferences.getTodayDate())
                if (Utils.isNetworkAvailable(this)) {
                    onActivityResult(LogInActivity.REQUEST_CODE_LOG_IN, RESULT_OK, null);
                } else {
                    Picasso.get().load(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/avatar.png")).into(iv_user_ava);
                    Picasso.get().load(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/rank.png")).into(iv_user_medal);
                    Picasso.get().load(new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/stars.png")).into(iv_user_stars);
                    tv_user_nickname.setText(sp.getString(USER_NAME, ""));
                    tv_user_rang.setText(sp.getString(USER_PLACE, ""));
                }
            }
        } else {
            findViewById(R.id.b_fragment_drawer_login).setVisibility(View.GONE);
            findViewById(R.id.parallax).setVisibility(View.GONE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK & requestCode == LogInActivity.REQUEST_CODE_LOG_IN) {
            b_fragment_drawer_login.setVisibility(View.GONE);
            appExecutors.networkIO().execute(new Runnable() {
                @Override
                public void run() {
                    String userId = sp.getString(USER_ID, "");
                    try {
                        //загружаем все ссылки на изображения
                        Document doc = Jsoup.connect("https://www.dotabuff.com/players/" + userId).get();
                        final String userName = doc.getElementsByClass("header-content-title").get(0).child(0).childNode(0).toString();
                        final String userPlace = doc.getElementsByClass("leaderboard-rank-value").text();
                        final String userRank = doc.getElementsByClass("image-player image-bigavatar").attr("src");
                        final String userStars = doc.getElementsByClass("rank-tier-pip").attr("src");
                        final String userAvatar = doc.getElementsByClass("rank-tier-base").attr("src");
                        //сохраняем текстовые данные на устройсво
                        appExecutors.diskIO().execute(new Runnable() {
                            @Override
                            public void run() {
                                sp.edit().putString(USER_NAME, userName).commit();
                                sp.edit().putString(USER_PLACE, userPlace).commit();
                                sp.edit().putString(DAY_FOR_UPDATE, MySharedPreferences.getTodayDate()).commit();
                            }
                        });
                        //ставим изображения после их скачивания в память
                        appExecutors.mainThread().execute(new Runnable() {
                            @Override
                            public void run() {
//                                log("user_nickname_loaded");
                                tv_user_nickname.setText(userName);
//                                log("user_rang_loaded");
                                tv_user_rang.setText(userPlace);
                                if (!userAvatar.isEmpty())
                                    Picasso.get().load(userAvatar).placeholder(R.drawable.spell_placeholder).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                            appExecutors.diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/rank.png");
                                                        if (file.exists()) file.delete();
                                                        file.createNewFile();
                                                        FileOutputStream ostream = new FileOutputStream(file);
                                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                                        ostream.flush();
                                                        ostream.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
                                            log("user_medal_loaded");
                                            iv_user_medal.setImageBitmap(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                                if (!userRank.isEmpty())
                                    Picasso.get().load(userRank).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                            appExecutors.diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/avatar.png");
                                                        if (file.exists()) file.delete();
                                                        file.createNewFile();
                                                        FileOutputStream ostream = new FileOutputStream(file);
                                                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, ostream);
                                                        ostream.flush();
                                                        ostream.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
//                                            log("user_avatar_loaded");
                                            iv_user_ava.setImageBitmap(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                                if (!userStars.isEmpty())
                                    Picasso.get().load(userStars).into(new Target() {
                                        @Override
                                        public void onBitmapLoaded(final Bitmap bitmap, Picasso.LoadedFrom from) {
                                            appExecutors.diskIO().execute(new Runnable() {
                                                @Override
                                                public void run() {
                                                    try {
                                                        File file = new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/stars.png");
                                                        if (file.exists()) file.delete();
                                                        file.createNewFile();
                                                        FileOutputStream ostream = new FileOutputStream(file);
                                                        bitmap.compress(Bitmap.CompressFormat.PNG, 100, ostream);
                                                        ostream.flush();
                                                        ostream.close();

                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                }
                                            });
//                                            log("user_stars_loaded");
                                            iv_user_stars.setImageBitmap(bitmap);
                                        }

                                        @Override
                                        public void onBitmapFailed(Exception e, Drawable errorDrawable) {

                                        }

                                        @Override
                                        public void onPrepareLoad(Drawable placeHolderDrawable) {

                                        }
                                    });
                            }
                        });
                        log("user_account_information_updated");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            });
        }
    }

}
