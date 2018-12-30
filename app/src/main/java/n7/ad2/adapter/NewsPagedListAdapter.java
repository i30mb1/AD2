package n7.ad2.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
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
import n7.ad2.activity.NewsActivity;
import n7.ad2.db.news.SteamNews;

import static n7.ad2.activity.NewsActivity.HREF;
import static n7.ad2.worker.SteamDbNewsWorker.PAGE;

public class NewsPagedListAdapter extends PagedListAdapter<SteamNews, NewsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<SteamNews> DIFF_CALLBACK = new DiffUtil.ItemCallback<SteamNews>() {
        @Override
        public boolean areItemsTheSame(@NonNull SteamNews oldItem, @NonNull SteamNews newItem) {
            return oldItem.href.equals(newItem.href);
        }

        @Override
        public boolean areContentsTheSame(@NonNull SteamNews oldItem, @NonNull SteamNews newItem) {
            return oldItem.title.equals(newItem.title);
        }
    };
    private Context context;
    private boolean withImage;

    public NewsPagedListAdapter(Context context,boolean withImage) {
        super(DIFF_CALLBACK);
        this.context = context;
        this.withImage = withImage;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list_news, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        SteamNews steamNews = getItem(position);
        if (steamNews != null)
            holder.bindTo(steamNews);
        else
            holder.clear();

    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        CardView cardView;
        TextView title;
        ImageView iv_item_list_news_image;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            iv_item_list_news_image = itemView.findViewById(R.id.iv_item_list_news_image);
            cardView = itemView.findViewById(R.id.cv_item_list_news);
            title = itemView.findViewById(R.id.tv_item_list_news_title);
        }

        private void bindTo(final SteamNews steamNews) {
            title.setText(steamNews.title);
            if (withImage) {
                Picasso.get().load(steamNews.imageHref).into(iv_item_list_news_image);
                title.setTextColor(context.getResources().getColor(R.color.textColorPrimary));
            } else {
                title.setBackground(null);
            }
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context.getApplicationContext(), NewsActivity.class);
                    intent.putExtra(HREF, steamNews.href);
                    context.startActivity(intent);
                }
            });
        }

        private void clear() {
            title.setText("Loading...");
        }
    }


}
