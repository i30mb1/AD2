package n7.ad2.heroes.full;

import android.arch.paging.PagedListAdapter;
import android.databinding.DataBindingUtil;
import android.media.MediaPlayer;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;

import n7.ad2.R;
import n7.ad2.databinding.DialogResponseBinding;
import n7.ad2.utils.StickyHeaderDecorator;
import n7.ad2.utils.Utils;

public class ResponsesPagedListAdapter extends PagedListAdapter<ResponseModel, ResponsesPagedListAdapter.ViewHolder> implements StickyHeaderDecorator.StickyHeaderInterface {

    private static final DiffUtil.ItemCallback<ResponseModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ResponseModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ResponseModel oldItem, @NonNull ResponseModel newItem) {
            return true;
        }

        @Override
        public boolean areContentsTheSame(@NonNull ResponseModel oldItem, @NonNull ResponseModel newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }
    };

    private MediaPlayer mediaPlayer = new MediaPlayer();
    private LinkedList<String> listSavedResponses;
    private View root;
    private int viewRunningPosition = -1;
    private HeroFulViewModel viewModel;
    private LayoutInflater inflater;

    ResponsesPagedListAdapter(View root, HeroFulViewModel viewModel) {
        super(DIFF_CALLBACK);
        this.viewModel = viewModel;
        this.listSavedResponses = viewModel.responsesInMemory;
        this.root = root;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (inflater == null) inflater = LayoutInflater.from(viewGroup.getContext());
        if (viewType == 0) {
            View view = inflater.inflate(R.layout.item_response, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = inflater.inflate(R.layout.item_response_header, viewGroup, false);
            return new ViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        ResponseModel model = getItem(position);
        if (model == null) {
            viewHolder.clear();
        } else {
            viewHolder.bindTo(model, position);
        }
    }

    @Override
    public int getItemViewType(int position) {
        ResponseModel model = getItem(position);
        if (model != null) {
            if (model.getHref().equals("")) {
                return 1;
            } else {
                return 0;
            }
        }
        return 0;
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
        ResponseModel model = getItem(headerPosition);
        if (model != null) {
            ((TextView) header.findViewById(R.id.tv_item_response)).setText(model.getTitle());
        }
    }

    @Override
    public boolean isHeader(int itemPosition) {
        ResponseModel model = getItem(itemPosition);
        if (model != null) {
            return itemPosition > 0 && model.getHref().equals("");
        }
        return false;
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_response;
        LinearLayout ll_item_response;
        ImageView iv_item_response;
        ProgressBar pb_item_response;
        View rootView;


        ViewHolder(@NonNull View itemView) {
            super(itemView);
            rootView = itemView.getRootView();
            tv_item_response = itemView.findViewById(R.id.tv_item_response);
            ll_item_response = itemView.findViewById(R.id.ll_item_response_heroes);
            iv_item_response = itemView.findViewById(R.id.iv_item_response_saved);
            pb_item_response = itemView.findViewById(R.id.pb_item_response);
        }

        private void bindTo(final ResponseModel model, final int position) {

            if (iv_item_response != null) {
                iv_item_response.setVisibility((listSavedResponses.contains(model.getTitle().replace("?", "") + ".mp3")) ? View.VISIBLE : View.GONE);
            }

            tv_item_response.setText(model.getTitle());

            if (pb_item_response != null) {
                pb_item_response.setVisibility((viewRunningPosition == position) ? View.VISIBLE : View.INVISIBLE);
            }

            if (ll_item_response != null && !model.getIcons().equals("")) {
                ll_item_response.removeAllViewsInLayout();
                int counter = 0;
                LinearLayout linearLayout = new LinearLayout(ll_item_response.getContext());
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);

                String[] icons = model.getIcons().split("\\+");
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
                                            .into(imageView);
                                }
                            });
                    if (counter % 4 == 0) {
                        linearLayout = new LinearLayout(ll_item_response.getContext());
                        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
                        ll_item_response.addView(linearLayout);
                    }
                    linearLayout.addView(imageView);
                    counter++;
                }
            } else if (ll_item_response != null) {
                ll_item_response.removeAllViews();
            }

            if (rootView != null) {
                rootView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View view) {
                        try {
                            notifyItemChanged(viewRunningPosition);
                            viewRunningPosition = position;
                            notifyItemChanged(position);
                            mediaPlayer.release();
                            mediaPlayer = new MediaPlayer();
                            File file = new File(view.getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + viewModel.heroName + File.separator + model.getTitle() + ".mp3");
                            if (file.exists()) mediaPlayer.setDataSource(file.getPath());
                            else
                                mediaPlayer.setDataSource(model.getHref());
                            mediaPlayer.prepareAsync();

                            mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                                @Override
                                public void onPrepared(MediaPlayer mediaPlayer) {
                                    mediaPlayer.start();
                                }
                            });
                            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                                @Override
                                public void onCompletion(MediaPlayer mediaPlayer) {
                                    removeProgressBar();
                                }
                            });
                            mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                                @Override
                                public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                                    removeProgressBar();
                                    showErrorSnackbar(view);
                                    return true;
                                }
                            });

                        } catch (IOException e) {
                            removeProgressBar();
                            showErrorSnackbar(view);
                        }
                    }

                    private void removeProgressBar() {
                        mediaPlayer.release();
                        viewRunningPosition = -1;
                        notifyItemChanged(position);
                    }

                    private void showErrorSnackbar(View view) {
                        if (Utils.isNetworkAvailable(view.getContext())) {
                            Snackbar.make(root, R.string.all_something_went_wrong, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(root, R.string.all_error_internet, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                if (viewModel.userSubscription()) {
                    rootView.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(final View view) {

                            AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

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
                    });
                }
            }
        }

        private void clear() {
            tv_item_response.setText("...");
        }
    }
}
