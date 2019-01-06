package n7.ad2.heroes;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import java.util.List;

import n7.ad2.R;
import n7.ad2.databinding.ItemListHeroBinding;
import n7.ad2.heroes.db.HeroModel;

public class HeroesPagedListAdapter extends PagedListAdapter<HeroModel, HeroesPagedListAdapter.ViewHolder>   {

    private static final DiffUtil.ItemCallback<HeroModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<HeroModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return true;
        }
    };
    private LayoutInflater inflater;

    HeroesPagedListAdapter() {
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

        ViewHolder(ItemListHeroBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(final HeroModel hero) {
            binding.setHero(hero);
            binding.executePendingBindings();
        }

        private void clear() {
            binding.ivItemListHero.setImageResource(R.drawable.hero_placeholder);
            binding.tvItemListHero.setText("");
        }
    }
}
