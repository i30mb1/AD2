package n7.ad2.streams;


import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.arch.paging.PagedListAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.util.DiffUtil;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListenerBaseImpl;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import n7.ad2.AppExecutors;
import n7.ad2.R;
import n7.ad2.streams.retrofit.Streams;
import n7.ad2.utilsTwitch.Element;
import n7.ad2.utilsTwitch.Playlist;
import n7.ad2.utilsTwitch.TappableSurfaceView;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static n7.ad2.splash.SplashActivity.ANIMATION_DURATION;

public class SingleStreamFragment extends Fragment implements SurfaceHolder.Callback {

    private final DiffUtil.ItemCallback<Streams> DIFF_CALLBACK = new DiffUtil.ItemCallback<Streams>() {
        @Override
        public boolean areItemsTheSame(@NonNull Streams streams, @NonNull Streams t1) {
            return streams.getChannel().getDisplay_name().equals(t1.getChannel().getDisplay_name());
        }

        @Override
        public boolean areContentsTheSame(@NonNull Streams streams, @NonNull Streams t1) {
            return false;
        }
    };
    private final ArrayList<String> qualitiesTitles = new ArrayList<>();
    private View view;
    private RecyclerView rv_fragment_single_stream_chat;
    private RecyclerView rv_fragment_single_stream;
    private PlainTextAdapter adapterChat;
    private ProgressBar pb_fragment_single_stream;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private StreamsAdapter streamsAdapter;
    private AppExecutors appExecutors;
    private Element.List qualities;
    private int qualityPosition;
    private Twirk twirk;
    private boolean isHolderEnabled = true;
    private boolean isSoundEnabled = true;
    private boolean isStreamEnabled = true;
    private boolean isChatEnabled = true;
    private LinearLayout ll_fragment_single_stream_holder;
    private ImageView iv_fragment_single_stream_sound;
    private ImageView iv_fragment_single_stream_chat;
    private ImageView iv_fragment_single_stream;
    private TextView tv_fragment_single_alpha;
    private View oldView;
    private TappableSurfaceView sv_fragment_single_stream;
    private float alphaChat = 1.0F;
    private LinearLayoutManager linearLayout;
    private ImageView iv_fragment_single_stream_close;
    private TextView tv_fragment_single_quality;

    public SingleStreamFragment() {
        // Required empty public constructor
    }

    private static String readStream(InputStream in) throws IOException {
        InputStreamReader inputStreamReader = new InputStreamReader(in, "UTF-8");
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        StringBuilder result = new StringBuilder();
        String line = "";
        do {
            result.append(line);
            result.append('\n');
        }
        while ((line = bufferedReader.readLine()) != null);
        return result.toString();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_single_stream, container, false);

        appExecutors = new AppExecutors();

        initViews();
        initAdapterChat();
        initAdapterStreams();
        initSurfaceHolder();

