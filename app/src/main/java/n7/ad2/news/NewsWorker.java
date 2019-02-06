package n7.ad2.news;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.LinkedList;
import java.util.concurrent.TimeUnit;

import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.Worker;
import androidx.work.WorkerParameters;
import n7.ad2.R;
import n7.ad2.news.db.NewsDao;
import n7.ad2.news.db.NewsModel;
import n7.ad2.news.db.NewsRoomDatabase;

import static n7.ad2.main.MainActivity.LOG_ON_RECEIVE;

public class NewsWorker extends Worker {

    public static final String PAGE = "page";
    public static final String HREF = "href";
    public static final String DELETE_TABLE = "delete_table";
    public static final String TAG = "news_worker_tag";
    public String base_url = "https://ru.dotabuff.com/blog?page=";

    public NewsWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

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

        int page = getInputData().getInt(PAGE, 1);
        NewsDao steamNewsDao = NewsRoomDatabase.getDatabase(getApplicationContext()).steamNewsDao();

        boolean deleteTable = getInputData().getBoolean(DELETE_TABLE, false);
        if (deleteTable) steamNewsDao.deleteAll();

        try {
            Document doc = Jsoup.connect(base_url + page).get();
            Elements body = doc.getElementsByClass("related-posts");
            Elements news = body.get(0).getElementsByTag("a");

            LinkedList<NewsModel> list = new LinkedList<>();
            for (Element element : news) {
                String href = element.attr("href");
                Elements headLines = element.getElementsByClass("headline");
                if (headLines.size() == 0) continue;
                String title = headLines.get(0).text();

                String imageHrefRaw = element.child(0).attr("style");
                String imageHref = imageHrefRaw.substring(imageHrefRaw.indexOf("(") + 1, imageHrefRaw.indexOf(")"));

                NewsModel steamNews = new NewsModel(href);
                steamNews.setTitle(title);
                steamNews.setImageHref(imageHref);

                list.addLast(steamNews);

                Data data = new Data.Builder().putString(HREF, href).build();
                OneTimeWorkRequest worker = new OneTimeWorkRequest.Builder(NewsSingleWorker.class).setInputData(data).setInitialDelay(10, TimeUnit.SECONDS).build();
                WorkManager.getInstance().enqueue(worker);
            }

            steamNewsDao.setNews(list);
            getApplicationContext().sendBroadcast(new Intent(LOG_ON_RECEIVE).putExtra(LOG_ON_RECEIVE, "page_" + page + "_news_loaded"));

            return Result.success();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return Result.failure();
    }

}
