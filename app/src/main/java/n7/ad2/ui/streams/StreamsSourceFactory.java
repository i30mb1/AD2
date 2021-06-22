package n7.ad2.ui.streams;

import androidx.paging.DataSource;

import n7.ad2.data.source.remote.model.Stream;

public class StreamsSourceFactory extends DataSource.Factory<Integer, Stream> {

    private final StreamsStorage streamsStorage;

    StreamsSourceFactory(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    @Override
    public DataSource<Integer, Stream> create() {
        return new PositionalStreamsDataSource(streamsStorage);
    }
}
