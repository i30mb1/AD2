package n7.ad2.adapter;

import android.arch.paging.PagedListAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.robinhood.ticker.TickerUtils;
import com.robinhood.ticker.TickerView;
import com.squareup.picasso.Picasso;

import java.util.Random;

import n7.ad2.R;
import n7.ad2.streams.TwitchGameActivity;
import n7.ad2.retrofit.streams.Streams;

import static n7.ad2.streams.TwitchGameActivity.CHANNEL_NAME;
import static n7.ad2.streams.TwitchGameActivity.CHANNEL_TITLE;

public class StreamsPagedListAdapter extends PagedListAdapter<Streams, StreamsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Streams> DIFF_CALLBACK = new DiffUtil.ItemCallback<Streams>() {
        @Override
        public boolean areItemsTheSame(@NonNull Streams streams, @NonNull Streams t1) {
            return streams.getChannel().getDisplay_name().equals(t1.getChannel().getDisplay_name());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Streams streams, @NonNull Streams t1) {
            return false;
        }
    };
    private final int[] view = {-2, -1, 0, 1, 2, 3};
    private final long[] duration = {2000, 3000, 4000, 3500, 2500};

    public StreamsPagedListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_stream, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        Streams streams = getItem(i);
        if (streams != null) {
            viewHolder.bindTo(streams);
        } else {
            viewHolder.clear();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        CardView cv_item_list_stream_root;
        ImageView iv_item_list_stream_image;
        TextView tv_item_list_stream_channel_name;
        TextView tv_item_list_stream_channel_description;
        TickerView tv_item_list_stream_viewers;
        Handler handler;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
//            cv_item_list_stream_root = itemView.findViewById(R.id.cv_item_list_stream_root);
            tv_item_list_stream_viewers = itemView.findViewById(R.id.tv_item_list_stream_viewers);
            tv_item_list_stream_viewers.setCharacterList(TickerUtils.getDefaultNumberList());
//            iv_item_list_stream_image = itemView.findViewById(R.id.iv_item_list_stream_image);
            tv_item_list_stream_channel_name = itemView.findViewById(R.id.tv_item_list_stream_channel_name);
            tv_item_list_stream_channel_description = itemView.findViewById(R.id.tv_item_list_stream_channel_description);
            handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int value = Integer.valueOf(tv_item_list_stream_viewers.getText());
                        int randomValue = +view[new Random().nextInt(view.length - 1)];
                        if (value + randomValue >= 0)
                            tv_item_list_stream_viewers.setText(value + randomValue + "");
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                    handler.postDelayed(this, duration[new Random().nextInt(duration.length - 1)]);
                }
            });

        }

        private void bindTo(final Streams streams) {
            Picasso.get().load(streams.getPreview().getMedium()).placeholder(R.drawable.streams_placeholder).into(iv_item_list_stream_image);
            tv_item_list_stream_channel_name.setText(streams.getChannel().getDisplay_name());
            tv_item_list_stream_channel_description.setText(streams.getChannel().getStatus());
            tv_item_list_stream_viewers.setText("" + streams.getViewers());
            cv_item_list_stream_root.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(v.getContext(), TwitchGameActivity.class);
                    intent.putExtra(CHANNEL_NAME, streams.getChannel().getDisplay_name().toLowerCase());
                    intent.putExtra(CHANNEL_TITLE, streams.getChannel().getStatus());
                    v.getContext().startActivity(intent);
                }
            });
        }

        private void clear() {
            iv_item_list_stream_image.setImageResource(R.drawable.streams_placeholder);
            tv_item_list_stream_channel_name.setText("...");
            tv_item_list_stream_channel_description.setText("...");
            tv_item_list_stream_viewers.setText("0");
        }
    }


}
