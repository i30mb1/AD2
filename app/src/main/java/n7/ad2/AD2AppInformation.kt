package n7.ad2

import javax.inject.Inject

class AD2AppInformation @Inject constructor(
    res: AppResources,
) : AppInformation {
    override val isDebug = BuildConfig.DEBUG
    override val appLocale: AppLocale = when (val value = res.getString(n7.ad2.android.R.string.locale)) {
        "ru" -> AppLocale.Russian(value)
        else -> AppLocale.English(value)
    }
}