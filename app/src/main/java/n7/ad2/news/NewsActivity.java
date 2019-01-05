package n7.ad2.news;

import android.arch.lifecycle.Observer;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.MenuItem;
import android.webkit.WebSettings;
import android.webkit.WebView;

import n7.ad2.R;
import n7.ad2.activity.BaseActivity;
import n7.ad2.news.db.NewsDao;
import n7.ad2.news.db.NewsModel;
import n7.ad2.news.db.NewsRoomDatabase;


public class NewsActivity extends BaseActivity {

    public static final String HREF = "href";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        setToolbar();
        setNews();
    }

    private void setToolbar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.news));
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private int getColorAccentTheme() {
//        TypedValue typedValue = new TypedValue();
//        getTheme().resolveAttribute(R.attr.colorPrimary, typedValue, true);
//        return typedValue.data;
//    }

    private String getColorTextTheme() {
        TypedValue typedValue = new TypedValue();
        Resources.Theme theme = getTheme();
        theme.resolveAttribute(android.R.attr.textColorPrimary, typedValue, true);
        int color = typedValue.data;
        return "#" + Integer.toHexString(color & 0x00FFFFFF);
    }

    private void setNews() {
        final WebView contents = findViewById(R.id.tv_activity_news_contents);
        contents.setBackgroundColor(Color.TRANSPARENT);

        WebSettings webSettings = contents.getSettings();
        webSettings.setTextZoom(85);

        String href = getIntent().getStringExtra(HREF);

        NewsDao steamNewsDao = NewsRoomDatabase.getDatabase(this).steamNewsDao();
        steamNewsDao.getNewsByIndex(href).observe(this, new Observer<NewsModel>() {
            @Override
            public void onChanged(@Nullable NewsModel steamNews) {
                contents.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;text-decoration:none;}</style>" +
                        "<style type=\"text/css\">body{color:" + getColorTextTheme() + ";}</style>" + steamNews.getContent(), "text/html", "UTF-8", null);
            }
        });
//        webSettings.setDefaultFontSize(10);
    }

}
