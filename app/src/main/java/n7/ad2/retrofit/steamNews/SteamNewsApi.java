package n7.ad2.retrofit.steamNews;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface SteamNewsApi {

    //    @GET(".")
    @GET("?appid=570&maxlength=max&format=json")
    Call<News> getSteamNews(
            @Query("count") int count,
            @Query("enddate") long endDate);
}
