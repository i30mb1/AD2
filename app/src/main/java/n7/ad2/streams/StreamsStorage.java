package n7.ad2.streams;

import android.app.Application;
import android.databinding.ObservableBoolean;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import n7.ad2.MySharedPreferences;
import n7.ad2.streams.retrofit.Channel;
import n7.ad2.streams.retrofit.Preview;
import n7.ad2.streams.retrofit.StreamList;
import n7.ad2.streams.retrofit.StreamSingle;
import n7.ad2.streams.retrofit.Streams;
import n7.ad2.streams.retrofit.StreamsApi;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static n7.ad2.main.MainViewModel.ACCOUNTS_FOR_TOP_TWITCH;

public class StreamsStorage {

    private Application application;
    private ObservableBoolean isLoading;

    public StreamsStorage(Application application,ObservableBoolean isLoading) {
        this.application = application;
        this.isLoading = isLoading;
    }

    public List<Streams> getSubscribersStreams() {
        isLoading.set(true);
        List<Streams> list = new ArrayList<>();
        String accounts[] = MySharedPreferences.getSharedPreferences(application).getString(ACCOUNTS_FOR_TOP_TWITCH, "").split("\\+");
        if (accounts.length > 0) for (String account : accounts) {
            String parts[] = account.split("\\^");

            Streams stream = new Streams();
            Preview preview = new Preview();
            Channel channel = new Channel();

            if (parts.length > 0) channel.setDisplay_name(parts[0]);
            preview.setMedium("nothing");
            if (parts.length > 1) stream.setViewers(Integer.valueOf(parts[1]));

            String BASE_URL = "https://api.twitch.tv/kraken/";
            Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
            StreamsApi streamsApi = retrofit.create(StreamsApi.class);
            Call<StreamSingle> streamListCall = streamsApi.getStream(channel.getDisplay_name());
            try {
                Response response = streamListCall.execute();
                if (response.isSuccessful()) {
                    StreamSingle responseStream = (StreamSingle) response.body();
                    if (responseStream != null && responseStream.getStream() != null) {
                        if (stream.getViewers() == 0)
                            stream.setViewers(responseStream.getStream().getViewers());
                        stream.setPreview(responseStream.getStream().getPreview());
                        stream.setChannel(responseStream.getStream().getChannel());
                        list.add(stream);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        isLoading.set(false);
        return list;
    }

    public List<Streams> getData(int offset, int limit) {
        isLoading.set(true);
        List<Streams> list = new ArrayList<>();

        String BASE_URL = "https://api.twitch.tv/kraken/";
        Retrofit retrofit = new Retrofit.Builder().baseUrl(BASE_URL).addConverterFactory(GsonConverterFactory.create()).build();
        StreamsApi streamsApi = retrofit.create(StreamsApi.class);
        Call<StreamList> streamListCall = streamsApi.getStreams(limit, offset);
        try {
            Response response = streamListCall.execute();
            if (response.isSuccessful()) {
                StreamList streamList = (StreamList) response.body();
//                total = streamList.get_total();
                list = streamList.getStreams();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        isLoading.set(false);
        return list;
    }
}
