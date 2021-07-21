package n7.ad2.ui.streams;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.databinding.ObservableBoolean;
import androidx.annotation.NonNull;


public class StreamsViewModel extends AndroidViewModel {

    Application application;
    private final StreamsStorage streamsStorage;
    public ObservableBoolean isLoading = new ObservableBoolean();

    public StreamsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        streamsStorage = new StreamsStorage(application, isLoading);
    }

}
