package n7.ad2.activity;

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
import n7.ad2.db.news.SteamNews;
import n7.ad2.db.news.SteamNewsDao;
import n7.ad2.db.news.SteamNewsRoomDatabase;


public class NewsActivity extends BaseActivity {

    public static final String HREF = "href";
    public String base_url = "https://www.dotabuff.com";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);

        initBaseUrl();
        setToolbar();
        setNews();
    }

    private void initBaseUrl() {
        String language = getString(R.string.language_resource);
        switch (language) {
            default:
            case "eng":
                base_url = "https://www.dotabuff.com";
                break;
            case "ru":
                base_url = "https://ru.dotabuff.com";
                break;
            case "zh":
                base_url = "https://ru.dotabuff.com";
                break;
        }
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

        SteamNewsDao steamNewsDao = SteamNewsRoomDatabase.getDatabase(this).steamNewsDao();
        steamNewsDao.getNewsByIndex(href).observe(this, new Observer<SteamNews>() {
            @Override
            public void onChanged(@Nullable SteamNews steamNews) {
                contents.loadDataWithBaseURL(null, "<style>img{display: inline;height: auto;max-width: 100%;text-decoration:none;}</style>" +
                        "<style type=\"text/css\">body{color:" + getColorTextTheme() + ";}</style>" + steamNews.contents, "text/html", "UTF-8", null);
            }
        });
//        webSettings.setDefaultFontSize(10);
    }

}
