package n7.ad2.data.source.local

import android.app.Application
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AppPreference @Inject constructor(
    application: Application,
) {

    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { _ -> emptyPreferences() },
    ) {
        application.preferencesDataStoreFile("appPreference")
    }

    private val dateKey = intPreferencesKey("date")

    suspend fun saveDate(date: Int) {
        dataStore.edit { preferences ->
            preferences[dateKey] = date
        }
    }

    fun getDate(): Flow<Int> {
        return dataStore.data.map { preferences ->
            preferences[dateKey] ?: 0
        }
    }

}