package n7.ad2.worker;

import android.support.annotation.NonNull;

import java.io.IOException;

import androidx.work.Data;
import androidx.work.Worker;
import n7.ad2.MySharedPreferences;
import n7.ad2.db.n7message.N7Message;
import n7.ad2.db.n7message.N7MessageRoomDatabase;
import n7.ad2.retrofit.update.Update;
import n7.ad2.retrofit.update.UpdateApi;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static n7.ad2.MySharedPreferences.PREMIUM_ACCOUNTS;

public class UpdateAppWorker extends Worker {

    public static final String BASE_URL = "https://raw.githubusercontent.com/i30mb1/AD2/master/";
    public static final String VERSION_CODE_APP = "VERSION_CODE_APP";
    public static final String VERSION_CODE_SERVER = "VERSION_CODE_SERVER";
    public static final String N7MESSAGE = "n7message";
    public static final String MESSAGE = "message";
    public static final String FROM_MARKET = "fromMarket";
    public static final String USER_INITIATION = "userInitiation";

    @NonNull
    @Override
    public Result doWork() {

        Double deviceVersion = getInputData().getDouble(VERSION_CODE_APP, 0);

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
                saveMessageInDatabase(update);
                saveTwitchAccounts(update);
                if (deviceVersion < update.getVersionCode()) {
                    Data data = new Data.Builder()
                            .putString(MESSAGE, update.getMessage().getEn())
                            .putBoolean(FROM_MARKET, update.getMessage().isUpdateFromMarket())
                            .build();
                    setOutputData(data);
                    return Result.SUCCESS;
                } else {
                    Data data = new Data.Builder()
                            .putString(VERSION_CODE_APP, deviceVersion.toString())
                            .putString(VERSION_CODE_SERVER, update.getVersionCode().toString())
                            .build();
                    setOutputData(data);
                    return Result.FAILURE;
                }
            } else {
                return Result.FAILURE;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.FAILURE;
    }

    private void saveMessageInDatabase(Update update) {
        N7Message object = new N7Message();
        object.message = update.getMessage().getN7message();
        N7MessageRoomDatabase.getDatabase(getApplicationContext()).n7MessageDao().setMessage(object);
    }

    private void saveTwitchAccounts(Update update) {
        MySharedPreferences.getSharedPreferences(getApplicationContext())
                .edit().putString(PREMIUM_ACCOUNTS, update.getMessage().getTwitch()).apply();
    }
}
