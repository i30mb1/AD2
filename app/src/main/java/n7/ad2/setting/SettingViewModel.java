package n7.ad2.setting;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class SettingViewModel extends AndroidViewModel {


    public SettingViewModel(@NonNull Application application) {
        super(application);

        initDialogSubscription(application);

    }

    public void initDialogSubscription(Application application) {

    }


}
