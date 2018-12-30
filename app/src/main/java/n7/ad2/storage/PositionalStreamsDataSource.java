package n7.ad2.storage;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import n7.ad2.retrofit.streams.Streams;

public class PositionalStreamsDataSource extends PositionalDataSource<Streams> {

    private final StreamsStorage streamsStorage;

    public PositionalStreamsDataSource(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Streams> callback) {
        List<Streams> list = new ArrayList<>();
        list.addAll(streamsStorage.getPremium());
        list.addAll(streamsStorage.getData(params.requestedStartPosition, params.requestedLoadSize));
        callback.onResult(list, 0);
//        callback.onResult(list, 0, streamsStorage.getTotal());
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Streams> callback) {
        List<Streams> list = streamsStorage.getData(params.startPosition, params.loadSize);
        callback.onResult(list);
    }

}
