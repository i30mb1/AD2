package n7.ad2.adapter;

import android.app.Activity;
import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.State;
import androidx.work.WorkManager;
import androidx.work.WorkStatus;
import n7.ad2.R;
import n7.ad2.activity.GamesPersonalActivity;
import n7.ad2.db.games.Games;
import n7.ad2.worker.ScheduleGameWorker;

import static n7.ad2.MySharedPreferences.ANIMATION_DURATION;
import static n7.ad2.activity.GamesPersonalActivity.URL;

public class GamesPagedListAdapter extends PagedListAdapter<Games, GamesPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Games> DIFF_CALLBACK = new DiffUtil.ItemCallback<Games>() {
        @Override
        public boolean areItemsTheSame(@NonNull Games oldItem, @NonNull Games newItem) {
            return oldItem.url.equals(newItem.url);
        }

        @Override
        public boolean areContentsTheSame(@NonNull Games oldItem, @NonNull Games newItem) {
            return oldItem.team1Name.equals(newItem.team1Name) && oldItem.team2Name.equals(newItem.team2Name);
        }
    };
    private LifecycleOwner lifecycleOwner;
    private boolean isPremium;

    public GamesPagedListAdapter(LifecycleOwner lifecycleOwner,boolean isPremium) {
        super(DIFF_CALLBACK);
        this.isPremium = isPremium;
        this.lifecycleOwner = lifecycleOwner;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_games, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        Games games = getItem(position);
        if (games != null)
            viewHolder.bindTo(games);
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
        private State status;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            root = itemView.findViewById(R.id.item_list_games_root);
            tv_item_list_games_team1_name = itemView.findViewById(R.id.tv_item_list_games_team1_name);
            tv_item_list_games_team2_name = itemView.findViewById(R.id.tv_item_list_games_team2_name);
            tv_item_list_games_middle = itemView.findViewById(R.id.tv_item_list_games_middle);
            iv_item_list_games_team1_logo = itemView.findViewById(R.id.iv_item_list_games_team1_logo);
            iv_item_list_games_team2_logo = itemView.findViewById(R.id.iv_item_list_games_team2_logo);
            b_tv_item_list_games = itemView.findViewById(R.id.b_tv_item_list_games);
        }

        private void bindTo(final Games games) {
            hideButtonScheduler();
            tv_item_list_games_team1_name.setText(games.team1Name);
            tv_item_list_games_team2_name.setText(games.team2Name);
            Picasso.get().load(games.team1Logo).placeholder(R.drawable.games_unknown_team1).into(iv_item_list_games_team1_logo);
            Picasso.get().load(games.team2Logo).placeholder(R.drawable.games_unknown_team2).into(iv_item_list_games_team2_logo);
            if (!games.teamScore.equals("")) tv_item_list_games_middle.setText(games.teamScore);
            if (games.teamTime != 0) {
                tv_item_list_games_middle.setText(new SimpleDateFormat("HH:mm dd.MM.yyyy", Locale.US).format(games.teamTime));
            }
            if (games.teamTimeRemains == 0) {
                root.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(view.getContext(), GamesPersonalActivity.class);
                        intent.putExtra(URL, games.url);
                        Pair<View, String> p1 = Pair.create((View) tv_item_list_games_team1_name, "team1_name");
                        Pair<View, String> p2 = Pair.create((View) tv_item_list_games_team2_name, "team2_name");
                        Pair<View, String> p3 = Pair.create((View) tv_item_list_games_middle, "middle");
                        Pair<View, String> p4 = Pair.create((View) iv_item_list_games_team1_logo, "team1_logo");
                        Pair<View, String> p5 = Pair.create((View) iv_item_list_games_team2_logo, "team2_logo");
                        ActivityOptionsCompat optionsCompat = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) view.getContext(), p1, p2, p3, p4, p5);
                        view.getContext().startActivity(intent, optionsCompat.toBundle());
                    }
                });
            } else {
                WorkManager.getInstance().getStatusesByTag(games.url).observe(lifecycleOwner, new Observer<List<WorkStatus>>() {
                    @Override
                    public void onChanged(@Nullable List<WorkStatus> workStatuses) {
                        if (workStatuses != null && workStatuses.size() == 0) {
                            status = null;
                            b_tv_item_list_games.setText(R.string.games_schedule);
                            b_tv_item_list_games.setTextColor(itemView.getResources().getColor(R.color.textColorSecondary));
                        }
                        if (workStatuses != null && workStatuses.size() != 0) {
                            status = workStatuses.get(0).getState();
                            if (workStatuses.get(0).getState().equals(State.ENQUEUED)) {
                                b_tv_item_list_games.setText(android.R.string.cancel);
                                b_tv_item_list_games.setTextColor(itemView.getResources().getColor(R.color.colorAccent));
                            }
                            if (workStatuses.get(0).getState().equals(State.SUCCEEDED) || workStatuses.get(0).getState().equals(State.CANCELLED)) {
                                status = null;
                                b_tv_item_list_games.setText(R.string.games_schedule);
                                b_tv_item_list_games.setTextColor(itemView.getResources().getColor(R.color.textColorSecondary));
                                WorkManager.getInstance().cancelAllWorkByTag(games.url);
                                WorkManager.getInstance().pruneWork();//очищает все завершённые работы
                            }
                        }
                    }
                });
                b_tv_item_list_games.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (status == null) {
                            Data data = new Data.Builder().putString("message", games.team1Name + " vs " + games.team2Name).build();
                            OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest
                                    .Builder(ScheduleGameWorker.class)
                                    .setInitialDelay(games.teamTimeRemains, TimeUnit.SECONDS)
                                    .setInputData(data)
                                    .addTag(games.url)
                                    .build();
                            WorkManager.getInstance().enqueue(oneTimeWorkRequest);
                            hideButtonScheduler();
                        } else {
                            WorkManager.getInstance().cancelAllWorkByTag(games.url);
                            WorkManager.getInstance().pruneWork();
                            hideButtonScheduler();
                        }
                    }
                });
                if(isPremium)
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
            tv_item_list_games_middle.animate().translationY(-tv_item_list_games_middle.getHeight()).setDuration(ANIMATION_DURATION).start();
            b_tv_item_list_games.animate().alpha(1.0f).setDuration(ANIMATION_DURATION).start();
            b_tv_item_list_games.setClickable(true);
            collapse = !collapse;
        }

        private void hideButtonScheduler() {
            tv_item_list_games_middle.animate().translationY(0).setDuration(ANIMATION_DURATION).start();
            b_tv_item_list_games.animate().alpha(0.0f).setDuration(ANIMATION_DURATION).start();
            b_tv_item_list_games.setClickable(false);
            collapse = !collapse;
        }
    }


}
