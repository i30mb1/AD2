package n7.ad2.heroes.full;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.arch.paging.PagedListAdapter;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.util.List;

import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.MySharedPreferences;
import n7.ad2.R;
import n7.ad2.setting.SettingActivity;
import n7.ad2.utils.StickyHeaderDecorator;
import n7.ad2.utils.Utils;

import static n7.ad2.setting.SettingActivity.SUBSCRIPTION;

public class ResponsesPagedListAdapter extends PagedListAdapter<ResponseModel, ResponsesPagedListAdapter.ViewHolder> implements StickyHeaderDecorator.StickyHeaderInterface {

    private static final DiffUtil.ItemCallback<ResponseModel> DIFF_CALLBACK = new DiffUtil.ItemCallback<ResponseModel>() {
        @Override
        public boolean areItemsTheSame(@NonNull ResponseModel oldItem, @NonNull ResponseModel newItem) {
            return oldItem.getTitle().equals(newItem.getTitle());
        }

        @Override
        public boolean areContentsTheSame(@NonNull ResponseModel oldItem, @NonNull ResponseModel newItem) {
            return oldItem.getHref().equals(newItem.getHref()) & oldItem.isInStore() == newItem.isInStore();
        }
    };
    MediaPlayer mediaPlayer = new MediaPlayer();
    private String heroName;
    private AppExecutors appExecutors;
    private Context context;
    private List<String> listSavedResponses;
    private View parentView;
    private int viewRunningPosition = -1;

