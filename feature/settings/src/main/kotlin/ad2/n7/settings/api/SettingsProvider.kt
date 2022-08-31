package ad2.n7.settings.api

import ad2.n7.settings.internal.SettingsFragment
import androidx.fragment.app.Fragment
import n7.ad2.provider.api.SettingsApi

class SettingsProvider : SettingsApi {
    override fun getFragment(): Fragment = SettingsFragment.getInstance()
}