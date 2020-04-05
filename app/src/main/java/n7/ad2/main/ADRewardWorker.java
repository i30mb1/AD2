package n7.ad2.main;

import android.content.Context;
import android.content.Intent;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;

import androidx.work.Worker;
import androidx.work.WorkerParameters;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;

public class ADRewardWorker extends Worker {

    public ADRewardWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {


        return Result.success();
    }
}
