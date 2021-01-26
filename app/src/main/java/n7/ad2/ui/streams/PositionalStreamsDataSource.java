package n7.ad2.ui.streams;

import androidx.paging.PositionalDataSource;
import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import n7.ad2.ui.streams.retrofit.Streams;

public class PositionalStreamsDataSource extends PositionalDataSource<Streams> {

    private final StreamsStorage streamsStorage;

    public PositionalStreamsDataSource(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Streams> callback) {
        List<Streams> list = new ArrayList<>();
        list.addAll(streamsStorage.getSubscribersStreams());
        list.addAll(streamsStorage.getData(params.requestedStartPosition, params.requestedLoadSize));
        callback.onResult(list, params.requestedStartPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Streams> callback) {
        List<Streams> list = streamsStorage.getData(params.startPosition, params.loadSize);
        callback.onResult(list);
    }

}
