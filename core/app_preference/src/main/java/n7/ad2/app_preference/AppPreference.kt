@file:Suppress("BlockingMethodInNonBlockingContext")

package n7.ad2.app_preference

import android.app.Application
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import n7.ad2.app_preference.domain.usecase.GetCurrentDayUseCase

// https://proandroiddev.com/kotlin-property-delegates-for-datastore-preferences-library-5d4e1cdb609b
class AppPreference constructor(
    application: Application,
    getCurrentDayUseCase: GetCurrentDayUseCase,
) {

    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    ) {
        application.preferencesDataStoreFile("appPreference")
    }

    private val currentDay = getCurrentDayUseCase()

    private val dateKey = intPreferencesKey("date")
    private val showFingerCoordinate = booleanPreferencesKey("fingerCoordinate")
    private val settings = stringPreferencesKey("settings")
    private val settingsLastDayUpdate = intPreferencesKey("settingsLastDateUpdate")

    suspend fun isNeedToUpdateSettings(): Boolean {
        return dataStore.data.first()[settingsLastDayUpdate] != currentDay
    }

    suspend fun saveSettings(data: String) {
        dataStore.edit { preferences ->
            preferences[settings] = data
            preferences[settingsLastDayUpdate] = currentDay
        }
    }

    suspend fun getSettings(): String {
        return dataStore.data.first()[settings] ?: ""
    }

    suspend fun saveDate(date: Int) {
        dataStore.edit { preferences -> preferences[dateKey] = date }
    }

    suspend fun getDate(): Int {
        return dataStore.data.first()[dateKey] ?: 0
    }

    suspend fun setShowFingerCoordinate(show: Boolean) {
        dataStore.edit { preferences -> preferences[showFingerCoordinate] = show }
    }

    suspend fun isShowFingerCoordinate(): Boolean {
        return dataStore.data.first()[showFingerCoordinate] ?: true
    }

}