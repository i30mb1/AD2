package n7.ad2.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.Intent;
import android.databinding.ObservableBoolean;
import android.databinding.ObservableInt;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.ad2.BuildConfig;
import n7.ad2.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.SingleLiveEvent;
import n7.ad2.SnackbarMessage;
import n7.ad2.adapter.PlainTextAdapter;
import n7.ad2.db.n7message.N7Message;
import n7.ad2.db.n7message.N7MessageRoomDatabase;
import n7.ad2.retrofit.update.Update;
import n7.ad2.retrofit.update.UpdateApi;
import n7.ad2.setting.SettingActivity;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static n7.ad2.splash.SplashActivityViewModel.CURRENT_DAY_IN_APP;

public class MainViewModel extends AndroidViewModel {

    public static final String SHOULD_UPDATE_FROM_MARKET = "SHOULD_UPDATE_FROM_MARKET";
    public static final String ACCOUNTS_FOR_TOP_TWITCH = "ACCOUNTS_FOR_TOP_TWITCH";
    public static final String LAST_DAY_WHEN_CHECK_UPDATE = "LAST_DAY_WHEN_CHECK_UPDATE";
    private static final String BASE_URL = "https://raw.githubusercontent.com/i30mb1/AD2/master/";
    public final SingleLiveEvent<Void> showDialogUpdate = new SingleLiveEvent<>();
    public final SnackbarMessage snackbarMessage = new SnackbarMessage();
    private final PlainTextAdapter adapter;
    public ObservableBoolean isUpdating = new ObservableBoolean(false);
    public ObservableInt scrollTo = new ObservableInt();
    private Application application;
    private Executor diskIO;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        diskIO = Executors.newSingleThreadExecutor();

        adapter = new PlainTextAdapter();

        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                checkLastVersion();
            }
        });

    }

    public PlainTextAdapter getAdapter() {
        return adapter;
    }

    public void startActivityOptions(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    private void checkLastVersion() {
        int currentDay = PreferenceManager.getDefaultSharedPreferences(application).getInt(CURRENT_DAY_IN_APP, 0);
        int lastDayWhenCheckUpdate = PreferenceManager.getDefaultSharedPreferences(application).getInt(LAST_DAY_WHEN_CHECK_UPDATE, 0);
        if (currentDay != lastDayWhenCheckUpdate) {
            startUpdate();
            PreferenceManager.getDefaultSharedPreferences(application).edit().putInt(LAST_DAY_WHEN_CHECK_UPDATE, currentDay).apply();
        }
    }

    private void log(String text) {
        adapter.add(text);
        scrollTo.set(adapter.getItemCount() - 1);
    }

    private void saveMessageInDatabase(Update update) {
        N7Message object = new N7Message();
        object.message = update.getMessage().getN7message();
        N7MessageRoomDatabase.getDatabase(application).n7MessageDao().setMessage(object);
    }

    private void saveTwitchAccounts(Update update) {
        MySharedPreferences.getSharedPreferences(application).edit().putString(ACCOUNTS_FOR_TOP_TWITCH, update.getMessage().getTwitch()).apply();
    }

    public void startUpdate() {
        diskIO.execute(new Runnable() {
            @Override
            public void run() {
                isUpdating.set(true);
                log("checking_update");
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
                Integer deviceVersion = Integer.valueOf(BuildConfig.VERSION_NAME);

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL) //базовая часть адреса
                        .addConverterFactory(GsonConverterFactory.create()) //конвертер, необходимый для преобразования JSON'а в объекты
                        .build();

                UpdateApi updateApi = retrofit.create(UpdateApi.class);
                Call<Update> updateCall = updateApi.getUpdate();
                try {
                    Response response = updateCall.execute();
                    if (response.isSuccessful()) {
                        Update update = (Update) response.body();
                        if (update == null) return;

                        saveMessageInDatabase(update);
                        saveTwitchAccounts(update);

                        boolean shouldUpdateFromGoogleMarket = update.getMessage().isUpdateFromMarket();
                        PreferenceManager.getDefaultSharedPreferences(application).edit().putBoolean(SHOULD_UPDATE_FROM_MARKET, shouldUpdateFromGoogleMarket).apply();

                        int serverVersion = update.getVersionCode();
                        if (serverVersion > deviceVersion) {
                            showDialogUpdate.call();
                        } else {
                            snackbarMessage.postValue(R.string.update_ok);
                        }
                        log("device_version::" + deviceVersion);
                        log("server_version::" + serverVersion);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    isUpdating.set(false);
                }
            }
        });
    }
}