    public ResponsesPagedListAdapter(String heroName, AppExecutors appExecutors, Context context, List list, View parentView) {
        super(DIFF_CALLBACK);
        this.heroName = heroName;
        this.appExecutors = appExecutors;
        this.context = context;
        this.listSavedResponses = list;
        this.parentView = parentView;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_response, viewGroup, false);
            return new ViewHolder(view);
        } else {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_response_header, viewGroup, false);
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
        if (getItem(position).getHref().equals("")) {
            return 1;
        } else {
            return 0;
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
        ((TextView) header.findViewById(R.id.tv_item_response)).setText(getItem(headerPosition).getTitle());
    }

    @Override
    public boolean isHeader(int itemPosition) {
        if (itemPosition > 0 && getItem(itemPosition).getHref().equals("")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean enableWriteSetting(final Context context, final View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (Settings.System.canWrite(context)) {
                return true;
            } else {
                Snackbar.make(view, R.string.all_grand_permission, Snackbar.LENGTH_INDEFINITE).setAction(R.string.all_enable,
                        new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                Intent intent = new Intent(Settings.ACTION_MANAGE_WRITE_SETTINGS);
                                intent.setData(Uri.parse("package:" + context.getPackageName()));
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                context.startActivity(intent);
                            }
                        }).show();
                return false;
            }
        }
        return true;
    }

    private boolean checkPermission(View view) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (context.checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                return true;
            } else {
                Snackbar.make(view, R.string.all_grand_permission, Snackbar.LENGTH_INDEFINITE).setAction(R.string.all_enable, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 7);
                    }
                }).show();
                return false;
            }
        }
        return true;
    }

    public int dpSize(int px) {
        float scale = parentView.getContext().getResources().getDisplayMetrics().density;
        return (int) (px * scale + 0.5f);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_item_response;
        LinearLayout ll_item_response;
        ImageView iv_item_response_icons;
        ImageView iv_item_response;
        ProgressBar pb_item_response;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tv_item_response = itemView.findViewById(R.id.tv_item_response);
            ll_item_response = itemView.findViewById(R.id.ll_item_response);
            iv_item_response_icons = itemView.findViewById(R.id.iv_item_response_icons);
            iv_item_response = itemView.findViewById(R.id.iv_item_response);
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

            if (iv_item_response_icons != null && model.getIcons().equals("")) {
                iv_item_response_icons.setVisibility(View.GONE);
            }

            if (iv_item_response_icons != null && !model.getIcons().equals("")) {
                iv_item_response_icons.setVisibility(View.VISIBLE);
                final String icon = model.getIcons().split("\\+")[0];
//               Drawable drawable = Utils.getDrawableFromAssets(iv_item_response_icons.getContext(), String.format("heroes/%s/%s", icon, "mini.webp"));
//                if (drawable == null) {
//                    drawable = Utils.getDrawableFromAssets(iv_item_response_icons.getContext(), String.format("items/%s/%s", icon, "full.webp"));
//                }
//                iv_item_response_icons.setImageDrawable(drawable);
                Picasso.get().load("file:///android_asset/heroes/" + icon + "/mini.webp")
                        .into(iv_item_response_icons, new Callback() {
                            @Override
                            public void onSuccess() {

                            }

                            @Override
                            public void onError(Exception e) {
                                Picasso.get().load("file:///android_asset/items/" + icon + "/full.webp")
                                        .into(iv_item_response_icons);
                            }
                        });
            }

            if (ll_item_response != null) {
                ll_item_response.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try {
                            notifyItemChanged(viewRunningPosition);
                            viewRunningPosition = position;
                            notifyItemChanged(position);
                            mediaPlayer.release();
                            mediaPlayer = new MediaPlayer();
                            File file = new File(view.getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + heroName + File.separator + model.getTitle() + ".mp3");
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
                                    showErrorSnackbar();
                                    return true;
                                }
                            });

                        } catch (IOException e) {
                            removeProgressBar();
                            showErrorSnackbar();
                        }
                    }

                    private void removeProgressBar() {
                        mediaPlayer.release();
                        viewRunningPosition = -1;
                        notifyItemChanged(position);
                    }

                    private void showErrorSnackbar() {
                        if (Utils.isNetworkAvailable(context)) {
                            Snackbar.make(parentView, R.string.all_something_went_wrong, Snackbar.LENGTH_SHORT).show();
                        } else {
                            Snackbar.make(parentView, R.string.all_error_internet, Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
                ll_item_response.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(final View view) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                        builder.setView(R.layout.dialog_response);
                        final AlertDialog dialog = builder.show();
                        TextView tv_dialog_response_count = dialog.findViewById(R.id.tv_dialog_response_count);
                        Button b_dialog_response_download = dialog.findViewById(R.id.b_dialog_response_download);
                        Button b_dialog_response_set_ringtone = dialog.findViewById(R.id.b_dialog_response_set_ringtone);

                        if (MySharedPreferences.getSharedPreferences(view.getContext()).getBoolean(SUBSCRIPTION, false))
                            tv_dialog_response_count.setVisibility(View.GONE);
                        int count = MySharedPreferences.getSharedPreferences(view.getContext()).getInt(MySharedPreferences.RESPONSE_COUNT_KEY, MySharedPreferences.FREE_COUNT);
                        tv_dialog_response_count.setText(String.valueOf(count));
                        b_dialog_response_set_ringtone.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(final View view) {
                                if (enableWriteSetting(view.getContext(), view) && checkPermission(view)) {
                                    appExecutors.networkIO().execute(new Runnable() {
                                        @Override
                                        public void run() {
                                            File file = new File(view.getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + heroName + File.separator + model.getTitle().replace("?", "") + ".mp3");
                                            if (file.exists()) {
                                                ContentValues values = new ContentValues();
                                                values.put(MediaStore.MediaColumns.DATA, file.getAbsolutePath());
                                                values.put(MediaStore.MediaColumns.TITLE, model.getTitle());
                                                values.put(MediaStore.MediaColumns.MIME_TYPE, "audio/mp3");
                                                values.put(MediaStore.MediaColumns.SIZE, file.length());
                                                values.put(MediaStore.Audio.Media.IS_NOTIFICATION, true);
                                                values.put(MediaStore.Audio.Media.IS_RINGTONE, true);
                                                values.put(MediaStore.Audio.Media.IS_ALARM, true);
                                                values.put(MediaStore.Audio.Media.IS_MUSIC, false);

                                                ContentResolver contentResolver = context.getContentResolver();
                                                Uri generalaudiouri = MediaStore.Audio.Media.INTERNAL_CONTENT_URI;
                                                contentResolver.delete(generalaudiouri, MediaStore.MediaColumns.DATA + "='" + file.getAbsolutePath() + "'", null);
                                                Uri ringtoneuri = contentResolver.insert(generalaudiouri, values);
                                                RingtoneManager.setActualDefaultRingtoneUri(context, RingtoneManager.TYPE_NOTIFICATION, ringtoneuri);
                                                Snackbar.make(parentView, R.string.hero_response_ringtone_set, Snackbar.LENGTH_SHORT).show();
                                            } else {
                                                Snackbar.make(parentView, R.string.hero_responses_fragment_download_first, Snackbar.LENGTH_SHORT).show();
                                            }
                                            dialog.cancel();
                                        }
                                    });
                                }
                            }
                        });
                        if (count > 0 || MySharedPreferences.getSharedPreferences(view.getContext()).getBoolean(SUBSCRIPTION, false)) {
                            b_dialog_response_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(final View view) {
                                    if (Utils.isNetworkAvailable(view.getContext())) {
                                        appExecutors.networkIO().execute(new Runnable() {
                                            @Override
                                            public void run() {
                                                File file = new File(view.getContext().getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator + heroName + File.separator + model.getTitle() + ".mp3");
                                                if (file.exists()) {
                                                    Snackbar.make(parentView, R.string.hero_responses_sound_already_downloaded, Snackbar.LENGTH_LONG).setAction(R.string.open_file, new View.OnClickListener() {
                                                        @Override
                                                        public void onClick(View view) {
                                                            Uri selectedUri = Uri.parse(context.getExternalFilesDir(Environment.DIRECTORY_RINGTONES) + File.separator);
                                                            Intent intentOpenFile = new Intent(Intent.ACTION_VIEW);
                                                            intentOpenFile.setDataAndType(selectedUri, "application/*");
                                                            if (intentOpenFile.resolveActivityInfo(context.getPackageManager(), 0) != null) {
                                                                context.startActivity(Intent.createChooser(intentOpenFile, context.getString(R.string.hero_responses_open_folder_with)));
                                                            } else {
                                                                // if you reach this place, it means there is no any file
                                                                // explorer app installed on your device
                                                            }
                                                        }
                                                    }).show();
                                                } else {
                                                    DownloadManager manager = (DownloadManager) view.getContext().getSystemService(Context.DOWNLOAD_SERVICE);
                                                    if (manager != null) {
                                                        manager.enqueue(new DownloadManager.Request(Uri.parse(model.getHref()))
                                                                .setDescription(heroName)
                                                                .setTitle(model.getTitle())
                                                                .setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)
                                                                .setDestinationInExternalFilesDir(view.getContext(), Environment.DIRECTORY_RINGTONES, heroName + File.separator + model.getTitle().replace("?", "") + ".mp3")
                                                        );
                                                    }
                                                }
                                                dialog.cancel();
                                            }
                                        });
                                    } else {
                                        dialog.cancel();
                                        Snackbar.make(parentView, R.string.all_error_internet, Snackbar.LENGTH_LONG).show();
                                    }

                                }
                            });
                        } else {
                            b_dialog_response_download.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    dialog.cancel();
                                    Snackbar.make(parentView, R.string.all_come_back_tomorrow, Snackbar.LENGTH_LONG).setAction(R.string.all_buy, new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            view.getContext().startActivity(new Intent(view.getContext(), SettingActivity.class));
                                        }
                                    }).show();
                                }
                            });
                        }
                        return true;
                    }
                });
            }
        }

        private void clear() {
            tv_item_response.setText("...");
        }
    }
}
