package n7.ad2.adapter;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import n7.ad2.R;
import n7.ad2.activity.HeroFullActivity;
import n7.ad2.db.heroes.HeroModel;

import static n7.ad2.activity.HeroFullActivity.HERO_FOLDER;
import static n7.ad2.activity.HeroFullActivity.HERO_NAME;

public class HeroesPagedListAdapter extends PagedListAdapter<HeroModel, HeroesPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<HeroModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<HeroModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return oldItem.getCodeName().equals(newItem.getCodeName());
        }
    };

    public HeroesPagedListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_hero, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        HeroModel hero = getItem(position);
        if (hero != null)
            holder.bindTo(hero);
        else
            holder.clear();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        private ImageView imageView;
        private TextView textView;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_image);
            textView = itemView.findViewById(R.id.tv_item_name);
            cardView = itemView.findViewById(R.id.cv_item_list_hero);
        }

        private void bindTo(final HeroModel hero) {
            textView.setText(hero.getName());
            Picasso.get()
                    .load("file:///android_asset/heroes/" + hero.getCodeName() + "/full.webp")
                    .error(R.drawable.hero_placeholder_error)
                    .placeholder(R.drawable.hero_placeholder)
                    .into(imageView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), HeroFullActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) itemView.getContext(), imageView, "iv");
                    intent.putExtra(HERO_NAME, hero.getName());
                    intent.putExtra(HERO_FOLDER, hero.getCodeName());
                    imageView.getContext().startActivity(intent, options.toBundle());
                }
            });
        }

        private void clear() {
            textView.setText("");
            imageView.setImageResource(R.drawable.hero_placeholder);
        }
    }
}
