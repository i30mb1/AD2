package n7.ad2.storage;

import android.arch.paging.DataSource;

import n7.ad2.retrofit.streams.Streams;

public class StreamsSourceFactory extends DataSource.Factory<Integer, Streams> {

    private final StreamsStorage streamsStorage;

    public StreamsSourceFactory(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    @Override
    public DataSource<Integer, Streams> create() {
        return new PositionalStreamsDataSource(streamsStorage);
    }
}
