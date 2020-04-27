package n7.ad2.heroes;

import androidx.paging.PagedListAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.ItemListHeroBinding;
import n7.ad2.heroes.db.HeroModel;

public class HeroesPagedListAdapter extends PagedListAdapter<HeroModel, HeroesPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<HeroModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<HeroModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return oldItem.getCodeName().equals(newItem.getCodeName());
        }

        @Override
        public boolean areContentsTheSame(@NonNull HeroModel oldItem, @NonNull HeroModel newItem) {
            return true;
        }
    };
    private LayoutInflater inflater;
    private HeroesFragment fragment;

    HeroesPagedListAdapter(HeroesFragment fragment) {
        super(DIFF_CALLBACK);
        this.fragment = fragment;
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
        if (hero != null) {
            holder.bindTo(hero);
        } else {
            holder.clear();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemListHeroBinding binding;

        ViewHolder(ItemListHeroBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(final HeroModel hero) {
            binding.setHero(hero);
            binding.setFragment(fragment);
            binding.executePendingBindings();
        }

        private void clear() {
            binding.iv.setImageResource(R.drawable.hero_placeholder);
            binding.tv.setText("");
        }
    }
}
