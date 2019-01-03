package n7.ad2.main;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import n7.ad2.BuildConfig;
import n7.ad2.MySharedPreferences;
import n7.ad2.SingleLiveEvent;
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

    public static final String ACCOUNTS_FOR_TOP_TWITCH = "ACCOUNTS_FOR_TOP_TWITCH";
    private static final String LAST_DAY_WHEN_CHECK_UPDATE = "LAST_DAY_WHEN_CHECK_UPDATE";
    private static final String BASE_URL = "https://raw.githubusercontent.com/i30mb1/AD2/master/";
    final SingleLiveEvent<String> logEvent = new SingleLiveEvent<>();
    private Application application;
    private Executor diskIO;

    public MainViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
        diskIO = Executors.newSingleThreadExecutor();

    }

    public void startActivityOptions(Context context) {
        context.startActivity(new Intent(context, SettingActivity.class));
    }

    private void checkLastVersion() {
        int currentDay = PreferenceManager.getDefaultSharedPreferences(application).getInt(CURRENT_DAY_IN_APP, 0);
        int lastDayWhenCheckUpdate = PreferenceManager.getDefaultSharedPreferences(application).getInt(LAST_DAY_WHEN_CHECK_UPDATE, 0);
        if (currentDay != lastDayWhenCheckUpdate) {

        }
    }

    private void saveMessageInDatabase(Update update) {
        N7Message object = new N7Message();
        object.message = update.getMessage().getN7message();
        N7MessageRoomDatabase.getDatabase(application).n7MessageDao().setMessage(object);
    }

    private void saveTwitchAccounts(Update update) {
        MySharedPreferences.getSharedPreferences(application).edit().putString(ACCOUNTS_FOR_TOP_TWITCH, update.getMessage().getTwitch()).apply();
    }

    private void startUpdate() {
        Double deviceVersion = Double.valueOf(BuildConfig.VERSION_NAME);

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
                String serverVersion = update.getVersionCode().toString();

                if (serverVersion > deviceVersion) {

                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

//        WorkManager.getInstance().getStatusById(updateAppWorker.getId()).observe(MainActivity.this, new Observer<WorkStatus>() {
//            @Override
//            public void onChanged(@Nullable WorkStatus workStatus) {
//                if (workStatus != null && workStatus.getState().equals(State.SUCCEEDED)) {
//                    log("work_update_succeeded");
//                    isUpdate = false;
//                    final boolean fromMarket = workStatus.getOutputData().getBoolean(UpdateAppWorker.FROM_MARKET, true);
//                    String message = workStatus.getOutputData().getString(UpdateAppWorker.MESSAGE);
//                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
//                    builder.setView(R.layout.dialog_update);
//                    final AlertDialog dialog = builder.show();
//                    dialog.findViewById(R.id.b_dialog_update_no).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            dialog.cancel();
//                        }
//                    });
//                    dialog.findViewById(R.id.b_dialog_update_yes).setOnClickListener(new View.OnClickListener() {
//                        @Override
//                        public void onClick(View view) {
//                            if (fromMarket)
//                                loadNewVersionFromMarket();
//                            else
//                                loadNewVersionFromGitHub();
//                            dialog.cancel();
//                        }
//                    });
//                } else if (isUserInitiative && workStatus != null && workStatus.getState().equals(State.FAILED)) {
//                    log("work_update_failed");
//                    isUpdate = false;
//                    if (Utils.isNetworkAvailable(MainActivity.this)) {
//                        log("app_version=" + workStatus.getOutputData().getString(VERSION_CODE_APP));
//                        log("server_version=" + workStatus.getOutputData().getString(VERSION_CODE_SERVER));
//                        Snackbar.make(iv_drawer_setting, R.string.update_it_is_no_new_version, Snackbar.LENGTH_SHORT).show();
//                    } else {
//                        Snackbar.make(iv_drawer_setting, R.string.all_error_internet, Snackbar.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
    }

}
