package n7.ad2.heroes.full;


import android.arch.paging.DataSource;

public class ResponsesSourceFactory extends DataSource.Factory<Integer, ResponseModel> {

    private final ResponsesStorage responsesStorage;
    private String search;

    public ResponsesSourceFactory(ResponsesStorage responsesStorage, String search) {
        this.responsesStorage = responsesStorage;
        this.search = search;
    }

    @Override
    public DataSource<Integer, ResponseModel> create() {
        return new PositionalResponsesDataSource(responsesStorage, search);
    }
}
