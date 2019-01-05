package n7.ad2.tournaments;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import n7.ad2.tournaments.db.Games;

public class GamesViewModel extends AndroidViewModel {

    private final GamesRepository gamesRepository;

    public GamesViewModel(@NonNull Application application) {
        super(application);
        gamesRepository = new GamesRepository(application);
    }

    public LiveData<PagedList<Games>> getGames() {
        return gamesRepository.getGames();
    }

    public LiveData<Games> getGameByUrl(String url) {
        return gamesRepository.getGameByUrl(url);
    }
}
