package n7.ad2.worker;

import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import n7.ad2.R;
import n7.ad2.db.news.SteamNews;
import n7.ad2.db.news.SteamNewsDao;
import n7.ad2.db.news.SteamNewsRoomDatabase;

public class SteamDbNewsWorker extends Worker {

    public static final String PAGE = "page";
    public static final String HREF = "href";
    public static final String DELETE_TABLE = "delete_table";
    public static final String UNIQUE_WORK = "unique_news_work";
    public String base_url = "https://ru.dotabuff.com/blog?page=";
    private SteamNewsDao steamNewsDao;

    private void initBaseUrl() {
        String language = getApplicationContext().getString(R.string.language_resource);
        switch (language) {
            default:
            case "eng":
                base_url = "https://www.dotabuff.com/blog?page=";
                break;
            case "ru":
                base_url = "https://ru.dotabuff.com/blog?page=";
                break;
        }
    }

    @NonNull
    @Override
    public Result doWork() {
        initBaseUrl();
        List<SteamNews> steamNewsList = new ArrayList<>();
        int page = getInputData().getInt(PAGE, 1);
        boolean deleteTable = getInputData().getBoolean(DELETE_TABLE, false);
        steamNewsDao = SteamNewsRoomDatabase.getDatabase(getApplicationContext()).steamNewsDao();

        try {
            Document doc = Jsoup.connect(base_url + page).get();
            Elements body = doc.getElementsByClass("related-posts");
            Elements news = body.get(0).getElementsByTag("a");

            for (Element element : news) {
                String href = element.attr("href");
                Elements headLines = element.getElementsByClass("headline");
                if (headLines.size() == 0) continue;
                String headLine = headLines.get(0).text();

                String imageHref = element.child(0).attr("style");
                String withoutBracket = imageHref.substring(imageHref.indexOf("(") + 1, imageHref.indexOf(")"));

                SteamNews steamNews = new SteamNews();
                steamNews.href = href;
                steamNews.title = headLine;
                steamNews.imageHref = withoutBracket;
                steamNewsList.add(steamNews);

                Data data = new Data.Builder().putString(HREF, href).build();
                OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(SteamDbSingleNewsWorker.class).setInputData(data).build();
                WorkManager.getInstance().enqueue(worker);
            }

            if (deleteTable) steamNewsDao.deleteAll();
            steamNewsDao.setSteamNews(steamNewsList);
            return Result.SUCCESS;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Result.FAILURE;
    }

}
