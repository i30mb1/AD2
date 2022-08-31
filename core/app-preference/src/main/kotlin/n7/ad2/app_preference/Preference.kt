package n7.ad2.app_preference

interface Preference {
    suspend fun isNeedToUpdateSettings(): Boolean
    suspend fun saveSettings(data: String)
    suspend fun getSettings(): String
    suspend fun saveDate(date: Int)
    suspend fun getDate(): Int
    suspend fun setFingerCoordinateEnabled(isEnabled: Boolean)
    suspend fun isFingerCoordinateEnabled(): Boolean
    suspend fun isLogWidgetEnabled(): Boolean
}