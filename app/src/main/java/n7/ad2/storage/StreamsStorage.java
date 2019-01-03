package n7.ad2.storage;

import android.app.Application;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import n7.ad2.MySharedPreferences;
import n7.ad2.retrofit.streams.Channel;
import n7.ad2.retrofit.streams.Preview;
import n7.ad2.retrofit.streams.StreamList;
import n7.ad2.retrofit.streams.StreamSingle;
import n7.ad2.retrofit.streams.Streams;
import n7.ad2.retrofit.streams.StreamsApi;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import static n7.ad2.main.MainViewModel.ACCOUNTS_FOR_TOP_TWITCH;


public class StreamsStorage {

    private int total = 0;
    private MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private Application application;

    public StreamsStorage(Application application) {
        this.application = application;
    }

    public LiveData<Boolean> isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading.postValue(loading);
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<Streams> getPremium() {
        setLoading(true);
        List<Streams> list = new ArrayList<>();
        String premiums[] = MySharedPreferences.getSharedPreferences(application).getString(ACCOUNTS_FOR_TOP_TWITCH, "").split("\\+");
        if (premiums.length > 0)
            for (String premium : premiums) {
                String parts[] = premium.split("\\^");

                Streams stream = new Streams();
                Preview preview = new Preview();
                Channel channel = new Channel();

                if (parts.length > 0)
                    channel.setDisplay_name(premium.split("\\^")[0]);
                preview.setMedium("nothing");
                if (parts.length > 1)
                    stream.setViewers(Integer.valueOf(premium.split("\\^")[1]));

                String BASE_URL = "https://api.twitch.tv/kraken/";
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(BASE_URL) //базовая часть адреса
                        .addConverterFactory(GsonConverterFactory.create()) //конвертер, необходимый для преобразования JSON'а в объекты
                        .build();
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
        setLoading(false);
        return list;
    }

    public List<Streams> getData(int offset, int limit) {
        setLoading(true);
        List<Streams> list = new ArrayList<>();

        String BASE_URL = "https://api.twitch.tv/kraken/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL) //базовая часть адреса
                .addConverterFactory(GsonConverterFactory.create()) //конвертер, необходимый для преобразования JSON'а в объекты
                .build();
        StreamsApi streamsApi = retrofit.create(StreamsApi.class);
        Call<StreamList> streamListCall = streamsApi.getStreams(limit, offset);
        try {
            Response response = streamListCall.execute();
            if (response.isSuccessful()) {
                StreamList streamList = (StreamList) response.body();
                total = streamList.get_total();
                list = streamList.getStreams();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        setLoading(false);
        return list;
    }
}
