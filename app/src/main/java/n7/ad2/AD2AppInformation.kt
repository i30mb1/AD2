package n7.ad2

import javax.inject.Inject

class AD2AppInformation @Inject constructor(
    res: AppResources,
) : AppInformation {
    override val isDebug = BuildConfig.DEBUG
    override val appLocale: AppLocale = AppLocale.valueOf(res.getString(n7.ad2.android.R.string.locale))
}