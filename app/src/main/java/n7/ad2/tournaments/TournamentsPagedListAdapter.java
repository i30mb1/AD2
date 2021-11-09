package n7.ad2.tournaments;

import static n7.ad2.tournaments.GameFullActivity.URL;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityOptionsCompat;
import androidx.core.util.Pair;
import androidx.databinding.ObservableBoolean;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.Observer;
import androidx.paging.PagedListAdapter;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import n7.ad2.R;
import n7.ad2.tournaments.db.TournamentGame;

public class TournamentsPagedListAdapter extends PagedListAdapter<TournamentGame, TournamentsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<TournamentGame> DIFF_CALLBACK = new DiffUtil.ItemCallback<TournamentGame>() {
        @Override
        public boolean areItemsTheSame(@NonNull TournamentGame tournamentGame, @NonNull TournamentGame t1) {
            return tournamentGame.team1Logo.equals(t1.team1Logo) && tournamentGame.team2Logo.equals(t1.team2Logo);
        }

        @Override
        public boolean areContentsTheSame(@NonNull TournamentGame tournamentGame, @NonNull TournamentGame t1) {
            return true;
        }
    };
    private LifecycleOwner lifecycleOwner;
    private ObservableBoolean isPremium;

    public TournamentsPagedListAdapter(LifecycleOwner lifecycleOwner) {
        super(DIFF_CALLBACK);
        this.isPremium = isPremium;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_tournament_game, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TournamentGame games = getItem(position);
        if (games != null) {
            viewHolder.bindTo(games);
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private final TextView tv_item_list_games_team1_name;
        private final TextView tv_item_list_games_team2_name;
        private final TextView tv_item_list_games_middle;
        private final ImageView iv_item_list_games_team1_logo;
        private final ImageView iv_item_list_games_team2_logo;
        private final CardView root;
        private final Button b_tv_item_list_games;
        private boolean collapse = false;
        private WorkInfo.State status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.root_item_list_tournament_game);
            tv_item_list_games_team1_name = itemView.findViewById(R.id.tv_item_list_tournament_game_team1);
            tv_item_list_games_team2_name = itemView.findViewById(R.id.tv_item_list_tournament_game_team2);
            tv_item_list_games_middle = itemView.findViewById(R.id.tv_item_list_tournament_game_score);
            iv_item_list_games_team1_logo = itemView.findViewById(R.id.iv_item_list_tournament_game_team1);
            iv_item_list_games_team2_logo = itemView.findViewById(R.id.iv_item_list_tournament_game_team2);
            b_tv_item_list_games = itemView.findViewById(R.id.b_item_list_tournament_game);
        }

        private void bindTo(final TournamentGame game) {
            collapse = false;
            hideButtonScheduler();
            tv_item_list_games_team1_name.setText(game.team1Name);
            tv_item_list_games_team2_name.setText(game.team2Name);
            Picasso.get().load(game.team1Logo).placeholder(R.drawable.game_unknown_team1).into(iv_item_list_games_team1_logo);
            Picasso.get().load(game.team2Logo).placeholder(R.drawable.game_unknown_team2).into(iv_item_list_games_team2_logo);
            if (!game.teamScore.equals("")) tv_item_list_games_middle.setText(game.teamScore);
            if (game.teamTime != 0) {
                tv_item_list_games_middle.setText(new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US).format(game.teamTime));
            }
            if (game.teamTimeRemains == 0) {
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), GameFullActivity.class);
                        intent.putExtra(URL, game.url);
                        Pair<View, String> p1 = Pair.create((View) tv_item_list_games_team1_name, "tv1");
                        Pair<View, String> p2 = Pair.create((View) tv_item_list_games_team2_name, "tv2");
                        Pair<View, String> p3 = Pair.create((View) tv_item_list_games_middle, "tv3");
                        Pair<View, String> p4 = Pair.create((View) iv_item_list_games_team1_logo, "iv1");
                        Pair<View, String> p5 = Pair.create((View) iv_item_list_games_team2_logo, "iv2");
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), p1, p2, p3, p4, p5);
                        view.getContext().startActivity(intent, optionsCompat.toBundle());
                    }
                });
            } else {
                WorkManager.getInstance().getWorkInfosByTagLiveData(game.url).observe(lifecycleOwner, new Observer<List<WorkInfo>>() {
                    @Override
                    public void onChanged(@Nullable List<WorkInfo> workInfos) {
                        if (workInfos != null && workInfos.size() == 0) {
                            status = null;
                            b_tv_item_list_games.setText(tv_item_list_games_middle.getContext().getText(R.string.item_list_tournament_game_notification_off));
                            b_tv_item_list_games.setTextColor(tv_item_list_games_middle.getContext().getResources().getColor(android.R.color.holo_red_light));
                            b_tv_item_list_games.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_off, 0, 0, 0);
                        }
                        if (workInfos != null && workInfos.size() != 0) {
                            status = workInfos.get(0).getState();
                            if (workInfos.get(0).getState().equals(WorkInfo.State.ENQUEUED)) {
                                b_tv_item_list_games.setText(tv_item_list_games_middle.getContext().getText(R.string.item_list_tournament_game_notification_on));
                                b_tv_item_list_games.setTextColor(tv_item_list_games_middle.getContext().getResources().getColor(android.R.color.holo_green_light));
                                b_tv_item_list_games.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_on, 0, 0, 0);
                            }
                            if (workInfos.get(0).getState().equals(WorkInfo.State.SUCCEEDED) || workInfos.get(0).getState().equals(WorkInfo.State.CANCELLED)) {
                                status = null;
                                b_tv_item_list_games.setText(tv_item_list_games_middle.getContext().getText(R.string.item_list_tournament_game_notification_off));
                                b_tv_item_list_games.setTextColor(tv_item_list_games_middle.getContext().getResources().getColor(android.R.color.holo_red_light));
                                b_tv_item_list_games.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_alarm_off, 0, 0, 0);
                                WorkManager.getInstance().cancelAllWorkByTag(game.url);
                                WorkManager.getInstance().pruneWork();//очищает все завершённые работы
                            }
                        }
                    }
                });
                b_tv_item_list_games.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (status == null) {
                            Data data = new Data.Builder().putString("message", game.team1Name + " vs " + game.team2Name).build();
                            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                                    .Builder(ScheduleGameWorker.class)
                                    .setInitialDelay(game.teamTimeRemains, TimeUnit.SECONDS)
                                    .setInputData(data)
                                    .addTag(game.url)
                                    .build();
                            WorkManager.getInstance().enqueue(oneTimeWorkRequest);
                            hideButtonScheduler();
                        } else {
                            WorkManager.getInstance().cancelAllWorkByTag(game.url);
                            WorkManager.getInstance().pruneWork();
                            hideButtonScheduler();
                        }
                    }
                });
                if (isPremium.get())
                    root.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            if (collapse) {
                                showButtonScheduler();
                            } else {
                                hideButtonScheduler();
                            }
                        }
                    });
                b_tv_item_list_games.setClickable(false);
            }
        }

        private void showButtonScheduler() {
            tv_item_list_games_middle.animate().alpha(0.0f).setDuration(300).start();
            b_tv_item_list_games.animate().alpha(1.0f).setDuration(300).start();
            b_tv_item_list_games.setClickable(true);
            collapse = !collapse;
        }

        private void hideButtonScheduler() {
            tv_item_list_games_middle.animate().alpha(1.0f).setDuration(300).start();
            b_tv_item_list_games.animate().alpha(0.0f).setDuration(300).start();
            b_tv_item_list_games.setClickable(false);
            collapse = !collapse;
        }
    }


}
