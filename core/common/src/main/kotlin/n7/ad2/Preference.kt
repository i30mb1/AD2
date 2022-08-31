package n7.ad2

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

class PreferenceFake : Preference {
    override suspend fun isNeedToUpdateSettings(): Boolean = false
    override suspend fun saveSettings(data: String) = Unit
    override suspend fun getSettings(): String = ""
    override suspend fun saveDate(date: Int) = Unit
    override suspend fun getDate(): Int = 0
    override suspend fun setFingerCoordinateEnabled(isEnabled: Boolean) = Unit
    override suspend fun isFingerCoordinateEnabled(): Boolean = true
    override suspend fun isLogWidgetEnabled(): Boolean = true
}