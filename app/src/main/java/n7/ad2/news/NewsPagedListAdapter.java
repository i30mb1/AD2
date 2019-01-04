package n7.ad2.news;

import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import n7.ad2.R;
import n7.ad2.activity.NewsActivity;
import n7.ad2.databinding.ItemListNewsBinding;
import n7.ad2.news.db.NewsModel;

import static n7.ad2.activity.NewsActivity.HREF;

public class NewsPagedListAdapter extends PagedListAdapter<NewsModel, NewsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<NewsModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<NewsModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull NewsModel oldItem, @NonNull NewsModel newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull NewsModel oldItem, @NonNull NewsModel newItem) {
            return oldItem.getHref().equals(newItem.getHref());
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
//            if (withImage) {
//                title.setTextColor(context.getResources().getColor(R.color.textColorPrimary));
//            } else {
//                title.setBackground(null);
//            }
        }

        private void clear() {
            binding.tv.setText("");
        }
    }


}
