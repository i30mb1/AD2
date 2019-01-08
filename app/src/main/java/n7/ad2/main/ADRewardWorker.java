package n7.ad2.main;

import android.content.Context;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static n7.ad2.setting.SettingActivity.SUBSCRIPTION;

public class ADRewardWorker extends Worker {

    public ADRewardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit()
                .putBoolean(SUBSCRIPTION,false).apply();

        return Result.success();
    }
}
