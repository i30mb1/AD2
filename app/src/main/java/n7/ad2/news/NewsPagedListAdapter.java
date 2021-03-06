package n7.ad2.news;

import androidx.paging.PagedListAdapter;
import androidx.databinding.DataBindingUtil;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import n7.ad2.R;
import n7.ad2.databinding.ItemListNewsBinding;
import n7.ad2.news.db.NewsModel;

public class NewsPagedListAdapter extends PagedListAdapter<NewsModel, NewsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<NewsModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<NewsModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull NewsModel oldItem, @NonNull NewsModel newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull NewsModel oldItem, @NonNull NewsModel newItem) {
            return true;
        }
    };
    private boolean withImage;
    private LayoutInflater inflater;

    public NewsPagedListAdapter(boolean withImage) {
        super(DIFF_CALLBACK);
        this.withImage = withImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(parent.getContext());
        ItemListNewsBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_list_news, parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        NewsModel news = getItem(position);
        if (news != null) {
            news.setWithImage(withImage);
            holder.bindTo(news);
        } else {
            holder.clear();
        }

    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ItemListNewsBinding binding;

        ViewHolder(ItemListNewsBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        private void bindTo(NewsModel news) {
            binding.setNews(news);
            binding.executePendingBindings();
        }

        private void clear() {
            binding.tvItemListNews.setText("");
        }
    }


}
