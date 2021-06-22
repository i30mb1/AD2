package n7.ad2.ui.streams;

import androidx.annotation.NonNull;
import androidx.paging.PositionalDataSource;

import java.util.ArrayList;
import java.util.List;

import n7.ad2.data.source.remote.model.Stream;

public class PositionalStreamsDataSource extends PositionalDataSource<Stream> {

    private final StreamsStorage streamsStorage;

    public PositionalStreamsDataSource(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Stream> callback) {
        List<Stream> list = new ArrayList<>();
        list.addAll(streamsStorage.getSubscribersStreams());
        list.addAll(streamsStorage.getData(params.requestedStartPosition, params.requestedLoadSize));
        callback.onResult(list, params.requestedStartPosition);
    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Stream> callback) {
        List<Stream> list = streamsStorage.getData(params.startPosition, params.loadSize);
        callback.onResult(list);
    }

}
