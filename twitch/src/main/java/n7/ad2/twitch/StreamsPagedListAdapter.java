package n7.ad2.twitch;

import androidx.paging.PagedListAdapter;
import androidx.databinding.DataBindingUtil;
import android.os.Handler;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import com.robinhood.ticker.TickerUtils;

import java.util.Random;

import n7.ad2.twitch.databinding.ItemListStreamBinding;
import n7.ad2.twitch.retrofit.Streams;

public class StreamsPagedListAdapter extends PagedListAdapter<Streams, StreamsPagedListAdapter.ViewHolder> {

    private static final DiffUtil.ItemCallback<Streams> DIFF_CALLBACK = new DiffUtil.ItemCallback<Streams>() {
        @Override
        public boolean areItemsTheSame(@NonNull Streams streams, @NonNull Streams t1) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull Streams streams, @NonNull Streams t1) {
            return streams.getChannel().getDisplay_name().equals(t1.getChannel().getDisplay_name());
        }
    };
    private final int[] view = {-2, -1, 0, 1, 2, 3};
    private final long[] duration = {2000, 3000, 4000, 3500, 2500};
    private LayoutInflater inflater;

    StreamsPagedListAdapter() {
        super(DIFF_CALLBACK);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        if (inflater == null) inflater = LayoutInflater.from(viewGroup.getContext());

        ItemListStreamBinding binding = DataBindingUtil.inflate(inflater, R.layout.item_list_stream, viewGroup, false);
        return new ViewHolder(binding);
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

    class ViewHolder extends RecyclerView.ViewHolder {
        ItemListStreamBinding binding;
        Handler handler;

        ViewHolder(@NonNull final ItemListStreamBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
            binding.tvItemListStreamViewers.setCharacterList(TickerUtils.getDefaultNumberList());
            handler = new Handler();
            handler.post(new Runnable() {
                @Override
                public void run() {
                    try {
                        int value = Integer.valueOf(binding.tvItemListStreamViewers.getText());
                        int randomValue = +view[new Random().nextInt(view.length - 1)];
                        if (value + randomValue >= 0)
                            binding.tvItemListStreamViewers.setText(String.valueOf(value + randomValue));
                        handler.postDelayed(this, duration[new Random().nextInt(duration.length - 1)]);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
        }

        private void bindTo(final Streams streams) {
            try {
                binding.tvItemListStreamViewers.setText(String.valueOf(streams.getViewers()));
                binding.setStream(streams);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        private void clear() {
//            binding.ivItemListStream.setImageResource(R.drawable.streams_placeholder);
//            binding.tvItemListStreamTitle.setText("");
//            binding.tvItemListStreamSummary.setText("");
        }
    }


}
