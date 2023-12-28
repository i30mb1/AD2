package n7.ad2

import android.content.res.Configuration
import android.os.Build
import n7.ad2.common.jvm.LazyComponentHolder

private class AD2AppInformation(
    res: Resources,
) : AppInformation {
    override val isDebug = BuildConfig.DEBUG
    override val appLocale: AppLocale = when (val value = res.getString(n7.ad2.core.common.android.R.string.locale)) {
        "RU" -> AppLocale.Russian
        else -> AppLocale.English
    }
    override val appVersion = BuildConfig.VERSION_NAME
    override val isNightMode: Boolean = (res.getConfiguration() as Configuration).isNightModeActive
}

fun AppInformation(res: Resources): AppInformation {
    "${Build.MANUFACTURER} ${Build.MODEL}"
    return AD2AppInformation(res)
}

object AppInformationHolder : LazyComponentHolder<AppInformation>()
