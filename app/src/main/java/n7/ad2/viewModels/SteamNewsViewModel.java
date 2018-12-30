package n7.ad2.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import n7.ad2.db.news.SteamNews;
import n7.ad2.repositories.SteamDbNewsRepository;
import n7.ad2.repositories.SteamNewsRepository;

public class SteamNewsViewModel extends AndroidViewModel {

//    private final SteamNewsRepository steamNewsRepository;
    private final SteamDbNewsRepository steamDbNewsRepository;

    public SteamNewsViewModel(@NonNull Application application) {
        super(application);
//        steamNewsRepository = new SteamNewsRepository(application);
        steamDbNewsRepository = new SteamDbNewsRepository(application);
    }

    public LiveData<PagedList<SteamNews>> getSteamNews() {
        return steamDbNewsRepository.getSteamNews();
    }
}
