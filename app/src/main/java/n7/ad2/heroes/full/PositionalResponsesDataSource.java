package n7.ad2.heroes.full;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import java.util.List;

public class PositionalResponsesDataSource extends PositionalDataSource<Response> {

    private final ResponsesStorage responsesStorage;
    private String search;

    public PositionalResponsesDataSource(ResponsesStorage responsesStorage, String search) {
        this.responsesStorage = responsesStorage;
        this.search = search;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<Response> callback) {
        if (search.equals("")) {
            List<Response> list = responsesStorage.getData(params.requestedStartPosition, params.requestedLoadSize);
            callback.onResult(list, params.requestedStartPosition);
        } else {
            List<Response> list = responsesStorage.getDataSearch(search);
            callback.onResult(list, 0);
        }

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<Response> callback) {
        if (search.equals("")) {
            List<Response> list = responsesStorage.getData(params.startPosition, params.loadSize);
            callback.onResult(list);
        }
    }
}
