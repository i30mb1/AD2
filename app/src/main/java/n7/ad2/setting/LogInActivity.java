package n7.ad2.setting;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import androidx.appcompat.app.AppCompatActivity;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import n7.ad2.R;

public class LogInActivity extends AppCompatActivity {

    public static final int REQUEST_CODE_LOG_IN = 7;
    public static final String USER_ID = "user_id";
    public static final String REALM_PARAM = "AD2";
    public static final String LOGIN_URL = "https://steamcommunity.com/openid/login?" +
            "openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&" +
            "openid.identity=http://specs.openid.net/auth/2.0/identifier_select&" +
            "openid.mode=checkid_setup&" +
            "openid.ns=http://specs.openid.net/auth/2.0&" +
            "openid.realm=http://" + REALM_PARAM + "&" +
            "openid.return_to=http://" + REALM_PARAM + "/signin/";

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);

        final WebView webView = findViewById(R.id.wb_activity_log);
        webView.getSettings().setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                //checks the url being loaded
                try {
                    if (url != null) {
                        setTitle(url);
                        Uri Url = Uri.parse(url);
                        if (Url.getAuthority() != null)
                            if (Url.getAuthority().equals(REALM_PARAM.toLowerCase())) {
                                // That means that authentication is finished and the url contains user's id.
                                webView.stopLoading();
                                // Extracts user id.
                                Uri userAccountUrl = Uri.parse(Url.getQueryParameter("openid.identity"));
                                String userId = userAccountUrl.getLastPathSegment();
                                PreferenceManager.getDefaultSharedPreferences(LogInActivity.this).edit().putString(USER_ID, userId).apply();
                                Intent intent = getIntent();
                                intent.putExtra(USER_ID, userId);
                                setResult(RESULT_OK, intent);
                                finish();
                            }
                    }
                } catch (NullPointerException e) {
                    e.printStackTrace();
                    finish();
                }

            }
        });
        // Constructing openid url request
        webView.loadUrl(LOGIN_URL);
    }
}
