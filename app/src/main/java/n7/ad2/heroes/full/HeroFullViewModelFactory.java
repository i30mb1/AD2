package n7.ad2.heroes.full;

import android.app.Application;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.annotation.NonNull;

import n7.ad2.ui.heroInfo.HeroInfoViewModel;

public class HeroFullViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final String heroName;
    private final Application application;

    public HeroFullViewModelFactory(Application application, String heroName) {

        this.heroName = heroName;
        this.application = application;
    }

    @SuppressWarnings("unchecked")
    @NonNull
    @Override
    public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
        if (modelClass == HeroInfoViewModel.class) {
//            return (T) new HeroInfoViewModel(application, heroName);
        }
        return super.create(modelClass);
    }
}
