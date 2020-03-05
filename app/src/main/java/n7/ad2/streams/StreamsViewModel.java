package n7.ad2.streams;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.paging.LivePagedListBuilder;
import androidx.paging.PagedList;
import androidx.databinding.ObservableBoolean;
import androidx.annotation.NonNull;

import n7.ad2.streams.retrofit.Streams;

public class StreamsViewModel extends AndroidViewModel {

    Application application;
    private final StreamsStorage streamsStorage;
    public ObservableBoolean isLoading = new ObservableBoolean();

    public StreamsViewModel(@NonNull Application application) {
        super(application);
        this.application = application;

        streamsStorage = new StreamsStorage(application, isLoading);
    }

    public LiveData<PagedList<Streams>> getStreams() {
        StreamsSourceFactory sourceFactory = new StreamsSourceFactory(streamsStorage);
        PagedList.Config config = new PagedList.Config.Builder()
                .setEnablePlaceholders(false)
                .setPageSize(30).build();
        LiveData<PagedList<Streams>> pagedListLiveData = new LivePagedListBuilder<>(sourceFactory, config).build();
        return pagedListLiveData;
    }

}
