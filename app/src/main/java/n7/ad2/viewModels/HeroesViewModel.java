package n7.ad2.viewModels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import java.util.List;

import n7.ad2.db.heroes.Heroes;
import n7.ad2.repositories.HeroesRepository;

public class HeroesViewModel extends AndroidViewModel {

    private final HeroesRepository heroesRepository;

    public HeroesViewModel(@NonNull Application application) {
        super(application);
        heroesRepository = new HeroesRepository(application);
    }

    public LiveData<List<Heroes>> getHeroes() {
        return heroesRepository.getHeroes();
    }

    public LiveData<Heroes> getHero(String codeName) {
        return heroesRepository.getHero(codeName);
    }

    public LiveData<PagedList<Heroes>> getPagedListHeroes() {
        return heroesRepository.getPagedListHeroes();
    }

    public LiveData<PagedList<Heroes>> getPagedListHeroesFilter(String s) {
        return heroesRepository.getPagedListHeroesFilter(s);
    }
}
