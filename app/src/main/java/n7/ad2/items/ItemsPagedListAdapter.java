package n7.ad2.items;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.ItemListItemBinding;
import n7.ad2.items.db.ItemModel;

public class ItemsPagedListAdapter extends PagedListAdapter<ItemModel, ItemsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<ItemModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ItemModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemModel items, @NonNull ItemModel t1) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemModel items, @NonNull ItemModel t1) {
            return items.getId() == t1.getId();
        }
    };

    private LayoutInflater inflater;

    ItemsPagedListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (inflater == null) inflater = LayoutInflater.from(viewGroup.getContext());
        ItemListItemBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_list_item, viewGroup, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        ItemModel item = getItem(i);
        if (item != null) {
            viewHolder.bindTo(item);
        } else {
            viewHolder.clear();
        }
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemListItemBinding binding;

        ViewHolder(ItemListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(ItemModel item) {
            binding.setItem(item);
        }

        private void clear() {
          binding.iv.setImageResource(R.drawable.item_placeholder);
          binding.tv.setText("");
        }
    }


}
