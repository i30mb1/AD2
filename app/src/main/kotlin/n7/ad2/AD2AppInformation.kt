package n7.ad2

import android.content.res.Configuration
import javax.inject.Inject

class AD2AppInformation @Inject constructor(
    res: Resources,
) : AppInformation {
    override val isDebug = BuildConfig.DEBUG
    override val appLocale: AppLocale = when (val value = res.getString(n7.ad2.commonandroid.R.string.locale)) {
        "RU" -> AppLocale.Russian
        else -> AppLocale.English
    }
    override val appVersion = BuildConfig.VERSION_NAME
    override val isNightMode: Boolean = (res.getConfiguration() as Configuration).isNightModeActive

}