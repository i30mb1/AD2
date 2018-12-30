package n7.ad2.repositories;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;

import n7.ad2.model.ResponseModel;
import n7.ad2.storage.ResponsesSourceFactory;
import n7.ad2.storage.ResponsesStorage;

public class ResponsesRepository {

    private Application application;
    private ResponsesStorage responsesStorage;
    private String heroFolder;

    public ResponsesRepository(Application application, String heroFolder, String language) {
        this.heroFolder = heroFolder;
        this.application = application;
        // моё хранилище данных
        switch (language) {
//            case "zh":
//                responsesStorage = new ResponsesStorage(application, "heroes/" + heroFolder + "/zh_responses.json");
//                break;
            case "ru":
                responsesStorage = new ResponsesStorage(application, "heroes/" + heroFolder + "/ru_responses.json");
                break;
            default:
                responsesStorage = new ResponsesStorage(application, "heroes/" + heroFolder + "/eng_responses.json");
                break;
        }
    }

    public LiveData<PagedList<ResponseModel>> getPagedList(String search) {
        // DataSource это посредник между PagedList и Storage
        // PositionalResponsesDataSource dataSource = new PositionalResponsesDataSource(responsesStorage);

        // ResponsesSourceFactory фабрика, которую LivePagedListBuilder сможет использовать, чтобы самостоятельно создавать DataSource
        ResponsesSourceFactory sourceFactory = new ResponsesSourceFactory(responsesStorage, search);

        PagedList.Config config = new PagedList.Config.Builder().setEnablePlaceholders(false).setPageSize(20).build();

        // PagedList обёртка над List он содержит данные, умеет отдавать их а также подгружает новые
        // PagedList<ResponseModel> pagedList = new PagedList.Builder<>(dataSource,config).build();
        // обёртка над PagedList чтобы всё это происходило в бэкграунд потоке
        LiveData<PagedList<ResponseModel>> pagedListLiveData = new LivePagedListBuilder<>(sourceFactory, config).build();
        return pagedListLiveData;
    }
}


