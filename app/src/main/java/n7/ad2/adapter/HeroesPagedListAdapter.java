package n7.ad2.adapter;

import android.app.Activity;
import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.activity.HeroFullActivity;
import n7.ad2.databinding.ItemListHeroBinding;
import n7.ad2.db.heroes.HeroModel;

import static n7.ad2.activity.HeroFullActivity.HERO_CODE_NAME;
import static n7.ad2.activity.HeroFullActivity.HERO_NAME;

public class HeroesPagedListAdapter extends PagedListAdapter<HeroModel, HeroesPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<HeroModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<HeroModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return oldItem.getId() == newItem.getId();
        }
    };
    private LayoutInflater inflater;

    public HeroesPagedListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        ItemListHeroBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_list_hero, parent, false);
        return new ViewHolder(binding);
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

        ItemListHeroBinding binding;

        public ViewHolder(ItemListHeroBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(final HeroModel hero) {
            binding.setHero(hero);
        }

        private void clear() {

        }
    }
}
