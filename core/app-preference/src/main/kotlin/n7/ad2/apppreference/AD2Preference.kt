package n7.ad2.apppreference

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
import n7.ad2.apppreference.domain.usecase.GetCurrentDayUseCase

// https://proandroiddev.com/kotlin-property-delegates-for-datastore-preferences-library-5d4e1cdb609b
class AD2Preference constructor(
    application: Application,
    getCurrentDayUseCase: GetCurrentDayUseCase,
) : Preference {

    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    ) {
        application.preferencesDataStoreFile("appPreference")
    }

    private val currentDay = getCurrentDayUseCase()

    private val dateKey = intPreferencesKey("date")
    private val fingerCoordinateEnabled = booleanPreferencesKey("fingerCoordinateEnabled")
    private val logWidgetEnabled = booleanPreferencesKey("logWidgetEnabled")
    private val settings = stringPreferencesKey("settings")
    private val settingsLastDayUpdate = intPreferencesKey("settingsLastDateUpdate")
    private val newsLastDateUpdate = intPreferencesKey("newsLastDateUpdate")

    override suspend fun isNeedToUpdateNews(): Boolean {
        return dataStore.data.first()[newsLastDateUpdate] != currentDay
    }

    override suspend fun saveUpdateNewsDate() {
        dataStore.edit { preference ->
            preference[newsLastDateUpdate] = currentDay
        }
    }

    override suspend fun isNeedToUpdateSettings(): Boolean {
        return dataStore.data.first()[settingsLastDayUpdate] != currentDay
    }

    override suspend fun saveSettings(data: String) {
        dataStore.edit { preferences ->
            preferences[settings] = data
            preferences[settingsLastDayUpdate] = currentDay
        }
    }

    override suspend fun getSettings(): String {
        return dataStore.data.first()[settings] ?: ""
    }

    override suspend fun saveDate(date: Int) {
        dataStore.edit { preferences -> preferences[dateKey] = date }
    }

    override suspend fun getDate(): Int {
        return dataStore.data.first()[dateKey] ?: 0
    }

    override suspend fun setFingerCoordinateEnabled(isEnabled: Boolean) {
        dataStore.edit { preferences -> preferences[fingerCoordinateEnabled] = isEnabled }
    }

    override suspend fun isFingerCoordinateEnabled(): Boolean {
        return dataStore.data.first()[fingerCoordinateEnabled] ?: true
    }

    override suspend fun isLogWidgetEnabled(): Boolean {
        return dataStore.data.first()[logWidgetEnabled] ?: true
    }

}