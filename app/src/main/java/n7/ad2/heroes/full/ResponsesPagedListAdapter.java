package n7.ad2.heroes.full;

import android.arch.paging.PagedListAdapter;
import android.content.Context;
import android.databinding.DataBindingUtil;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import n7.ad2.R;
import n7.ad2.databinding.DialogResponseBinding;
import n7.ad2.databinding.ItemResponseBinding;
import n7.ad2.databinding.ItemResponseHeaderBinding;
import n7.ad2.utils.StickyHeaderDecorator;

public class ResponsesPagedListAdapter extends PagedListAdapter<Response, RecyclerView.ViewHolder> implements StickyHeaderDecorator.StickyHeaderInterface {

    private static final DiffUtil.ItemCallback<Response> DIFF_CALLBACK = new DiffUtil.ItemCallback<Response>() {
        @Override
        public boolean areItemsTheSame(@NonNull Response oldItem, @NonNull Response newItem) {
            return oldItem.getType() == newItem.getType();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Response oldItem, @NonNull Response newItem) {
            if (oldItem.getType() == newItem.getType()) {
                switch (oldItem.getType()) {
                    case Response.TYPE_HEADER:
                        return ((HeaderModel) oldItem).getTitle().equals(((HeaderModel) newItem).getTitle());
                    case Response.TYPE_RESPONSE:
                        return ((ResponseModel) oldItem).getTitle().equals(((ResponseModel) newItem).getTitle());
                    default:
                        return false;
                }
            } else {
                return false;
            }
        }
    };

    private HeroFulViewModel viewModel;
    private LayoutInflater inflater;

    ResponsesPagedListAdapter(HeroFulViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(viewGroup.getContext());
        switch (viewType) {
            default:
            case Response.TYPE_HEADER:
                ItemResponseHeaderBinding itemResponseHeaderBinding = DataBindingUtil.inflate(inflater, R.layout.item_response_header, viewGroup, false);
                return new HeaderViewHolder(itemResponseHeaderBinding);
            case Response.TYPE_RESPONSE:
                ItemResponseBinding itemResponseBinding = DataBindingUtil.inflate(inflater, R.layout.item_response, viewGroup, false);
                return new ResponseViewHolder(itemResponseBinding);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {
        switch (getItemViewType(position)) {
            default:
            case Response.TYPE_HEADER:
                ((HeaderViewHolder) viewHolder).bindTo(((HeaderModel) getItem(position)));
                break;
            case Response.TYPE_RESPONSE:
                ((ResponseViewHolder) viewHolder).bindTo(((ResponseModel) getItem(position)));
                break;
        }
    }

    @Override
    public int getItemViewType(int position) {
        Response model = getItem(position);
        if (model != null) {
            return model.getType();
        } else {
            return Response.TYPE_RESPONSE;
        }
    }

    @Override
    public int getHeaderPositionForItem(int itemPosition) {
        int headerPosition = 0;
        do {
            if (this.isHeader(itemPosition)) {
                headerPosition = itemPosition;
                break;
            }
            itemPosition -= 1;
        } while (itemPosition >= 0);
        return headerPosition;
    }

    @Override
    public int getHeaderLayout(int headerPosition) {
        return R.layout.item_response_header;
    }

    @Override
    public void bindHeaderData(View header, int headerPosition) {
        Response item = getItem(headerPosition);
        if (item != null && item.getType() == Response.TYPE_RESPONSE) {
            return;
        }
        HeaderModel model = (HeaderModel) getItem(headerPosition);
        if (model != null) {
            ((TextView) header.findViewById(R.id.tv_item_response)).setText(model.getTitle());
        }
    }

    @Override
    public boolean isHeader(int itemPosition) {
        if (itemPosition < 0) return false;
        Response model = getItem(itemPosition);
        if (model != null) {
            return model.getType() == Response.TYPE_HEADER;
        }
        return false;
    }

    public boolean showDialog(Context context, ResponseModel model) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        DialogResponseBinding binding = DataBindingUtil.inflate(inflater, R.layout.dialog_response, null, false);
        binding.setModel(model);
        binding.setViewModel(viewModel);
        builder.setView(binding.getRoot());

        AlertDialog dialog = builder.create();
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogTheme;
        binding.setDialog(dialog);
        dialog.show();
        return true;
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {
        ItemResponseHeaderBinding binding;

        HeaderViewHolder(@NonNull ItemResponseHeaderBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(HeaderModel model) {
            binding.setModel(model);
            binding.executePendingBindings();
        }

        void clear() {

        }
    }

    class ResponseViewHolder extends RecyclerView.ViewHolder {
        ItemResponseBinding binding;

        ResponseViewHolder(@NonNull ItemResponseBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        void bindTo(ResponseModel model) {
            binding.setModel(model);
            binding.setViewModel(viewModel);
            binding.setAdapter(ResponsesPagedListAdapter.this);
            binding.executePendingBindings();

            if (viewModel.responsesInMemory.contains(model.getTitleForFolder())) {
                model.inStore.set(true);
            } else {
                model.inStore.set(false);
            }

            inflateIconsForResponse(model);
        }

        void inflateIconsForResponse(ResponseModel model) {
            binding.llItemResponseHeroes.removeAllViews();
            String[] icons = model.getIcons().split("\\+");
            if (icons.length == 1) return;

            int counter = 0;
            LinearLayout linearLayout = new LinearLayout(binding.llItemResponseHeroes.getContext());
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);

            for (final String icon : icons) {
                final ImageView imageView = (ImageView) inflater.inflate(R.layout.item_response_icon, linearLayout, false);
//                    imageView.getLayoutParams().height = 20;
                Picasso.get().load("file:///android_asset/heroes/" + icon.replace("%27", "'") + "/mini.webp")
                        .into(imageView, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load("file:///android_asset/items/" + icon + "/full.webp")
                                        .into(imageView, new Callback() {
                                            @Override
                                            public void onSuccess() {

                                            }

                                            @Override
                                            public void onError(Exception e) {

                                            }
                                        });
                            }
                        });
                if (counter % 4 == 0) {
                    linearLayout = new LinearLayout(binding.llItemResponseHeroes.getContext());
                    linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                    binding.llItemResponseHeroes.addView(linearLayout);
                }
                linearLayout.addView(imageView);
                counter++;
            }
        }

        void clear() {

        }
    }
}
