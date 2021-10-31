package n7.ad2.data.source.local

import android.app.Application
import androidx.datastore.core.handlers.ReplaceFileCorruptionHandler
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.emptyPreferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStoreFile
import kotlinx.coroutines.flow.first
import javax.inject.Inject
import javax.inject.Singleton

// https://proandroiddev.com/kotlin-property-delegates-for-datastore-preferences-library-5d4e1cdb609b
@Singleton
class AppPreference @Inject constructor(
    application: Application,
) {

    private val dataStore = PreferenceDataStoreFactory.create(
        corruptionHandler = ReplaceFileCorruptionHandler { emptyPreferences() },
    ) {
        application.preferencesDataStoreFile("appPreference")
    }

    private val dateKey = intPreferencesKey("date")
    private val showFingerCoordinate = booleanPreferencesKey("fingerCoordinate")

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