        return view;
    }

    private void startChat(final String channel) {
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                if (twirk != null && twirk.isConnected()) {
                    twirk.close();
                }
            }
        });
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                twirk = new TwirkBuilder("#" + channel, "aboutdota2", "oauth:jfhimbps50vmxgto0aj75n0smbbphz").build();
                try {
                    twirk.connect();
                    twirk.addIrcListener(new TwirkListenerBaseImpl() {
                        @Override
                        public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
                            super.onPrivMsg(sender, message);
                            adapterChat.add(sender.getName() + ": " + message.getContent());
                            if(!linearLayout.isSmoothScrolling())
                            rv_fragment_single_stream_chat.smoothScrollToPosition(adapterChat.getItemCount() - 1);
                        }
                    });
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void startStream(final String channel) {
        pb_fragment_single_stream.setVisibility(View.VISIBLE);
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessToken = "https://api.twitch.tv/api/channels/" + channel + "/access_token?client_id=vmr0piicf3e3nxw4fs0zz2e2vqak8y";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(accessToken).build();
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String token = jsonObject.getString("token");
                    String sig = jsonObject.getString("sig");
                    String url = "http://usher.twitch.tv/api/channel/hls/" + channel + ".m3u8?token=" + URLEncoder.encode(token, "UTF-8") + "&sig=" + sig;
                    String result2 = basicRequestSend(url);
                    Playlist playList = Playlist.parse(result2);
                    qualities = new Element.List(playList.getElements());
                    for (int i = 0; i < qualities.size(); i++) {
                        qualitiesTitles.add(qualities.get(i).getQuality());
                    }
                    qualityPosition = qualities.size() - 1;
                    startStreamWithDefineQuality();
                } catch (Exception e) {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            pb_fragment_single_stream.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
    }

    private void startStreamWithDefineQuality() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            mediaPlayer.setDataSource(qualities.get(qualityPosition).getURI().toString());
            mediaPlayer.prepareAsync();
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
//                    itemMenuQuality.setTitle(qualities.get(qualityPosition).getQuality());
                }
            });

        } catch (Exception e) {
            pb_fragment_single_stream.setVisibility(View.INVISIBLE);
        }
    }

    private String basicRequestSend(String url) throws Exception {
        String result = "";
        HttpURLConnection urlConnection;
        URL urlObj = new URL(url);
        urlConnection = (HttpURLConnection) urlObj.openConnection();
        try {
            result = readStream(urlConnection.getInputStream());
            urlConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    private void initAdapterStreams() {
        rv_fragment_single_stream = view.findViewById(R.id.rv_fragment_single_stream);
        rv_fragment_single_stream.setLayoutManager(new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false));
        streamsAdapter = new StreamsAdapter();
        rv_fragment_single_stream.setAdapter(streamsAdapter);

        StreamsViewModel streamsViewModel = ViewModelProviders.of(this).get(StreamsViewModel.class);
//        streamsViewModel.getStatusLoading().observe(this, new Observer<Boolean>() {
//            @Override
//            public void onChanged(@Nullable Boolean aBoolean) {
//                if (aBoolean != null && aBoolean) {
//                    pb_fragment_single_stream.setVisibility(View.VISIBLE);
//                } else {
//                    pb_fragment_single_stream.setVisibility(View.INVISIBLE);
//                }
//            }
//        });
        streamsViewModel.getStreams().observe(this, new Observer<PagedList<Streams>>() {
            @Override
            public void onChanged(@Nullable PagedList<Streams> streams) {
                streamsAdapter.submitList(streams);
            }
        });
    }

    private void initViews() {
        tv_fragment_single_quality = view.findViewById(R.id.tv_fragment_single_quality);
        tv_fragment_single_quality.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getContext() != null) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                    String[] animals = new String[qualitiesTitles.size()];
                    qualitiesTitles.toArray(animals);
                    int checkedItem = qualityPosition;
                    builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            qualityPosition = i;
                            tv_fragment_single_quality.setText(qualitiesTitles.get(qualityPosition).substring(0,3).toLowerCase());
                            startStreamWithDefineQuality();
                            dialogInterface.dismiss();
                        }
                    });
                    AlertDialog dialog = builder.create();
                    dialog.show();
                }
            }
        });
        iv_fragment_single_stream_close = view.findViewById(R.id.iv_fragment_single_stream_close);
        iv_fragment_single_stream_close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getFragmentManager() != null) {
//                    getFragmentManager().beginTransaction().remove(SingleStreamFragment.this).commit();
                    if(getContext()!=null)
                        getContext().sendBroadcast(new Intent("refresh_ll").putExtra("id",((FrameLayout) view.getParent()).getId()));
                }
            }
        });
        pb_fragment_single_stream = view.findViewById(R.id.pb_fragment_single_stream);
        ll_fragment_single_stream_holder = view.findViewById(R.id.ll_fragment_single_stream_holder);
        tv_fragment_single_alpha = view.findViewById(R.id.tv_fragment_single_alpha);
        tv_fragment_single_alpha.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (alphaChat == 1.0F) {
                    alphaChat = 0.6F;
                }
                else if (alphaChat == 0.6F) {
                    alphaChat = 0.3F;
                }
                else if (alphaChat == 0.3F) {
                    alphaChat = 1.0F;
                }
                tv_fragment_single_alpha.setText(String.valueOf(alphaChat));
                adapterChat.notifyDataSetChanged();
            }
        });
        iv_fragment_single_stream_sound = view.findViewById(R.id.iv_fragment_single_stream_sound);
        iv_fragment_single_stream_sound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSoundEnabled) {
                    iv_fragment_single_stream_sound.setImageResource(R.drawable.ic_fragment_single_stream_sound_off);
                    mediaPlayer.setVolume(0, 0);
                } else {
                    iv_fragment_single_stream_sound.setImageResource(R.drawable.ic_fragment_single_stream_sound_on);
                    mediaPlayer.setVolume(1, 1);
                }
                isSoundEnabled = !isSoundEnabled;
            }
        });
        iv_fragment_single_stream_chat = view.findViewById(R.id.iv_fragment_single_stream_chat);
        iv_fragment_single_stream_chat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isChatEnabled) {
                    iv_fragment_single_stream_chat.setImageResource(R.drawable.ic_fragment_single_stream_chat_off);
                    rv_fragment_single_stream_chat.animate().alpha(0.0F).setDuration(ANIMATION_DURATION).start();
                } else {
                    iv_fragment_single_stream_chat.setImageResource(R.drawable.ic_fragment_single_stream_chat_on);
                    rv_fragment_single_stream_chat.animate().alpha(1F).setDuration(ANIMATION_DURATION).start();
                }
                isChatEnabled = !isChatEnabled;
            }
        });
        iv_fragment_single_stream = view.findViewById(R.id.iv_fragment_single_stream);
        iv_fragment_single_stream.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStreamEnabled) {
                    iv_fragment_single_stream.setImageResource(R.drawable.ic_fragment_single_stream_off);
                    sv_fragment_single_stream.animate().alpha(0.0f).setDuration(ANIMATION_DURATION).start();
                    if (mediaPlayer != null)
                        mediaPlayer.pause();
                } else {
                    iv_fragment_single_stream.setImageResource(R.drawable.ic_fragment_single_stream_on);
                    sv_fragment_single_stream.animate().alpha(1.0f).setDuration(ANIMATION_DURATION).start();
                    if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
                        mediaPlayer.start();
                    }
                }
                isStreamEnabled = !isStreamEnabled;
            }
        });
    }

    private void toggleWindow() {
        if (isHolderEnabled) {
            ll_fragment_single_stream_holder.animate().translationX(-ll_fragment_single_stream_holder.getHeight()).setDuration(ANIMATION_DURATION).start();
        } else {
            ll_fragment_single_stream_holder.animate().translationX(0).setDuration(ANIMATION_DURATION).start();
        }
        isHolderEnabled = !isHolderEnabled;
    }

    private void initSurfaceHolder() {
        sv_fragment_single_stream = view.findViewById(R.id.sv_fragment_single_stream);
        sv_fragment_single_stream.addTapListener(new TappableSurfaceView.TapListener() {
            @Override
            public void onTap(MotionEvent event) {
                toggleWindow();
            }
        });
        surfaceHolder = sv_fragment_single_stream.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pb_fragment_single_stream.setVisibility(View.INVISIBLE);
                if (!mp.isPlaying()) {
                    int videoWidth = mediaPlayer.getVideoWidth();
                    int videoHeight = mediaPlayer.getVideoHeight();
                    float videoProportion = (float) videoWidth / (float) videoHeight;

                    ViewGroup.LayoutParams lp = sv_fragment_single_stream.getLayoutParams();
                    lp.height = (int) ((float) sv_fragment_single_stream.getWidth() / videoProportion);
                    sv_fragment_single_stream.setLayoutParams(lp);
                    mp.start();
                }
            }
        });
    }

    private void initAdapterChat() {
        rv_fragment_single_stream_chat = view.findViewById(R.id.rv_fragment_single_stream_chat);
        linearLayout = new LinearLayoutManager(getContext(), LinearLayoutManager.VERTICAL, false);
        rv_fragment_single_stream_chat.setLayoutManager(linearLayout);
        adapterChat = new PlainTextAdapter();
        rv_fragment_single_stream_chat.setAdapter(adapterChat);
        for (int i = 0; i < 7; i++) {
            adapterChat.add("");
        }
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        mediaPlayer.setDisplay(surfaceHolder);
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }

    @Override
    public void onPause() {
        super.onPause();
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    public void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        super.onResume();
    }

    private int getColorAccentTheme() {
        TypedValue typedValue = new TypedValue();
        if (getContext() != null)
            getContext().getTheme().resolveAttribute(R.attr.colorAccent, typedValue, true);
        return typedValue.data;
    }

    public class StreamsAdapter extends PagedListAdapter<Streams, StreamsAdapter.ViewHolder> {

        StreamsAdapter() {
            super(DIFF_CALLBACK);
        }

        @NonNull
        @Override
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.item_list_simple_stream, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull final ViewHolder viewHolder, int i) {
            final Streams streams = getItem(i);
            if (streams != null) {
                viewHolder.tv.setTextColor(getResources().getColor(R.color.textColorSecondary));
                viewHolder.tv.setText(streams.getChannel().getDisplay_name());
                viewHolder.tv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (oldView != null)
                            ((TextView) oldView).setTextColor(getResources().getColor(R.color.textColorSecondary));
                        oldView = v;
                        viewHolder.tv.setTextColor(getColorAccentTheme());
                        startStream(streams.getChannel().getDisplay_name().toLowerCase());
                        startChat(streams.getChannel().getDisplay_name().toLowerCase());
                        rv_fragment_single_stream.animate().alpha(0.0f).withEndAction(new Runnable() {
                            @Override
                            public void run() {
                                rv_fragment_single_stream.setVisibility(View.GONE);
                            }
                        }).setDuration(ANIMATION_DURATION).start();
                    }
                });
                Picasso.get().load(streams.getPreview().getMedium()).placeholder(R.drawable.streams_placeholder).into(viewHolder.iv);
            }
        }

        class ViewHolder extends RecyclerView.ViewHolder {
            private TextView tv;
            private ImageView iv;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                tv = itemView.findViewById(R.id.tv);
                iv = itemView.findViewById(R.id.iv);
            }
        }
    }

    public class PlainTextAdapter extends RecyclerView.Adapter<PlainTextAdapter.ViewHolder> {

        private List<String> list;

        PlainTextAdapter() {
            list = new ArrayList<>();
        }

        public void add(String item) {
            list.add(item);
            notifyItemInserted(list.size() - 1);
        }

        @NonNull
        @Override
        public PlainTextAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_list_plain_multi_twitch_text, viewGroup, false);
            return new PlainTextAdapter.ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull PlainTextAdapter.ViewHolder holder, int i) {
            String text = list.get(i);
            holder.textView.setText(text);
            holder.textView.setAlpha(alphaChat);
////        float alpha = (1F - (getItemCount() - position)* 0.1F);//для 10 элементов
//            float alpha = (1F - (getItemCount() - position) * 0.075F);//для 15 элементов
//            holder.view.setAlpha(alpha);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class ViewHolder extends RecyclerView.ViewHolder {

            private TextView textView;

            ViewHolder(@NonNull View itemView) {
                super(itemView);
                textView = itemView.findViewById(R.id.tv);
                textView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggleWindow();
                    }
                });
            }
        }
    }

}
