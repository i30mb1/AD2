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
import n7.ad2.activity.ItemFullActivity;
import n7.ad2.db.items.ItemModel;

import static n7.ad2.activity.ItemFullActivity.ITEM_FOLDER;
import static n7.ad2.activity.ItemFullActivity.ITEM_NAME;

public class ItemsPagedListAdapter extends PagedListAdapter<ItemModel, ItemsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<ItemModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ItemModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ItemModel items, @NonNull ItemModel t1) {
            return items.getId() == t1.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull ItemModel items, @NonNull ItemModel t1) {
            return items.getCodeName().equals(t1.getCodeName());
        }
    };

    public ItemsPagedListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_item, viewGroup, false);
        return new ViewHolder(view);
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
        private ImageView imageView;
        private TextView textView;
        private CardView cardView;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.iv_item_list_item);
            textView = itemView.findViewById(R.id.tv_item_list_item);
            cardView = itemView.findViewById(R.id.cv_item_list_item);
        }

        private void bindTo(final ItemModel items) {
            textView.setText(items.getName().replace("%27", "'"));
            Picasso.get()
                    .load("file:///android_asset/items/" + items.getCodeName() + "/full.webp")
                    .error(R.drawable.item_placeholder_error)
                    .placeholder(R.drawable.item_placeholder)
                    .into(imageView);

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(itemView.getContext(), ItemFullActivity.class);
                    ActivityOptionsCompat options = ActivityOptionsCompat.makeSceneTransitionAnimation((Activity) itemView.getContext(), imageView, "iv");
                    intent.putExtra(ITEM_FOLDER, items.getCodeName());
                    intent.putExtra(ITEM_NAME, items.getName().replace("%27", "'"));
                    imageView.getContext().startActivity(intent, options.toBundle());
                }
            });
        }

        private void clear() {
            textView.setText("");
            imageView.setImageResource(R.drawable.item_placeholder);
        }
    }


}
