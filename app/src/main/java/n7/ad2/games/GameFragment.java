package n7.ad2.games;


import android.content.Intent;
import android.content.SharedPreferences;
import android.databinding.DataBindingUtil;
import android.databinding.ObservableBoolean;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.FragmentGameBinding;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;
import static n7.ad2.setting.SettingActivity.SUBSCRIPTION_PREF;

public class GameFragment extends Fragment implements SharedPreferences.OnSharedPreferenceChangeListener {

    public ObservableBoolean subscription = new ObservableBoolean();
    private FragmentGameBinding binding;

    public GameFragment() {
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_game, container, false);
        binding.setActivity(this);

        return binding.getRoot();
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        subscription.set(PreferenceManager.getDefaultSharedPreferences(getContext()).getBoolean(SUBSCRIPTION_PREF, false));
        PreferenceManager.getDefaultSharedPreferences(getContext()).registerOnSharedPreferenceChangeListener(this);
        getActivity().setTitle(R.string.games);
        getActivity().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "games_activity_created"));
        setHasOptionsMenu(true);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void startG1P1() {
        Pair<View, String> p2 = Pair.create((View) binding.tvFragmentGameG1, "tv1");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
        Intent intent = new Intent(getContext(), Game1p1.class);
        startActivity(intent, optionsCompat.toBundle());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void startG1P2() {
        Intent intent = new Intent(getContext(), Game1p2.class);
        startActivity(intent);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void startG2P1() {
        Pair<View, String> p2 = Pair.create((View) binding.tvActivityGame2p1Title, "tv2");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
        Intent intent = new Intent(getContext(), Game2p1.class);
        startActivity(intent, optionsCompat.toBundle());
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    public void startG2P2() {
        Intent intent = new Intent(getContext(), Game2p2.class);
        startActivity(intent);
    }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
        if (key.equals(SUBSCRIPTION_PREF)) {
            subscription.set(sharedPreferences.getBoolean(key,false));
        }
    }
}
