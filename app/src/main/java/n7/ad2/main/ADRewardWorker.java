package n7.ad2.main;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;
import static n7.ad2.setting.SettingActivity.SUBSCRIPTION;

public class ADRewardWorker extends Worker {

    public ADRewardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        boolean subscription = PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).getBoolean(SUBSCRIPTION, false);

        if (subscription) {
            PreferenceManager.getDefaultSharedPreferences(getApplicationContext()).edit().putBoolean(SUBSCRIPTION,false).apply();
            getApplicationContext().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "FREE_SUBSCRIPTION = END"));
        }

        return Result.success();
    }
}
