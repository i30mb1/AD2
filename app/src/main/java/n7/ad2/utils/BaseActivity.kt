package n7.ad2.utils

import android.annotation.SuppressLint
import android.os.Bundle
import android.preference.PreferenceManager
import androidx.appcompat.app.AppCompatActivity
import n7.ad2.R
import n7.ad2.ui.setting.domain.model.Theme

@SuppressLint("Registered")
open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        when (PreferenceManager.getDefaultSharedPreferences(this).getString(getString(R.string.setting_theme_key), "")) {
            Theme.RED.key -> setTheme(R.style.AD2Theme)
            Theme.BLUE.key -> setTheme(R.style.AD2Theme_White)
            Theme.PURPLE.key -> setTheme(R.style.AD2Theme_Black)
            else -> setTheme(R.style.AD2Theme)
        }
        super.onCreate(savedInstanceState)
    }

}