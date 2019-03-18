package n7.ad2.news;

import android.content.Context;
import android.support.annotation.NonNull;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;

import androidx.work.Worker;
import androidx.work.WorkerParameters;
import n7.ad2.R;
import n7.ad2.news.db.NewsDao;
import n7.ad2.news.db.NewsRoomDatabase;

import static n7.ad2.news.NewsWorker.HREF;

public class NewsSingleWorker extends Worker {

    private String base_url;

    public NewsSingleWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        initBaseUrl();

        String href = getInputData().getString(HREF);
        try {
            Document doc = Jsoup.connect(base_url + href).get();
            for (Element img : doc.getElementsByTag("img")) {
                if (img.attr("src").startsWith("/assets/")) {
                    img.remove();
                }
            }
            for (Element a : doc.getElementsByTag("a")) {
                a.removeAttr("href");
            }
            for (Element iframe : doc.getElementsByTag("iframe")) {
                iframe.remove();
            }
            for (Element social : doc.getElementsByClass("social")) {
                social.remove();
            }
            for (Element element : doc.getElementsByClass("categories categories-bottom")) {
                element.remove();
            }
            Elements body;
            if (doc.getElementsByClass("story-big").size() == 0) {
                body = doc.getElementsByClass("body");
            } else {
                body = doc.getElementsByClass("story-big");
            }

            String content = body.toString();

            NewsDao steamNewsDao = NewsRoomDatabase.getDatabase(getApplicationContext()).steamNewsDao();
            steamNewsDao.setContent(content, href);

            return Result.success();
        } catch (Throwable e) {
            e.printStackTrace();
        }

        return Result.failure();
    }

    private void initBaseUrl() {
        String language = getApplicationContext().getString(R.string.language_resource);
        switch (language) {
            default:
            case "eng":
                base_url = "https://www.dotabuff.com";
                break;
            case "ru":
                base_url = "https://ru.dotabuff.com";
                break;
        }
    }
}
