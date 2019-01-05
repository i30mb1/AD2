package n7.ad2.main;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import n7.ad2.R;

import static n7.ad2.setting.LogInActivity.USER_ID;

public class DotabuffActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dotabuff);

        final WebView webView = findViewById(R.id.wv_dotabuff);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebViewClient(new WebViewClient());
        webView.loadUrl("https://www.dotabuff.com/players/" + PreferenceManager.getDefaultSharedPreferences(this).getString(USER_ID, ""));
    }
}
