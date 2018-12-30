package n7.ad2.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import n7.ad2.repositories.StreamsRepository;
import n7.ad2.retrofit.streams.Streams;
import n7.ad2.storage.StreamsStorage;

public class StreamsViewModel extends AndroidViewModel {

    private final StreamsRepository streamsRepository;

    public StreamsViewModel(@NonNull Application application) {
        super(application);
        StreamsStorage streamsStorage = new StreamsStorage(application);
        this.streamsRepository = new StreamsRepository(streamsStorage);
    }

    public LiveData<PagedList<Streams>> getStreams() {
        return streamsRepository.getStreams();
    }

    public LiveData<Boolean> getStatusLoading() {
        return streamsRepository.getStatusLoading();
    }

}
