package n7.ad2.splash;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.graphics.drawable.Drawable;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.net.ssl.SSLContext;

import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.ExistingWorkPolicy;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import n7.ad2.R;
import n7.ad2.heroes.db.HeroesRoomDatabase;
import n7.ad2.items.db.ItemsRoomDatabase;
import n7.ad2.news.NewsWorker;
import n7.ad2.setting.purchaseUtils.IabHelper;
import n7.ad2.setting.purchaseUtils.IabResult;
import n7.ad2.setting.purchaseUtils.Inventory;
import n7.ad2.utils.PlainAdapter;
import n7.ad2.utils.SingleLiveEvent;

import static n7.ad2.news.NewsWorker.DELETE_TABLE;
import static n7.ad2.setting.SettingActivity.ONCE_PER_MONTH_SUBSCRIPTION;
import static n7.ad2.setting.SettingActivity.SUBSCRIPTION;
import static n7.ad2.utils.BaseActivity.THEME_DARK;
import static n7.ad2.utils.BaseActivity.THEME_GRAY;
import static n7.ad2.utils.BaseActivity.THEME_WHITE;

public class SplashViewModel extends AndroidViewModel {

    public static final String CURRENT_DAY_IN_APP = "CURRENT_DAY_IN_APP";
    public static final String FREE_SUBSCRIPTION_DAYS = "FREE_SUBSCRIPTION_DAYS";
    private static final String FREE_SUBSCRIPTION_DAY_LAST_USE = "FREE_SUBSCRIPTION_DAY_LAST_USE";
    private static final String NEWS_LOAD_LAST_DAY = "NEWS_LOAD_LAST_DAY";
    public static final String ADMOB_APP_ID = "ca-app-pub-5742225922710304~2823923052";
    final SingleLiveEvent<Void> startMainActivity = new SingleLiveEvent<>();
    public ObservableField<Drawable> resId = new ObservableField<>();
    public ObservableInt scrollTo = new ObservableInt();
    private Application application;
    private Executor diskIO;
    private int currentDay;
    private PlainAdapter adapter;

    public SplashViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.diskIO = Executors.newSingleThreadExecutor();

        setupAdapter();
        setupCurrentDay();
        setupAppIcon();
        setupSupportJsoupForOldDevices();
        setupFirebaseAnalytics();
        setupPurchaseHelper();

