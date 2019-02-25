package n7.ad2.heroes.full;

import android.arch.paging.PositionalDataSource;
import android.support.annotation.NonNull;

import java.util.List;

public class PositionalResponsesDataSource extends PositionalDataSource<ResponseModel> {

    private final ResponsesStorage responsesStorage;
    private String search;

    public PositionalResponsesDataSource(ResponsesStorage responsesStorage, String search) {
        this.responsesStorage = responsesStorage;
        this.search = search;
    }

    @Override
    public void loadInitial(@NonNull LoadInitialParams params, @NonNull LoadInitialCallback<ResponseModel> callback) {
        if (search.equals("")) {
            List<ResponseModel> list = responsesStorage.getData(params.requestedStartPosition, params.requestedLoadSize);
            callback.onResult(list, params.requestedStartPosition);
            //todo check this
        } else {
            List<ResponseModel> list = responsesStorage.getDataSearch(search);
            callback.onResult(list, 0);
        }

    }

    @Override
    public void loadRange(@NonNull LoadRangeParams params, @NonNull LoadRangeCallback<ResponseModel> callback) {
        if (search.equals("")) {
            List<ResponseModel> list = responsesStorage.getData(params.startPosition, params.loadSize);
            callback.onResult(list);
        }
    }
}
