package n7.ad2.settings.api

import androidx.fragment.app.Fragment
import n7.ad2.provider.api.SettingsApi
import n7.ad2.settings.internal.SettingsFragment

class SettingsProvider : SettingsApi {
    override fun getFragment(): Fragment = SettingsFragment.getInstance()
}