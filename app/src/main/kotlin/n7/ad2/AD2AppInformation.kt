package n7.ad2

import android.content.res.Configuration
import n7.ad2.common.jvm.LazyComponentHolder

private class AD2AppInformation(
    res: Resources,
) : AppInformation {
    override val isDebug = BuildConfig.DEBUG
    override val appLocale: AppLocale = when (val value = res.getString(n7.ad2.core.commonandroid.R.string.locale)) {
        "RU" -> AppLocale.Russian
        else -> AppLocale.English
    }
    override val appVersion = BuildConfig.VERSION_NAME
    override val isNightMode: Boolean = (res.getConfiguration() as Configuration).isNightModeActive
}

fun AppInformation(res: Resources): AppInformation {
    return AD2AppInformation(res)
}

object AppInformationHolder : LazyComponentHolder<AppInformation>()
