package n7.ad2.games;


import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Calendar;
import java.util.Objects;

import n7.ad2.utils.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.main.MainActivity;

import static n7.ad2.utils.MySharedPreferences.IS_USED_5_DAYS_BONUS_FROM_GAME1;

public class GameFragment extends Fragment {

    static public final String SCORE_GAME1_GAME_FRAGMENT = "SCORE_GAME1_GAME_FRAGMENT";
    static public final String SCORE_GAME2_GAME_FRAGMENT = "SCORE_GAME2_GAME_FRAGMENT";
    static public final int COUNT_FOR_5_DAYS_PREMIUM = 49;
    static private final int REQUEST_CODE_GAME1 = 1;
    static private final int REQUEST_CODE_GAME2 = 2;
    private TextView tv_fragment_game_score_game1;
    private int scoreGame1;
    private String scoreGame2;
    private SharedPreferences sp;
    private TextView tv_fragment_game_title_game1;
    private TextView tv_fragment_game_score_game2;
    private TextView tv_fragment_game_title_game2;
    private boolean isPremium = false;

    public GameFragment() {
        MySharedPreferences.LAST_FRAGMENT_SELECTED = 6;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_game, container, false);

        getActivity().setTitle(R.string.games);
        setHasOptionsMenu(true);
        sp = MySharedPreferences.getSharedPreferences(getContext());

        initGame1(view);
        initGame2(view);

        return view;
    }


    private void initGame2(View view) {
        Button b_fragment_game_player1_game2 = view.findViewById(R.id.b_fragment_game_player1_game2);
        Button b_fragment_game_player2_game2 = view.findViewById(R.id.b_fragment_game_player2_game2);
        tv_fragment_game_score_game2 = view.findViewById(R.id.tv_fragment_game_score_game2);
        tv_fragment_game_title_game2 = view.findViewById(R.id.tv_fragment_game_title_game2);

        setScoreGame2();

        b_fragment_game_player1_game2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame2Persons1Activity();
            }
        });
        b_fragment_game_player2_game2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    private void setScoreGame2() {
        scoreGame2 = sp.getString(SCORE_GAME2_GAME_FRAGMENT, "");
        tv_fragment_game_score_game2.setText(String.valueOf(scoreGame2));
    }

    private void launchGame2Persons1Activity() {
        Pair<View, String> p2 = Pair.create((View) tv_fragment_game_title_game2, "tv2");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
        Intent intent = new Intent(getContext(), Game2Persons1Activity.class);
        startActivityForResult(intent, REQUEST_CODE_GAME2, optionsCompat.toBundle());
    }

    private void initGame1(View view) {
        tv_fragment_game_score_game1 = view.findViewById(R.id.tv_fragment_game_score_game1);
        Button b_fragment_game_medium_game1 = view.findViewById(R.id.b_fragment_game_medium_game1);
        Button b_fragment_game_hard_game1 = view.findViewById(R.id.b_fragment_game_hard_game1);
        tv_fragment_game_title_game1 = view.findViewById(R.id.tv_fragment_game_title_game1);

        scoreGame1 = sp.getInt(SCORE_GAME1_GAME_FRAGMENT, 0);
        tv_fragment_game_score_game1.setText(String.valueOf(scoreGame1));

        checkAchievedBonus();

        b_fragment_game_medium_game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchGame1Activity();
            }
        });

        b_fragment_game_hard_game1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                launchGame1HardActivity();
            }
        });
    }

    private void launchGame1Activity() {
        Pair<View, String> p2 = Pair.create((View) tv_fragment_game_title_game1, "tv");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
        Intent intent = new Intent(getContext(), Game1Activity.class);
        intent.putExtra(SCORE_GAME1_GAME_FRAGMENT, scoreGame1);
        startActivityForResult(intent, REQUEST_CODE_GAME1, optionsCompat.toBundle());
    }

    private void launchGame1HardActivity() {
        Pair<View, String> p2 = Pair.create((View) tv_fragment_game_title_game1, "tv");
        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(), p2);
        Intent intent = new Intent(getContext(), Game1HardActivity.class);
        intent.putExtra(SCORE_GAME1_GAME_FRAGMENT, scoreGame1);
        startActivityForResult(intent, REQUEST_CODE_GAME1, optionsCompat.toBundle());
    }

    private void checkAchievedBonus() {
        if (sp.getBoolean(IS_USED_5_DAYS_BONUS_FROM_GAME1, false)) {
            tv_fragment_game_score_game1.setTextColor(getResources().getColor(android.R.color.holo_green_light));
        } else {
            tv_fragment_game_score_game1.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (!(resultCode == Activity.RESULT_OK)) return;
        if (requestCode == REQUEST_CODE_GAME1) {
            checkResultFromGame1(data);
        }
        if (requestCode == REQUEST_CODE_GAME2) {
            checkResultFromGame2(data);
        }

    }

    private void checkResultFromGame2(Intent data) {
        String newData = data.getStringExtra(SCORE_GAME2_GAME_FRAGMENT);
        tv_fragment_game_score_game2.setText(newData);
    }

    private void checkResultFromGame1(Intent data) {
        int newData = data.getIntExtra(SCORE_GAME1_GAME_FRAGMENT, 0);
        if (newData > scoreGame1) {
            tv_fragment_game_score_game1.setText(String.valueOf(newData));
            scoreGame1 = newData;
            sp.edit().putInt(SCORE_GAME1_GAME_FRAGMENT, scoreGame1).apply();
            if (newData > COUNT_FOR_5_DAYS_PREMIUM && !sp.getBoolean(IS_USED_5_DAYS_BONUS_FROM_GAME1, false)) {
                final Snackbar snackbar = Snackbar.make(tv_fragment_game_score_game1, R.string.game_fragment_earn_5_premium, Snackbar.LENGTH_INDEFINITE);
                snackbar.setAction(android.R.string.ok, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Calendar calendar = Calendar.getInstance();
                        Long inMemoryTime = sp.getLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis());
                        Long currentTime = calendar.getTimeInMillis();
                        Long latestTime = (inMemoryTime > currentTime ? inMemoryTime : currentTime);
                        calendar.setTimeInMillis(latestTime);
                        calendar.add(Calendar.DATE, 5);
                        sp.edit().putLong(MySharedPreferences.DATE_END_PREMIUM, calendar.getTimeInMillis()).apply();
                        sp.edit().putBoolean(IS_USED_5_DAYS_BONUS_FROM_GAME1, true).apply();
                        checkAchievedBonus();
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                            ((MainActivity) Objects.requireNonNull(getActivity())).log("5_day_premium_obtained");
                        }
                        snackbar.dismiss();
                    }
                });
                snackbar.show();
            }
        }
    }
}
