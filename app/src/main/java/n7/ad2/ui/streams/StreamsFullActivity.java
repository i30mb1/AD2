package n7.ad2.ui.streams;

import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.gikk.twirk.Twirk;
import com.gikk.twirk.TwirkBuilder;
import com.gikk.twirk.events.TwirkListenerBaseImpl;
import com.gikk.twirk.types.twitchMessage.TwitchMessage;
import com.gikk.twirk.types.users.TwitchUser;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import n7.ad2.R;
import n7.ad2.ui.streams.utilsTwitch.Element;
import n7.ad2.ui.streams.utilsTwitch.Playlist;
import n7.ad2.ui.streams.utilsTwitch.TappableSurfaceView;
import n7.ad2.utils.AppExecutors;
import n7.ad2.utils.BaseActivity;
import n7.ad2.utils.SmoothScrollableLinearLayoutManager;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import static androidx.core.internal.view.SupportMenuItem.SHOW_AS_ACTION_ALWAYS;
// https://medium.com/@mattgrint/detecting-clicks-or-scrolls-in-kotlin-for-android-9c2cf0aab7fa
public class StreamsFullActivity extends BaseActivity implements SurfaceHolder.Callback {

    public static final String CHANNEL_NAME = "CHANNEL_NAME";
    public static final String CHANNEL_TITLE = "CHANNEL_TITLE";
    public static final int DELAY_TO_CLOSE_TOOLBAR = 3000;
    private static final String PLAYER_DEFAULT_QUALITY = "PLAYER_DEFAULT_QUALITY";
    private static final String PLAYER_DEFAULT_CHAT_SPEED = "PLAYER_DEFAULT_CHAT_SPEED";
    public static float speed = 1;
    private final ArrayList<String> qualitiesTitles = new ArrayList<>();
    private final String[] chatSpeeds = new String[]{"0x", "2x", "3x", "5x"};
    private int chatSpeedSelected = 0;
    private ImageView iv_toolbar;
    private MediaPlayer mediaPlayer;
    private SurfaceHolder surfaceHolder;
    private boolean isToolbarVisible = true;
    private boolean isChatVisible = true;
    private RecyclerView rv_activity_twitch_game;
    private ProgressBar pb_activity_twitch_game;
    private int qualityPosition;
    private Element.List qualities;
    private PlainTextAdapter adapter;
    private SharedPreferences sp;
    private boolean isPremium = true;
    private AppExecutors appExecutors;
    private MenuItem itemMenuQuality;
    private MenuItem itemMenuVisibility;
    private MenuItem itemMenuChatSpeed;
    private Toolbar toolbar;
    private float alphaChat = 1.0F;
    private SmoothScrollableLinearLayoutManager layoutManager;

