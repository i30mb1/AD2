package n7.ad2.splash;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.databinding.ObservableInt;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

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

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.SingleLiveEvent;
import n7.ad2.db.heroes.HeroesRoomDatabase;
import n7.ad2.db.items.ItemsRoomDatabase;
import n7.ad2.purchaseUtils.IabHelper;
import n7.ad2.purchaseUtils.IabResult;
import n7.ad2.purchaseUtils.Inventory;
import n7.ad2.worker.SteamDbNewsWorker;

import static n7.ad2.MySharedPreferences.PREMIUM;
import static n7.ad2.activity.BaseActivity.THEME_DARK;
import static n7.ad2.activity.BaseActivity.THEME_GRAY;
import static n7.ad2.activity.BaseActivity.THEME_WHITE;
import static n7.ad2.setting.SettingActivity.ONCE_PER_MONTH_SUBSCRIPTION;
import static n7.ad2.worker.SteamNewsWorker.DELETE_TABLE;

public class SplashActivityViewModel extends AndroidViewModel {

    public static final String CURRENT_DAY_IN_APP = "CURRENT_DAY_IN_APP";
    public static final String FREE_PREMIUM_DAYS = "FREE_PREMIUM_DAYS";
    private static final String LAST_DAY_WHEN_USED_PREMIUM = "LAST_DAY_WHEN_USED_PREMIUM";
    private static final String LAST_DAY_WHEN_LOAD_NEWS = "LAST_DAY_WHEN_LOAD_NEWS";
    final SingleLiveEvent<Void> startMainActivity = new SingleLiveEvent<>();
    final SingleLiveEvent<String> logEvent = new SingleLiveEvent<>();
    public ObservableInt resId = new ObservableInt();
    private Application application;
    private Executor diskIO;
    private int currentDay;

    public SplashActivityViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        this.diskIO = Executors.newSingleThreadExecutor();

        setupCurrentDay();

        setupAppIcon();
        setupSupportJsoupForOldDevices();
        setupFirebaseAnalytics();
        setupPurchaseHelper();

        initialLoadForDB();
        loadNews();
    }

    private void loadNews() {
        int lastDayWhenLoadNews = PreferenceManager.getDefaultSharedPreferences(application).getInt(LAST_DAY_WHEN_LOAD_NEWS, 0);
        if (currentDay != lastDayWhenLoadNews) {
            Data data = new Data.Builder().putBoolean(DELETE_TABLE, true).build();
            OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(SteamDbNewsWorker.class).setInputData(data).build();
            WorkManager.getInstance().enqueue(worker);

            PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(LAST_DAY_WHEN_LOAD_NEWS, currentDay).apply();
            logEvent.postValue("start_loading_news");
        } else {
            logEvent.postValue("no_need_to_loading_news");
        }
    }

    private void setupCurrentDay() {
        String currentDayInString = new SimpleDateFormat("DDD", Locale.US).format(Calendar.getInstance().getTime());
        currentDay = Integer.valueOf(currentDayInString);
        PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(CURRENT_DAY_IN_APP, currentDay).apply();
        logEvent.postValue("current_day=" + currentDayInString);
    }

    private void setupAppIcon() {
        switch (PreferenceManager.getDefaultSharedPreferences(application).getString(application.getString(R.string.setting_theme_key), THEME_GRAY)) {
            default:
            case THEME_GRAY:
                resId.set(R.drawable.icon_gray);
                break;
            case THEME_WHITE:
                resId.set(R.drawable.icon_white);
                break;
            case THEME_DARK:
                resId.set(R.drawable.icon_dark);
                break;
        }
        logEvent.setValue("app_icon_loaded");
    }

    private void initialLoadForDB() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                HeroesRoomDatabase.getDatabase(application, diskIO).heroesDao().getAll();
                ItemsRoomDatabase.getDatabase(application, diskIO).itemsDao().getAll();
                logEvent.postValue("database_loaded");
                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                startMainActivity.call();
            }
        });
    }

//    private void setupLiveDataItems() {
//        DataSource.Factory dataSource = itemsDao.getDataSourceItems();
//        PagedList.Config config = new PagedList.Config.Builder()
//                .setPageSize(25)
//                .setEnablePlaceholders(true)
//                .build();
//        LiveData<PagedList<ItemModel>> items = new LivePagedListBuilder<>(dataSource, config).build();
//    }

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
                        setPremium(true);
                    } else {
                        int freePremiumDayLastUse = PreferenceManager.getDefaultSharedPreferences(application).getInt(LAST_DAY_WHEN_USED_PREMIUM, 0);
                        if (currentDay == freePremiumDayLastUse) {
                            setPremium(true);
                        } else {
                            int freePremiumDays = PreferenceManager.getDefaultSharedPreferences(application).getInt(FREE_PREMIUM_DAYS, 0);
                            if (freePremiumDays <= 0) {
                                setPremium(false);
                            } else {
                                freePremiumDays--;
                                PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(FREE_PREMIUM_DAYS, freePremiumDays).apply();
                                PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(LAST_DAY_WHEN_USED_PREMIUM, currentDay).apply();
                                setPremium(true);
                            }

                        }
                    }

                }
            });
        } catch (IabHelper.IabAsyncInProgressException e) {
            e.printStackTrace();
        }
    }

    private void setPremium(boolean isPremium) {
        MySharedPreferences.getSharedPreferences(application).edit().putBoolean(PREMIUM, isPremium).apply();
        logEvent.postValue("have_premium = " + isPremium);
    }

    private void setupFirebaseAnalytics() {
        FirebaseAnalytics.getInstance(application);
    }

//    public LiveData<HeroModel> getHero(String codeName) {
//        return heroesRepository.getHero(codeName);
//    }
//
//    public LiveData<PagedList<HeroModel>> getPagedListHeroes() {
//        return heroesRepository.getPagedListHeroes();
//    }
//
//    public LiveData<PagedList<HeroModel>> getPagedListHeroesFilter(String s) {
//        return heroesRepository.getPagedListHeroesFilter(s);
//    }
}
