package n7.ad2.heroes.full;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

public class HeroFullViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final String heroCode;
    private final String heroName;
    private final Application application;

    public HeroFullViewModelFactory(Application application, String heroCode, String heroName) {
        this.heroCode = heroCode;
        this.heroName = heroName;
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == HeroFulViewModel.class) {
            return (T) new HeroFulViewModel(application, heroCode, heroName);
        }
        return super.create(modelClass);
    }
}