    private static String readStream(InputStream in) throws IOException {
        InputStreamReader inputStreamReader;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.KITKAT) {
            inputStreamReader = new InputStreamReader(in, StandardCharsets.UTF_8);
        } else {
            inputStreamReader = new InputStreamReader(in, Charset.forName("UTF-8"));
        }
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
    public boolean onCreateOptionsMenu(Menu menu) {
        itemMenuQuality = menu.add(R.string.activity_twitch_game);
        itemMenuQuality.setShowAsAction(SHOW_AS_ACTION_ALWAYS);

        if (isPremium) {
            itemMenuChatSpeed = menu.add("speed");
            itemMenuChatSpeed.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
            chatSpeedSelected = sp.getInt(PLAYER_DEFAULT_CHAT_SPEED, 0);
            itemMenuChatSpeed.setTitle(chatSpeeds[chatSpeedSelected]);
            setLinearLayout(chatSpeedSelected);

            itemMenuVisibility = menu.add(String.valueOf(alphaChat));
            itemMenuVisibility.setShowAsAction(SHOW_AS_ACTION_ALWAYS);
        }
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        sp.edit().putInt(PLAYER_DEFAULT_CHAT_SPEED, chatSpeedSelected).apply();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (itemMenuQuality == item) {
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            String[] animals = new String[qualitiesTitles.size()];
            qualitiesTitles.toArray(animals);
            int checkedItem = qualityPosition;
            builder.setSingleChoiceItems(animals, checkedItem, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    sp.edit().putInt(PLAYER_DEFAULT_QUALITY, i).apply();
                    qualityPosition = i;
                    itemMenuQuality.setTitle(qualitiesTitles.get(qualityPosition));
                    startStreamWithDefineQuality();
                    dialogInterface.dismiss();
                }
            });
            AlertDialog dialog = builder.create();
            dialog.show();
        }
        if (itemMenuChatSpeed == item) {
            if (isPremium) {
                final AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setSingleChoiceItems(chatSpeeds, chatSpeedSelected, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        setLinearLayout(which);
                        dialog.dismiss();

                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
            }
        }
        if (itemMenuVisibility == item) {
            if (isPremium) {
                if (alphaChat == 1.0F) {
                    alphaChat = 0.6F;
                } else if (alphaChat == 0.6F) {
                    alphaChat = 0.3F;
                } else if (alphaChat == 0.3F) {
                    alphaChat = 1.0F;
                }
                itemMenuVisibility.setTitle(String.valueOf(alphaChat));
                adapter.notifyDataSetChanged();
            }
        }

        return super.onOptionsItemSelected(item);
    }

    private void setLinearLayout(int which) {
        switch (which) {
            case 0:
                speed = 1;
                chatSpeedSelected = which;
                itemMenuChatSpeed.setTitle(chatSpeeds[chatSpeedSelected]);
                break;
            case 1:
                speed = 2000;
                chatSpeedSelected = which;
                itemMenuChatSpeed.setTitle(chatSpeeds[chatSpeedSelected]);
                break;
            case 2:
                speed = 3000;
                chatSpeedSelected = which;
                itemMenuChatSpeed.setTitle(chatSpeeds[chatSpeedSelected]);
                break;
            case 3:
                speed = 5000;
                chatSpeedSelected = which;
                itemMenuChatSpeed.setTitle(chatSpeeds[chatSpeedSelected]);
                break;
        }

    }

    private void initWindow() {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR_OVERLAY);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON | WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED | WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON | WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    private void initViews() {
        pb_activity_twitch_game = findViewById(R.id.pb_activity_twitch_game);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        initWindow();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_twitch_game);

        appExecutors = new AppExecutors();

        initPremium();
        initAdapter();
        setToolbar();
        initViews();
        initSurfaceHolder();

        startStream();
        startChat();
    }

    private void initPremium() {
        sp = PreferenceManager.getDefaultSharedPreferences(this);

        if (!isPremium) {
            alphaChat = 1.0F;
            chatSpeedSelected = 0;
        }
    }

    private void startStream() {
        pb_activity_twitch_game.setVisibility(View.VISIBLE);
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String accessToken = "https://api.twitch.tv/api/channels/" + getIntent().getExtras().getString(CHANNEL_NAME) + "/access_token?client_id=kimne78kx3ncx6brgo4mv6wki5h1ko";
                    OkHttpClient client = new OkHttpClient();
                    Request request = new Request.Builder().url(accessToken).build();
                    Response response = client.newCall(request).execute();
                    JSONObject jsonObject = new JSONObject(response.body().string());
                    String token = jsonObject.getString("token");
                    String sig = jsonObject.getString("sig");
                    String url = "http://usher.twitch.tv/api/channel/hls/" + getIntent().getExtras().getString(CHANNEL_NAME) + ".m3u8?token=" + URLEncoder.encode(token, "UTF-8") + "&sig=" + sig;
                    String result2 = basicRequestSend(url);
                    Playlist playList = Playlist.parse(result2);
                    qualities = new Element.List(playList.getElements());
                    for (int i = 0; i < qualities.size(); i++) {
                        qualitiesTitles.add(qualities.get(i).getQuality());
                    }
                    qualityPosition = sp.getInt(PLAYER_DEFAULT_QUALITY, qualities.size() - 1);
                    startStreamWithDefineQuality();
                } catch (final Exception e) {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            Toast.makeText(StreamsFullActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                        }
                    });
                } finally {
                    appExecutors.mainThread().execute(new Runnable() {
                        @Override
                        public void run() {
                            pb_activity_twitch_game.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });
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

    private void startStreamWithDefineQuality() {
        try {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.reset();
            }
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
            if (qualityPosition < qualities.size()) {
                mediaPlayer.setDataSource(qualities.get(qualityPosition).getURI().toString());
            } else {
                qualityPosition = qualities.size() - 1;
                mediaPlayer.setDataSource(qualities.get(qualityPosition).getURI().toString());
            }
            mediaPlayer.prepareAsync();
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    itemMenuQuality.setTitle(qualities.get(qualityPosition).getQuality());
                }
            });

        } catch (final Exception e) {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(StreamsFullActivity.this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } finally {
            appExecutors.mainThread().execute(new Runnable() {
                @Override
                public void run() {
                    pb_activity_twitch_game.setVisibility(View.INVISIBLE);
                }
            });
        }
    }

    private void startChat() {
        appExecutors.networkIO().execute(new Runnable() {
            @Override
            public void run() {
//                final Twirk twirk = new TwirkBuilder("#" + getIntent().getExtras().getString(CHANNEL_NAME), "i30mb1", "oauth:g8qwh3dw0ul0oopjsr8dmmkxa2mpqe").build();
                final Twirk twirk = new TwirkBuilder("#" + getIntent().getExtras().getString(CHANNEL_NAME), "aboutdota2", "oauth:jfhimbps50vmxgto0aj75n0smbbphz").build();
                try {
                    twirk.connect();
                    twirk.addIrcListener(new TwirkListenerBaseImpl() {
                        @Override
                        public void onPrivMsg(TwitchUser sender, TwitchMessage message) {
                            super.onPrivMsg(sender, message);
                            if (isChatVisible) {
                                adapter.add(message.getContent());
                                if (chatSpeedSelected > 0) {
                                    if (!layoutManager.isSmoothScrolling())
                                        rv_activity_twitch_game.smoothScrollToPosition(adapter.getItemCount() - 1);
                                } else {
                                    rv_activity_twitch_game.smoothScrollToPosition(adapter.getItemCount() - 1);
                                }
                            }
                        }
                    });
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    private void initSurfaceHolder() {
        TappableSurfaceView sv_activity_twitch_game = findViewById(R.id.sv_activity_twitch_game);
        sv_activity_twitch_game.addTapListener(new TappableSurfaceView.TapListener() {
            @Override
            public void onTap(MotionEvent event) {
                toggleToolbar();
            }
        });

        surfaceHolder = sv_activity_twitch_game.getHolder();
        surfaceHolder.addCallback(this);
        surfaceHolder.setKeepScreenOn(true);

        mediaPlayer = new MediaPlayer();
        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                pb_activity_twitch_game.setVisibility(View.INVISIBLE);
                if (!mp.isPlaying()) {
                    mp.start();
                }
            }
        });
    }

    private void initAdapter() {
        rv_activity_twitch_game = findViewById(R.id.rv_activity_twitch_game);
        rv_activity_twitch_game.setHasFixedSize(true);
        layoutManager = new SmoothScrollableLinearLayoutManager(StreamsFullActivity.this);
        rv_activity_twitch_game.setLayoutManager(layoutManager);
        adapter = new PlainTextAdapter();
        rv_activity_twitch_game.setAdapter(adapter);
        for (int i = 0; i < 20; i++) {
            adapter.add("");
        }
    }

    private void toggleToolbar() {
        if (toolbar == null) return;
        if (isToolbarVisible) {
            toolbar.animate().alpha(0.0f).withEndAction(new Runnable() {
                @Override
                public void run() {
                    if (toolbar != null) toolbar.setVisibility(View.GONE);
                }
            }).setDuration(300).start();
        } else {
            toolbar.animate().alpha(1.0f).withStartAction(new Runnable() {
                @Override
                public void run() {
                    if (toolbar != null) toolbar.setVisibility(View.VISIBLE);
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            if (isToolbarVisible) {
                                toggleToolbar();
                            }
                        }
                    }, DELAY_TO_CLOSE_TOOLBAR);
                }
            }).setDuration(300).start();
        }
        isToolbarVisible = !isToolbarVisible;
    }

    private void setImageViewToolbar(boolean isVisible) {
        if (isVisible) {
            iv_toolbar.setImageResource(R.drawable.ic_fragment_single_stream_chat_on);
            rv_activity_twitch_game.animate().alpha(1.0f).setDuration(300).start();
        } else {
            iv_toolbar.setImageResource(R.drawable.ic_fragment_single_stream_chat_off);
            rv_activity_twitch_game.animate().alpha(0.0f).setDuration(300).start();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mediaPlayer != null)
            mediaPlayer.pause();
    }

    @Override
    protected void onResume() {
        if (mediaPlayer != null && !mediaPlayer.isPlaying()) {
            mediaPlayer.start();
        }
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                toggleToolbar();
            }
        }, 300+2000);

        super.onResume();
    }

    private void setToolbar() {
        toolbar = findViewById(R.id.toolbar);
        iv_toolbar = findViewById(R.id.iv_toolbar_chat);

        setSupportActionBar(toolbar);
        setTitle(getIntent().getStringExtra(CHANNEL_NAME));
//        toolbar.setSubtitle(getIntent().getStringExtra(CHANNEL_TITLE));
        iv_toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isChatVisible = !isChatVisible;
                setImageViewToolbar(isChatVisible);
            }
        });
        setImageViewToolbar(isChatVisible);
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
    protected void onDestroy() {
        if (mediaPlayer != null) {
            try {
                mediaPlayer.release();
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                mediaPlayer = null;
            }
        }
        super.onDestroy();
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
        public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
            View view = getLayoutInflater().inflate(R.layout.item_list_plain_twitch_text, viewGroup, false);
            return new ViewHolder(view);
        }

        @Override
        public void onBindViewHolder(@NonNull ViewHolder holder, int i) {
            String text = list.get(i);
            holder.textView.setText(text);
////        float alpha = (1F - (getItemCount() - position)* 0.1F);//для 10 элементов
//            float alpha = (1F - (getItemCount() - position) * 0.075F);//для 15 элементов
            holder.textView.setAlpha(alphaChat);
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
                        toggleToolbar();
                    }
                });
            }
        }
    }
}
