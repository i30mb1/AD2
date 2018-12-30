package n7.ad2.activity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import n7.ad2.R;

@SuppressLint("Registered")
public class BaseActivity extends AppCompatActivity {
    public static final String THEME_GRAY = "GRAY";
    public static final String THEME_WHITE = "WHITE";
    public static final String THEME_DARK = "DARK";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        switch (PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.setting_theme_key), THEME_GRAY)) {
            default:
            case THEME_GRAY:
                setTheme(R.style.AD2Theme);
                break;
            case THEME_WHITE:
                setTheme(R.style.AD2Theme_White);
                break;
            case THEME_DARK:
                setTheme(R.style.AD2Theme_Black);
                break;
        }
        super.onCreate(savedInstanceState);
    }

}