        initialLoadDatabase();
        loadNews();
    }

    private void setupAdapter() {
        adapter = new PlainAdapter();
        log("welcome_back_master");
    }

    public PlainAdapter getAdapter() {
        return adapter;
    }

    private void loadNews() {
        int lastDayWhenLoadNews = PreferenceManager.getDefaultSharedPreferences(application).getInt(NEWS_LOAD_LAST_DAY, 0);
        if (currentDay != lastDayWhenLoadNews) {
            Constraints constraints = new Constraints.Builder().setRequiredNetworkType(NetworkType.CONNECTED).build();
            Data data = new Data.Builder().putBoolean(DELETE_TABLE, true).build();
            OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(NewsWorker.class).setInputData(data).setConstraints(constraints).build();
            WorkManager.getInstance().beginUniqueWork(NewsWorker.TAG, ExistingWorkPolicy.APPEND, worker).enqueue();

            PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(NEWS_LOAD_LAST_DAY, currentDay).apply();
            log("loading_news_status = is_loading");
        } else {
            log("loading_news_status = was_loaded");
        }
    }

    private void log(String text) {
        adapter.add(text);
        scrollTo.set(adapter.getItemCount() - 1);
        adapter.notifyDataSetChanged();
    }

    private void setupCurrentDay() {
        String currentDayInString = new SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().getTime());
        currentDay = Integer.valueOf(currentDayInString);
        PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(CURRENT_DAY_IN_APP, currentDay).apply();
        log("today_is_day_number#" + currentDayInString);
    }

    private void setupAppIcon() {
        switch (PreferenceManager.getDefaultSharedPreferences(application).getString(application.getString(R.string.setting_theme_key), THEME_GRAY)) {
            default:
            case THEME_GRAY:
                resId.set(application.getResources().getDrawable(R.drawable.icon_gray));
                break;
            case THEME_WHITE:
                resId.set(application.getResources().getDrawable(R.drawable.icon_white));
                break;
            case THEME_DARK:
                resId.set(application.getResources().getDrawable(R.drawable.icon_dark));
                break;
        }
    }

    private void initialLoadDatabase() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                HeroesRoomDatabase.getDatabase(application, diskIO).heroesDao().getAll();
                ItemsRoomDatabase.getDatabase(application, diskIO).itemsDao().getAll();
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startMainActivity.call();
            }
        });
    }

    private void setupSupportJsoupForOldDevices() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    ProviderInstaller.installIfNeeded(application);
                    SSLContext sslContext = SSLContext.getInstance("TLSv1.2");
                    sslContext.init(null, null, null);
                    sslContext.createSSLEngine();
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
        }).start();
    }

    private void setupPurchaseHelper() {
        String base64EncodedPublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyr808/wY0nXtrh7WEp7ZXqkdZTcwgIaEfKvqKtjkR0KJm/O0B6mLAOn2lNYr8j/mrxprxUk1eLHJtUH2NGzCJzs40AdG5XL/2X34wQpKfLgHj95yybkOPQH7jtHdG15+jwkmT4X80icUpKfoEbHOLzwzQyUn97IdR4X2pQpbI4v5Xy6oYvBLtvcAZoYXUoTlUua+8N8ZGLFqTNlWeBk7rToC7jtXKEMDWYucjoMs2uyrZ2x04+/KFwakDH12MMsNmT1Xuo5Vx6gwZ9QflT6cMd5y0xQlXlGoWeeFUIEIMfyV4s1BSPs3LiYSgNrYLLJ24pznoPtW26Ro4xRpOV4cyQIDAQAB";
        final IabHelper mHelper = new IabHelper(application, base64EncodedPublicKey);
        mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
            @Override
            public void onIabSetupFinished(IabResult result) {
                if (result.isSuccess()) {
                    checkInventory(mHelper);
                }
            }
        });

    }

    private void checkInventory(final IabHelper mHelper) {
        try {
            mHelper.queryInventoryAsync(new IabHelper.QueryInventoryFinishedListener() {
                @Override
                public void onQueryInventoryFinished(IabResult result, Inventory inv) {
                    if (mHelper == null) return;
                    if (result.isFailure()) return;

                    if (inv.hasPurchase(ONCE_PER_MONTH_SUBSCRIPTION)) {
                        setSubscriptionMessage(true);
                    } else {
                        int freeSubscriptionDayLastUse = PreferenceManager.getDefaultSharedPreferences(application).getInt(FREE_SUBSCRIPTION_DAY_LAST_USE, 0);
                        int freeSubscriptionDays = PreferenceManager.getDefaultSharedPreferences(application).getInt(FREE_SUBSCRIPTION_DAYS, 0);
                        if (currentDay == freeSubscriptionDayLastUse) {
                            setSubscriptionMessage(true);
                        } else {
                            if (freeSubscriptionDays <= 0) {
                                setSubscriptionMessage(false);
                            } else {
                                freeSubscriptionDays--;
                                PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(FREE_SUBSCRIPTION_DAYS, freeSubscriptionDays).apply();
                                PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(FREE_SUBSCRIPTION_DAY_LAST_USE, currentDay).apply();
                                setSubscriptionMessage(true);
                            }
                        }
                        log("subscription_days_remain = " + freeSubscriptionDays);
                    }

                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    private void setSubscriptionMessage(boolean subscription) {
        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean(SUBSCRIPTION, subscription).apply();
        log("subscription_status = " + subscription);
    }

    private void setupFirebaseAnalytics() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                FirebaseAnalytics.getInstance(application);
                MobileAds.initialize(application, ADMOB_APP_ID);
            }
        });
    }

}
