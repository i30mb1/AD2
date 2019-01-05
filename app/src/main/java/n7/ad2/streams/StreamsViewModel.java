package n7.ad2.streams;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.support.annotation.NonNull;

public class StreamsViewModel extends AndroidViewModel {

    Application application;

    public StreamsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;
    }

}
