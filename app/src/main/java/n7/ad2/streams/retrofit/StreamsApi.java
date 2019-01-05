package n7.ad2.streams.retrofit;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface StreamsApi {

    @GET("streams?game=Dota%202&stream_type=live&client_id=vmr0piicf3e3nxw4fs0zz2e2vqak8y")
    Call<StreamList> getStreams(@Query("limit") int limit, @Query("offset") int offset);

    @GET("streams/{name}?client_id=vmr0piicf3e3nxw4fs0zz2e2vqak8y")
    Call<StreamSingle> getStream(@Path("name") String name);

}
