package n7.ad2.streams;

import android.arch.paging.DataSource;

import n7.ad2.streams.retrofit.Streams;

public class StreamsSourceFactory extends DataSource.Factory<Integer, Streams> {

    private final StreamsStorage streamsStorage;

    StreamsSourceFactory(StreamsStorage streamsStorage) {
        this.streamsStorage = streamsStorage;
    }

    @Override
    public DataSource<Integer, Streams> create() {
        return new PositionalStreamsDataSource(streamsStorage);
    }
}
