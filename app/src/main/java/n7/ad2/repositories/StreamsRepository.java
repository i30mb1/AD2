package n7.ad2.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import n7.ad2.retrofit.streams.Streams;
import n7.ad2.storage.StreamsSourceFactory;
import n7.ad2.storage.StreamsStorage;

public class StreamsRepository {

    private StreamsStorage streamsStorage;

    public StreamsRepository(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    public LiveData<PagedList<Streams>> getStreams() {
        StreamsSourceFactory sourceFactory = new StreamsSourceFactory(streamsStorage);
        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build();
        LiveData<PagedList<Streams>> pagedListLiveData = new LivePagedListBuilder<>(sourceFactory, config).build();
        return pagedListLiveData;
    }

    public LiveData<Boolean> getStatusLoading() {
        return streamsStorage.isLoading();
    }
}
