package n7.ad2.worker;

import android.support.annotation.NonNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.work.Worker;
import n7.ad2.db.news.SteamNews;
import n7.ad2.db.news.SteamNewsDao;
import n7.ad2.db.news.SteamNewsRoomDatabase;
import n7.ad2.retrofit.steamNews.Appnews;
import n7.ad2.retrofit.steamNews.News;
import n7.ad2.retrofit.steamNews.NewsItem;
import n7.ad2.retrofit.steamNews.SteamNewsApi;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class SteamNewsWorker extends Worker {

    public static final String BASE_URL = "http://api.steampowered.com/ISteamNews/GetNewsForApp/v0002/";
    public static final String NEWS_COUNT = "news_count";
    public static final String PAGE = "page";
    public static final String END_DATE = "end_date";
    public static final String DELETE_TABLE = "delete_table";
    public static final String UNIQUE_WORK = "unique_news_work";

    @NonNull
    @Override
    public Result doWork() {

        int newsCount = getInputData().getInt(NEWS_COUNT, 30);
        long endDate = getInputData().getLong(END_DATE, -1);
        boolean deleteTable = getInputData().getBoolean(DELETE_TABLE, false);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        SteamNewsApi steamNewsApi = retrofit.create(SteamNewsApi.class);
        Call<News> newsCall = steamNewsApi.getSteamNews(newsCount, endDate);

        try {
            Response response = newsCall.execute();
            if (response.isSuccessful()) {
                News news = (News) response.body();
                Appnews appnews = news.getAppnews();
                List<NewsItem> newsItems = appnews.getNewsitems();
                List<SteamNews> steamNewsList = new ArrayList<>();
                for (NewsItem newsItem : newsItems) {
                    SteamNews steamNews = new SteamNews();
                    steamNews.date = newsItem.getDate();
                    steamNews.contents = newsItem.getContents().replace("[img]", "<img src=\"").replace("[/img]", "\"/><br>");
                    steamNews.title = newsItem.getTitle();
                    steamNewsList.add(steamNews);
                }

                SteamNewsDao steamNewsDao = SteamNewsRoomDatabase.getDatabase(getApplicationContext()).steamNewsDao();
                if (deleteTable) steamNewsDao.deleteAll();
                steamNewsDao.setSteamNews(steamNewsList);

                return Result.SUCCESS;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.FAILURE;
    }
}
