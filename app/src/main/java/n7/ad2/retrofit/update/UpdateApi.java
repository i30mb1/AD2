package n7.ad2.retrofit.update;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface UpdateApi {

    @GET("updates.json")
    Call<Update> getUpdate();

    //@Query для запросов с параметрами
    @GET("updates.json")
    Call<List<Update>> getUpdate(@Query("n7") boolean n7);

    //@Path для изменения пути запроса
    @GET("{id}/updates.json")
    Call<Update> getUpdate(@Path("id") int id, @Query("n7") boolean n7);

}
