package n7.ad2.heroes.full;


import android.arch.paging.DataSource;

public class ResponsesSourceFactory extends DataSource.Factory<Integer, ResponseModel> {

    private final ResponsesStorage responsesStorage;
    private String search;
    private PositionalResponsesDataSource responsesDataSource;

    ResponsesSourceFactory(final ResponsesStorage responsesStorage, String search) {
        this.responsesStorage = responsesStorage;
        responsesStorage.setInvalidate(new Invalidate() {
            @Override
            public void invalidate() {
                if (responsesDataSource != null) {
                    responsesDataSource.invalidate();
                }
            }
        });
        this.search = search;
    }

    @Override
    public DataSource<Integer, ResponseModel> create() {
        responsesDataSource = new PositionalResponsesDataSource(responsesStorage, search);
        return responsesDataSource;
    }
}
